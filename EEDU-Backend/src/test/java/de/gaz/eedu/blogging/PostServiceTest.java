package de.gaz.eedu.blogging;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.exception.OccupiedException;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

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
    protected @NotNull Eval successEval()
    {
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
                "batchfile1.txt",
                "burger king",
                System.currentTimeMillis(),
                new String[]{"NONE"},
                new String[]{"NONE"},
                new String[]{"test"});

        return Eval.eval(createModel, postModel, (request, expect, result) -> {
            Assertions.assertEquals(createModel.author(), postModel.author());
            Assertions.assertEquals(createModel.title(), postModel.title());
            Assertions.assertArrayEquals(createModel.readPrivileges(), postModel.readPrivileges());
            Assertions.assertArrayEquals(createModel.editPrivileges(), postModel.editPrivileges());
            Assertions.assertArrayEquals(createModel.tags(), postModel.tags());
        });
    }

    @NotNull
    @Override
    protected PostCreateModel occupiedCreateModel()
    {
        // TODO
        throw new OccupiedException();
    }
}
