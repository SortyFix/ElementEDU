package de.gaz.eedu.course.room;

import de.gaz.eedu.course.room.model.RoomCreateModel;
import de.gaz.eedu.course.room.model.RoomModel;
import de.gaz.eedu.entity.EntityController;
import de.gaz.eedu.exception.CreationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/course/room")
@Getter(AccessLevel.PROTECTED) @RequiredArgsConstructor
public class RoomController extends EntityController<String, RoomService, RoomModel, RoomCreateModel>
{
    private final RoomService service;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROOM_CREATE')")
    @Override public @NotNull ResponseEntity<RoomModel[]> create(@NotNull @RequestBody RoomCreateModel[] model) throws CreationException
    { return super.create(model); }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROOM_DELETE')")
    @Override public @NotNull ResponseEntity<Void> delete(@NotNull @PathVariable String[] id)
    {
        return super.delete(id);
    }

    @GetMapping("/get/all")
    @Override public @NotNull ResponseEntity<Set<RoomModel>> fetchAll() { return super.fetchAll(); }
}
