package de.gaz.eedu.user;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.classroom.ClassRoomService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.repository.GroupRepository;
import de.gaz.eedu.user.model.LoginModel;
import de.gaz.eedu.user.model.ReducedUserModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.repository.UserRepository;
import de.gaz.eedu.user.theming.ThemeRepository;
import de.gaz.eedu.user.verification.GeneratedToken;
import de.gaz.eedu.user.verification.VerificationService;
import de.gaz.eedu.user.verification.model.AdvancedUserLoginModel;
import de.gaz.eedu.user.verification.model.UserLoginModel;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class manages user related tasks.
 * <p>
 * Within this class there are method managing user related tasks such as loading users or creating them.
 * This is archived by accessing a {@link UserRepository} which is auto wired in the constructor using lombok.
 * <p>
 * The {@link Service} marks this class as a service to spring. This is necessary to use spring related features such
 * as the previously mentioned auto wiring.
 * <p>
 * Another important concept is, that all {@link UserEntity} related tasks occur here.
 * Outside the {@link UserModel} is the class to use.
 *
 * @author ivo
 * @see UserRepository
 * @see Service
 * @see AllArgsConstructor
 */
@Service
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
@Slf4j
public class UserService extends EntityService<UserRepository, UserEntity, UserModel, UserCreateModel> implements UserDetailsService
{
    @Getter private final VerificationService verificationService;
    @Getter private final ClassRoomService classRoomService;
    @Getter(AccessLevel.NONE) private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ThemeRepository themeRepository;

    @Override public @NotNull UserRepository getRepository()
    {
        return userRepository;
    }

    @Transactional @Override
    public @NotNull List<UserEntity> createEntity(@NotNull Set<UserCreateModel> model) throws CreationException
    {
        if (getRepository().existsByLoginNameIn(model.stream().map(UserCreateModel::loginName).toList()))
        {
            throw new OccupiedException();
        }

        return saveEntity(model.stream().map(current -> current.toEntity(new UserEntity(), entity ->
        {
            entity.setThemeEntity(themeRepository.getReferenceById(current.theme()));

            List<Long> ids = Arrays.asList(current.groups());
            entity.attachGroups(getGroupRepository().findAllById(ids).toArray(GroupEntity[]::new));
            return entity;
        })).toList());
    }

    @Transactional @Override
    public @NotNull UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException
    {
        return getRepository().findByLoginName(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override public void deleteRelations(@NotNull UserEntity entry)
    {
        // detach user from courses
        Set<CourseEntity> courses = entry.getCourses();
        courses.forEach(courseEntity -> courseEntity.detachUsers(entry.getId()));
        getClassRoomService().getCourseService().saveEntity(courses);

        // detach user from class
        entry.getClassRoom().ifPresent(clazz -> clazz.detachStudents(getClassRoomService(), entry.getId()));
    }

    @Transactional public @NotNull Optional<GeneratedToken> requestLogin(@NotNull LoginModel loginModel)
    {
        Optional<UserEntity> potentialUser = switch (loginModel)
        {
            case UserLoginModel userLoginModel -> getRepository().findByLoginName(userLoginModel.loginName());
            case AdvancedUserLoginModel advancedUserLoginModel -> getRepository().findById(advancedUserLoginModel.id());
            default -> Optional.empty();
        };

        return potentialUser.filter(UserEntity::isAccountNonLocked).map(user ->
        {
            VerificationService service = getVerificationService();
            return service.requestLogin(user, loginModel);
        });
    }

    @Cacheable("tokenValidation")
    @Transactional public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token) throws SignatureException
    {
        if (token.isBlank())
        {
            return Optional.empty();
        }

        return getVerificationService().validate(token, (user) -> {
            Optional<UserEntity> optionalUser = loadEntityById(user);
            return optionalUser.map(UserEntity::getAuthorities).orElse(Collections.emptySet());
        });
    }

    @Override public @NotNull @Unmodifiable Set<UserEntity> findAllEntities(@NotNull Predicate<UserEntity> predicate)
    {
        return getRepository().findAllEntities().stream().filter(predicate).collect(Collectors.toUnmodifiableSet());
    }

    public @NotNull Optional<ReducedUserModel> findReduced(long id)
    {
        return getRepository().findReducedById(id);
    }

    public @NotNull @Unmodifiable Set<ReducedUserModel> findAllReduced()
    {
        return getRepository().findAllReduced();
    }
}
