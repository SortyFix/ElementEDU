package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.entity.model.EntityObject;
import de.gaz.eedu.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity @Getter @Setter
public class PostEntity implements EntityObject, EntityModelRelation<PostModel>
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private UserEntity author;
    private String title;
    private String body;
    private Long timeOfCreation;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_user_privileges", joinColumns = @JoinColumn(name = "post_id"))
    private Set<String> privileges;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    private Set<String> tags;

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity that = (PostEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(author, that.author) && Objects.equals(title,
                that.title) && Objects.equals(body, that.body) && Objects.equals(timeOfCreation,
                that.timeOfCreation) && Objects.equals(privileges, that.privileges) && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, author, title, body, timeOfCreation, privileges, tags);
    }

    @Override
    public PostModel toModel()
    {
        return new PostModel(id, author.getId(), title, body, timeOfCreation,
                privileges.toArray(String[]::new), tags.toArray(String[]::new));
    }
}

