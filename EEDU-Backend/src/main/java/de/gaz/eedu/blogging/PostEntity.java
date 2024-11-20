package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.entity.model.EntityObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Entity @Getter @Setter
public class PostEntity implements EntityObject, EntityModelRelation<PostModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String author;
    private String title;
    private String thumbnailURL;
    private String body;
    private Long timeOfCreation;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_user_read_privileges", joinColumns = @JoinColumn(name = "post_id"))
    private final Set<String> readPrivileges = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_user_edit_privileges", joinColumns = @JoinColumn(name = "post_id"))
    private final Set<String> editPrivileges = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    private final Set<String> tags = new HashSet<>();

    public boolean attachEditPrivileges(@NotNull PostService service, @NotNull String... privileges)
    {
        return updateDatabase(service, privileges, this::attachEditPrivileges);
    }

    public boolean attachEditPrivileges(@NotNull String... privileges)
    {
        return this.editPrivileges.addAll(Set.of(privileges));
    }

    public boolean detachEditPrivileges(@NotNull PostService service, @NotNull String... privileges)
    {
        return updateDatabase(service, privileges, this::detachEditPrivileges);
    }

    public boolean detachEditPrivileges(@NotNull String... privileges)
    {
        return this.editPrivileges.removeAll(Set.of(privileges));
    }

    public boolean attachReadPrivileges(@NotNull PostService service, @NotNull String... privileges)
    {
        return updateDatabase(service, privileges, this::attachReadPrivileges);
    }

    public boolean attachReadPrivileges(@NotNull String... privileges)
    {
        return this.readPrivileges.addAll(Set.of(privileges));
    }

    public boolean detachReadPrivileges(@NotNull PostService service, @NotNull String... privileges)
    {
        return updateDatabase(service, privileges, this::detachReadPrivileges);
    }

    public boolean detachReadPrivileges(@NotNull String... privileges)
    {
        return this.readPrivileges.removeAll(Set.of(privileges));
    }

    public boolean attachTags(@NotNull PostService service, @NotNull String... tags)
    {
        return updateDatabase(service, tags, this::attachTags);
    }

    public boolean attachTags(@NotNull String... tags)
    {
        return this.tags.addAll(Set.of(tags));
    }

    public boolean detachTags(@NotNull PostService service, @NotNull String... tags)
    {
        return updateDatabase(service, tags, this::detachTags);
    }

    public boolean detachTags(@NotNull String... privileges)
    {
        return this.tags.removeAll(Set.of(privileges));
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity that = (PostEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    @Override @NotNull public PostModel toModel()
    {
        String encodedThumbnail = encode();
        return new PostModel(id, author, title, encodedThumbnail, body, timeOfCreation,
                    readPrivileges.toArray(String[]::new), editPrivileges.toArray(String[]::new), tags.toArray(String[]::new));
    }

    public @NotNull String encode()
    {
        try
        {
            byte[] fileContent = Files.readAllBytes(Path.of(thumbnailURL));
            return Base64.getEncoder().encodeToString(fileContent);
        }
        catch(IOException ioException)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Not found: " + thumbnailURL, ioException);
        }
    }

    private <T> boolean updateDatabase(@NotNull PostService userService, @NotNull T entity, @NotNull Predicate<T> predicate)
    {
        if (predicate.test(entity))
        {
            userService.saveEntity(this);
            return true;
        }
        return false;
    }
}

