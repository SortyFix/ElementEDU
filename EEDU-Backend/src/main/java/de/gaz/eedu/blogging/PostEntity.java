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
import java.util.Objects;
import java.util.Set;

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
    private Set<String> readPrivileges;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_user_edit_privileges", joinColumns = @JoinColumn(name = "post_id"))
    private Set<String> editPrivileges;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    private Set<String> tags;

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
        return Objects.hash(id, author, title, body, timeOfCreation, readPrivileges, editPrivileges, tags);
    }

    @Override
    public PostModel toModel()
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
}

