package de.gaz.eedu.blogging;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/blog") public class PostController
{
    private final PostService postService;
    private final PostRepository postRepository;

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{postId}") public ResponseEntity<PostModel> getPost(@NotNull @PathVariable Long postId)
    {
        return ResponseEntity.ok(postService.getModel(postId));
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()") @GetMapping("/get/length") public ResponseEntity<Long> getLength()
    {
        return ResponseEntity.ok(postService.getLength());
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()") @GetMapping("/get/list") public ResponseEntity<PostModel[]> getPostList(@NotNull @RequestParam Integer pageNumber)
    {
        return ResponseEntity.ok(postService.getPostList(pageNumber));
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()") @PostMapping("/post")
    public ResponseEntity<PostModel> createPost(
            @AuthenticationPrincipal Long userId,
            @NotNull @RequestPart("createModel") PostCreateModel createModel,
            @Nullable @RequestPart(value = "multipartFile", required = false) MultipartFile multipartFile)
    {
        return ResponseEntity.ok(postService.createPost(userId, multipartFile, createModel));
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()") @PostMapping("/edit/{postId}") public ResponseEntity<PostModel> editPost(@AuthenticationPrincipal Long userId, @RequestBody PostCreateModel createModel, @PathVariable Long postId)
    {
        if(postService.userHasEditAuthority(userId, postId))
        {
            return ResponseEntity.ok(postService.editModel(postId, createModel.author(), createModel.title(), createModel.body()));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()") @PostMapping("/edit/{postId}/attach/tags") public ResponseEntity<Boolean> attachTags(@AuthenticationPrincipal Long userId, @PathVariable Long postId, @NotNull @RequestBody String... tags)
    {
        if(postService.userHasEditAuthority(userId, postId))
        {
            return ResponseEntity.ok(postRepository.getReferenceById(postId).attachTags(postService, tags));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    @PostMapping("/edit/{postId}/detach/tags")
    public ResponseEntity<Boolean> removeTags(@AuthenticationPrincipal Long userId, @PathVariable Long postId, @NotNull @RequestBody String... tags)
    {
        if (postService.userHasEditAuthority(userId, postId))
        {
            return ResponseEntity.ok(postRepository.getReferenceById(postId).detachTags(postService, tags));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    @PostMapping("/edit/{postId}/attach/editPrivileges")
    public ResponseEntity<Boolean> addEditPrivileges(@AuthenticationPrincipal Long userId, @PathVariable Long postId, @NotNull @RequestBody String... privileges)
    {
        if (postService.userHasEditAuthority(userId, postId))
        {
            return ResponseEntity.ok(postRepository.getReferenceById(postId).attachEditPrivileges(postService, privileges));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()")
    @PostMapping("/edit/{postId}/detach/editPrivileges")
    public ResponseEntity<Boolean> removeEditPrivileges(@AuthenticationPrincipal Long userId, @PathVariable Long postId, @NotNull @RequestBody String... privileges)
    {
        if (postService.userHasEditAuthority(userId, postId))
        {
            return ResponseEntity.ok(postRepository.getReferenceById(postId).detachEditPrivileges(postService, privileges));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()") @PostMapping("/editThumbnail") public ResponseEntity<PostModel> editThumbnail(@AuthenticationPrincipal Long userId, @NotNull @RequestBody Long postId, @NotNull @RequestPart MultipartFile newThumbnail) throws IOException
    {
        if(postService.userHasEditAuthority(userId, postId))
        {
            return ResponseEntity.ok(postService.editThumbnail(userId, newThumbnail));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("@verificationService.isFullyAuthenticated()") @DeleteMapping("/delete/{postId}") public void deletePost(@AuthenticationPrincipal Long userId, @NotNull @PathVariable Long postId)
    {
        if(postService.userHasEditAuthority(userId, postId))
        {
            postService.deleteEntity(postId);
            return;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
