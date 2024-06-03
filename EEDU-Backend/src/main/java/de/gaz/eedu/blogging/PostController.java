package de.gaz.eedu.blogging;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController @RequiredArgsConstructor @RequestMapping(value = "/blog") public class PostController
{
    private final PostService postService;

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{postId}") public PostModel getPost(@AuthenticationPrincipal Long userId, @NotNull @PathVariable Long postId)
    {
        if(postService.userHasReadAuthority(userId, postId))
        {
            return postService.getModel(userId, postId);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("hasAuthority(${blog.write})") @PostMapping("/post") public PostModel createPost(@AuthenticationPrincipal Long userId, @NotNull String author, @NotNull String title, @NotNull MultipartFile thumbnail, @NotNull String body,
            @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags)
    {
        return postService.createPost(userId, author, title, thumbnail, body, readPrivileges, editPrivileges, tags);
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/edit") public PostModel editPost(@AuthenticationPrincipal Long userId, @NotNull Long postId, @NotNull String author, @NotNull String title, @NotNull String body,
            @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags)
    {
        if(postService.userHasEditAuthority(userId, postId))
        {
            return postService.editModel(userId, postId, author, title, body, readPrivileges, editPrivileges, tags);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/editThumbnail") public PostModel editThumbnail(@AuthenticationPrincipal Long userId, @NotNull Long postId, @NotNull MultipartFile newThumbnail) throws IOException
    {
        if(postService.userHasEditAuthority(userId, postId))
        {
            return postService.editThumbnail(userId, postId, newThumbnail);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("isAuthenticated()") @DeleteMapping("/delete") public void deletePost(@AuthenticationPrincipal Long userId, @NotNull Long postId)
    {
        if(postService.userHasEditAuthority(userId, postId))
        {
            postService.deleteEntity(userId, postId);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
