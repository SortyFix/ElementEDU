package de.gaz.eedu.course.room;

import de.gaz.eedu.course.room.model.RoomCreateModel;
import de.gaz.eedu.course.room.model.RoomModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service @Getter(AccessLevel.PROTECTED) @RequiredArgsConstructor
public class RoomService extends EntityService<RoomRepository, RoomEntity, RoomModel, RoomCreateModel>
{
    private final RoomRepository repository;

    @Transactional @Override
    public @NotNull List<RoomEntity> createEntity(@NotNull Set<RoomCreateModel> model) throws CreationException
    {
        if (getRepository().existsByNameIn(model.stream().map(RoomCreateModel::name).toList()))
        {
            throw new OccupiedException();
        }

        return saveEntity(model.stream().map(current -> current.toEntity(new RoomEntity())).toList());
    }
}
