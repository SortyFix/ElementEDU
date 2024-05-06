package de.gaz.eedu.blogging;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequiredArgsConstructor @RequestMapping(value = "/blog") public class PostController
{
    private PostService postService;

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{postId}") public PostModel getPost(@AuthenticationPrincipal Long userId, @NotNull @PathVariable Long postId)
    {
        return postService.getModel(userId, postId);
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/post") public PostModel createPost(@AuthenticationPrincipal Long userId, @NotNull String author, @NotNull String title, @NotNull MultipartFile thumbnail, @NotNull String body,
            @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags)
    {
        return postService.createPost(userId, author, title, thumbnail, body, readPrivileges, editPrivileges, tags);
    }

    @PreAuthorize("isAuthenticated()") @DeleteMapping("/delete") public void deletePost(@AuthenticationPrincipal Long userId, @NotNull Long postId)
    {
        postService.deleteEntity(userId, postId);
    }
}
