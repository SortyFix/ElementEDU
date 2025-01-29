package de.gaz.eedu.course;

import de.gaz.eedu.course.classroom.ClassRoomRepository;
import de.gaz.eedu.course.model.CourseCreateModel;
import de.gaz.eedu.course.model.CourseModel;
import de.gaz.eedu.course.subject.SubjectService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.file.FileCreateModel;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor @Service @Getter(AccessLevel.PROTECTED)
public class CourseService extends EntityService<CourseRepository, CourseEntity, CourseModel, CourseCreateModel>
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

    @Transactional @Override public @NotNull List<CourseEntity> createEntity(@NotNull Set<CourseCreateModel> model) throws CreationException
    {
        if(getRepository().existsByNameIn(model.stream().map(CourseCreateModel::name).toList()))
        {
            throw new OccupiedException();
        }

        Set<FileEntity> repositories = new HashSet<>(model.size());
        List<CourseEntity> courseEntities = model.stream().map(clazzModel ->
        {
            //TODO Yonas: please add a way of creating entities without instantly saving them
            FileCreateModel file = new FileCreateModel("", new String[0], new String[0]);
            FileEntity fileEntity = file.toEntity(new FileEntity());
            repositories.add(fileEntity);

            return clazzModel.toEntity(new CourseEntity(fileEntity), (entity) ->
            {
                // attach users
                if (clazzModel.users().length > 0)
                {
                    List<UserEntity> userEntities = getUserRepository().findAllById(List.of(clazzModel.users()));
                    entity.attachUsers(userEntities.toArray(UserEntity[]::new));
                }

                // assign class
                if (clazzModel.classId() != null)
                {
                    getClassRepository().findById(clazzModel.classId()).ifPresentOrElse(entity::linkClassRoom, () ->
                    {
                        throw new EntityUnknownException(clazzModel.classId());
                    });
                }

                // add to subject
                entity.setSubject(getSubjectService().loadEntityByIDSafe(clazzModel.subjectId()));
                return entity;
            });
        }).toList();

        // create repositories first
        getFileService().getRepository().saveAll(repositories);
        return saveEntity(courseEntities);
    }
}
