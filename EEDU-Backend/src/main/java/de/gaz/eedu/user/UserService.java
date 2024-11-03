package de.gaz.eedu.user;

import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.classroom.ClassRoomService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupRepository;
import de.gaz.eedu.user.model.LoginModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.theming.ThemeRepository;
import de.gaz.eedu.user.verfication.AuthorizeService;
import de.gaz.eedu.user.verfication.authority.AuthorityFactory;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) @Slf4j
public class UserService extends EntityService<UserRepository, UserEntity, UserModel, UserCreateModel> implements UserDetailsService
{
    @Getter private final AuthorizeService authorizeService;
    @Getter private final ClassRoomService classRoomService;
    @Getter(AccessLevel.NONE) private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ThemeRepository themeRepository;

    @Override public @NotNull UserRepository getRepository()
    {
        return userRepository;
    }

    @Transactional @Override public @NotNull List<UserEntity> createEntity(@NotNull Set<UserCreateModel> model) throws CreationException
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

    @Transactional @Override public @NotNull UserDetails loadUserByUsername(
            @NotNull String username) throws UsernameNotFoundException
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

    @Transactional public @NotNull Optional<String> login(@NotNull LoginModel loginModel)
    {
        Optional<UserEntity> userOptional = getRepository().findByLoginName(loginModel.loginName());
        Function<UserEntity, String> auth = user -> getAuthorizeService().login(user, user.getPassword(), loginModel);
        return userOptional.filter(UserEntity::isAccountNonLocked).map(auth);
    }

    @Transactional public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token)
    {
        try
        {
            Function<UserEntity, Set<? extends GrantedAuthority>> function = UserEntity::getAuthorities;
            AuthorityFactory authorityFactory = (id) -> loadEntityById(id).map(function).orElse(new HashSet<>());
            return getAuthorizeService().validate(token, authorityFactory);
        }
        catch (ExpiredJwtException ignored)
        {
            log.warn("An incoming request has been received with an expired token.");
        }
        return Optional.empty();
    }
}
