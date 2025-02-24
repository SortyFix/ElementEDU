package de.gaz.eedu.course;

import de.gaz.eedu.course.classroom.ClassRoomRepository;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subject.SubjectService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.entity.model.CreationFactory;
import de.gaz.eedu.exception.AccountTypeMismatch;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.file.FileCreateModel;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
import de.gaz.eedu.user.AccountType;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.model.ReducedUserModel;
import de.gaz.eedu.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Getter(AccessLevel.PROTECTED)
public class CourseService extends EntityService<Long, CourseRepository, CourseEntity, CourseModel, CourseCreateModel>
{
    private final CourseRepository repository;
    private final SubjectService subjectService;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRepository;
    private final FileService fileService;

    public @NotNull CourseModel[] getCourses(long user)
    {
        return getRepository().findAllByUserId(user).stream().map(CourseEntity::toModel).toArray(CourseModel[]::new);
    }

    public @NotNull @Unmodifiable Set<ReducedUserModel> loadReducedModelsByCourse(long course)
    {
        return getRepository().findAllReducedUsersByCourse(course);
    }

    @Transactional @Override
    public @NotNull List<CourseEntity> createEntity(@NotNull Set<CourseCreateModel> model) throws CreationException
    {
        if (getRepository().existsByNameIn(model.stream().map(CourseCreateModel::name).toList()))
        {
            throw new OccupiedException();
        }

        List<CourseEntity> courseEntities = model.stream().map(courseModel ->
        {
            CourseEntity course = new CourseEntity(new FileCreateModel("", new String[0], new String[0]).toEntity(new FileEntity()));
            return courseModel.toEntity(course, this.courseFactory(courseModel));
        }).toList();

        // create repositories first
        getFileService().getRepository().saveAll(courseEntities.stream().map(CourseEntity::getRepository).toList());
        return saveEntity(courseEntities);
    }

    private @NotNull CreationFactory<Long, CourseEntity> courseFactory(@NotNull CourseCreateModel createModel)
    {
        return (entity) ->
        {
            entity.setTeacher(fetchTeacher(createModel.teacher()));
            if (createModel.students().length > 0)
            {
                List<UserEntity> students = getUserRepository().findAllById(List.of(createModel.students()));
                entity.attachStudents(students.toArray(UserEntity[]::new));
            }

            if (Objects.nonNull(createModel.classroom()))
            {
                String classroom = createModel.classroom();
                getClassRepository().findById(classroom).ifPresentOrElse(entity::linkClassRoom, () -> {
                    throw entityUnknown(classroom).get();
                });
            }

            entity.setSubject(getSubjectService().loadEntityByIDSafe(createModel.subject()));
            return entity;
        };
    }

    private @NotNull UserEntity fetchTeacher(long teacherId) throws ResponseStatusException
    {
        UserEntity teacher = getUserRepository().findEntity(teacherId).orElseThrow(entityUnknown(teacherId));
        if (!Objects.equals(teacher.getAccountType(), AccountType.TEACHER))
        {
            throw new AccountTypeMismatch(AccountType.TEACHER, teacher.getAccountType());
        }
        return teacher;
    }
}
