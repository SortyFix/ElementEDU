package de.gaz.eedu.blogging;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.exception.OccupiedException;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

@Getter(AccessLevel.PROTECTED)
public class PostServiceTest extends ServiceTest<PostService, PostEntity, PostModel, PostCreateModel>
{

    @Autowired private PostService postService;

    @Override
    protected @NotNull PostService getService()
    {
        return postService;
    }

    @Override
    protected @NotNull Eval<PostCreateModel, PostModel> successEval() throws IOException, URISyntaxException
    {
        String encodedFile = "VGhpcmQgZWxlbWVudAo=";
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        PostCreateModel createModel = new PostCreateModel(
                "Ivo",
                "10 Reasons for why",
                Objects.requireNonNull(classloader.getResource("batchfile1.txt")).getPath(),
                "burger king",
                new String[]{"NONE"},
                new String[]{"NONE"},
                new String[]{"test"}
        );

        PostModel postModel = new PostModel(2L, "Ivo",
                "10 Reasons for why",
                encodedFile,
                "burger king",
                System.currentTimeMillis(),
                new String[]{"NONE"},
                new String[]{"NONE"},
                new String[]{"test"});

        return Eval.eval(createModel, postModel, (request, expect, result) -> {
            Assertions.assertEquals(createModel.author(), postModel.author());
            Assertions.assertEquals(createModel.title(), postModel.title());
            Assertions.assertEquals(createModel.toEntity(new PostEntity()).toModel().thumbnailBlob(), encodedFile);
            Assertions.assertArrayEquals(createModel.readPrivileges(), postModel.readPrivileges());
            Assertions.assertArrayEquals(createModel.editPrivileges(), postModel.editPrivileges());
            Assertions.assertArrayEquals(createModel.tags(), postModel.tags());
        });
    }

    @Test
    protected void testCollectionEditing()
    {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        PostEntity entity = getPostService().createEntity(new PostCreateModel(
                "Ivo",
                "10 Reasons for why",
                Objects.requireNonNull(classloader.getResource("batchfile1.txt")).getPath(),
                "burger king",
                new String[]{"Read1", "Read2", "Read3"},
                new String[]{"Edit1", "Edit2", "Edit3"},
                new String[]{"Tag1", "Tag2", "Tag3"}
        ));

        entity.attachTags(getService(),"Tag4", "Tag5");

        Assertions.assertEquals(5, entity.getTags().size());
    }

    @NotNull
    @Override
    protected PostCreateModel occupiedCreateModel()
    {
        throw new OccupiedException();
    }
}
