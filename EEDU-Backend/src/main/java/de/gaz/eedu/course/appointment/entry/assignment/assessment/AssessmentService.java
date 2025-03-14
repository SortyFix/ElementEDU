package de.gaz.eedu.course.appointment.entry.assignment.assessment;

import de.gaz.eedu.course.appointment.entry.AppointmentEntryEntity;
import de.gaz.eedu.course.appointment.entry.AppointmentEntryRepository;
import de.gaz.eedu.course.appointment.entry.assignment.assessment.model.AssessmentCreateModel;
import de.gaz.eedu.course.appointment.entry.assignment.assessment.model.AssessmentModel;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service @Getter(AccessLevel.PROTECTED) @RequiredArgsConstructor
public class AssessmentService extends EntityService<Long, AssessmentRepository, AssessmentEntity, AssessmentModel, AssessmentCreateModel>
{
    private final AssessmentRepository repository;
    private final AppointmentEntryRepository appointmentRepository;
    private final UserRepository userRepository;

    @Contract(pure = true, value = "_, _ -> _")
    private static long generateId(long entity, long user)
    {
        // okay so, this is a bit complex, so I'll break it down
        // Firstly, I bitshift the appointmentEntryId into the lower 32 bits, because a long is 64 bits in total

        // Secondly, I shift the userid into the 32 remaining bits
        // The value 0xFFFFFFFFFL is a 32-bit mask in hexadecimal format

        // The 0x indicates it's a hexadecimal number, where each F are 4 bits (there are 8),
        // making up a total of 32 bits. The L suffix defines that the value is of type Long

        // This allows me to encode both the appointment id and the userid into a single id
        return (entity << 32) | (user & 0xFFFFFFFFL);
    }

    public long getId(long entity, long user)
    {
        return generateId(entity, user);
    }

    @Transactional @Override
    public @NotNull List<AssessmentEntity> createEntity(@NotNull Set<AssessmentCreateModel> model) throws CreationException
    {
        return saveEntity(model.stream().map(current -> {
            long aId = current.appointment();
            AppointmentEntryEntity appointment = getAppointmentRepository().findById(aId).orElseThrow(entityUnknown(aId));
            UserEntity user = getUserRepository().findById(current.user()).orElseThrow(entityUnknown(current.user()));
            return current.toEntity(new AssessmentEntity(generateId(appointment.getId(), user.getId()), appointment, user));
        }).toList());
    }

    @Transactional
    public @NotNull AssessmentModel setGrade(long assessment, @Nullable Float grade)
    {
        AssessmentEntity assessmentEntity = loadEntityByIDSafe(assessment);

        if (Objects.equals(assessmentEntity.getGrade(), grade))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        assessmentEntity.setGrade(grade);
        return saveEntity(assessmentEntity).toModel();
    }

    @Transactional
    public @NotNull AssessmentModel setFeedback(long assessment, @Nullable String feedback)
    {
        AssessmentEntity assessmentEntity = loadEntityByIDSafe(assessment);

        if (Objects.equals(assessmentEntity.getFeedback(), feedback))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        assessmentEntity.setFeedback(feedback);
        return saveEntity(assessmentEntity).toModel();
    }
}
