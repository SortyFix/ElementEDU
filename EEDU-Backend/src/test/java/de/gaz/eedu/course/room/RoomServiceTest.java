package de.gaz.eedu.course.room;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.course.room.model.RoomCreateModel;
import de.gaz.eedu.course.room.model.RoomModel;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;

@Getter(AccessLevel.PROTECTED)
public class RoomServiceTest extends ServiceTest<String, RoomService, RoomEntity, RoomModel, RoomCreateModel>
{
    @Autowired private RoomService service;

    @Override protected @NotNull Eval<RoomCreateModel, RoomModel> successEval() throws IOException, URISyntaxException
    {
        return Eval.eval(new RoomCreateModel("402"), new RoomModel("402"), (request, expect, result) -> Assertions.assertEquals(expect.id(), result.id()));
    }

    @Override protected @NotNull RoomCreateModel occupiedCreateModel()
    {
        return new RoomCreateModel("403");
    }

    @Override protected @NotNull TestData<String, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[] {
                new TestData<>("406", true),
                new TestData<>("407", false),
        };
    }
}
