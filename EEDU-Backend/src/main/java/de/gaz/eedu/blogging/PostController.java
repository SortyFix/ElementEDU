package de.gaz.eedu.blogging;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor @RequestMapping(value = "/blog") public class PostController
{
    private PostService postService;

    @PreAuthorize("isAuthenticated()") @GetMapping("/get") public PostModel getPost(@AuthenticationPrincipal Long userId, @NotNull Long postId)
    {
        return postService.getModel(userId, postId);
    }
}
