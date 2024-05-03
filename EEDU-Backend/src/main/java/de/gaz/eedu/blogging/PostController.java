package de.gaz.eedu.blogging;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor @RequestMapping(value = "/blog") public class PostController
{
    private PostService postService;

    @PreAuthorize("isAuthenticated()") @GetMapping("/get") public PostModel getPost(@AuthenticationPrincipal Long userId, @NotNull Long postId)
    {

        return postService.getModel(userId, postId);
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/post") public PostModel createPost(@AuthenticationPrincipal Long userId, @NotNull String author, @NotNull String title, @NotNull String thumbnailURL, @NotNull String body,
            @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags)
    {
        return postService.createEntity(new PostCreateModel(author, title, thumbnailURL, body, readPrivileges, editPrivileges, tags)).toModel();
    }
}
