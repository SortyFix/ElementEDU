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
        return Eval.eval(new RoomCreateModel("room9"), new RoomModel("room9"), (request, expect, result) -> Assertions.assertEquals(expect, result));
    }

    @Override protected @NotNull RoomCreateModel occupiedCreateModel()
    {
        return new RoomCreateModel("room0");
    }

    @Override protected @NotNull TestData<String, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[] {
                new TestData<>("room9", true),
                new TestData<>("room10", false),
        };
    }
}
