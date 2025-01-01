package de.gaz.eedu.course.room;

import de.gaz.eedu.course.room.model.RoomCreateModel;
import de.gaz.eedu.course.room.model.RoomModel;
import de.gaz.eedu.entity.EntityController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/course/room")
@Getter(AccessLevel.PROTECTED) @RequiredArgsConstructor
public class RoomController extends EntityController<RoomService, RoomModel, RoomCreateModel>
{
    private final RoomService service;

    @GetMapping("/all")
    @Override public @NotNull ResponseEntity<Set<RoomModel>> fetchAll()
    {
        return super.fetchAll();
    }
}
