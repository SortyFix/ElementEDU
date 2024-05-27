package de.gaz.eedu.blogging;

import de.gaz.eedu.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequiredArgsConstructor @RequestMapping(value = "/blog") public class PostController
{
    private final PostService postService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()") @GetMapping("/get/{postId}") public PostModel getPost(@AuthenticationPrincipal Long userId, @NotNull @PathVariable Long postId)
    {
        return postService.getModel(userId, postId);
    }

    @PreAuthorize("isAuthenticated()") @PostMapping("/post") public PostModel createPost(@AuthenticationPrincipal Long userId, @NotNull String author, @NotNull String title, @NotNull MultipartFile thumbnail, @NotNull String body,
            @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags)
    {
        if(userService.loadEntityByIDSafe(userId).hasAuthority("can.post"))
        {
            return postService.createPost(userId, author, title, thumbnail, body, readPrivileges, editPrivileges, tags);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("isAuthenticated()") @DeleteMapping("/delete") public void deletePost(@AuthenticationPrincipal Long userId, @NotNull Long postId)
    {
        postService.deleteEntity(userId, postId);
    }
}
