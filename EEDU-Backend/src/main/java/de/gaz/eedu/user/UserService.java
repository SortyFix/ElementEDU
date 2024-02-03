package de.gaz.eedu.user;

import de.gaz.eedu.course.classroom.ClassRoomEntity;
import de.gaz.eedu.course.classroom.ClassRoomService;
import de.gaz.eedu.course.CourseEntity;
import de.gaz.eedu.course.CourseService;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.exception.InsecurePasswordException;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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
@Service @AllArgsConstructor @Getter(AccessLevel.PROTECTED) public class UserService implements EntityService<UserRepository, UserEntity, UserModel, UserCreateModel>, UserDetailsService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Getter private final AuthorizeService authorizeService;
    @Getter private final ClassRoomService classRoomService;
    @Getter(AccessLevel.NONE)
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ThemeRepository themeRepository;

    @Override
    public @NotNull UserRepository getRepository()
    {
        return userRepository;
    }

    @Transactional @Override public @NotNull UserEntity createEntity(@NotNull UserCreateModel model) throws CreationException
    {
        if(getRepository().existsByLoginName(model.loginName()))
        {
            throw new NameOccupiedException(model.loginName());
        }

        String password = model.password();
        if (!password.matches("^(?=(.*[a-z])+)(?=(.*[A-Z])+)(?=(.*[0-9])+)(?=(.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~])+).{6,}$"))
        {
            throw new InsecurePasswordException();
        }

        String hashedPassword = getAuthorizeService().encode(model.password());
        return saveEntity(model.toEntity(new UserEntity(), entity ->
        {
            entity.setPassword(hashedPassword); // outsource as it must be encrypted using the encryption service.
            entity.setThemeEntity(themeRepository.getReferenceById(1L));
            return entity;
        }));
    }

    @Transactional @Override public @NotNull UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException
    {
        return getRepository().findByLoginName(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override public boolean delete(long id)
    {
        return getRepository().findById(id).map(userEntity ->
        {
            // Delete groups from this user
            Long[] groups = userEntity.getGroups().stream().map(GroupEntity::getId).toArray(Long[]::new);
            userEntity.detachGroups(this, groups);

            ClassRoomService classService = getClassRoomService();
            CourseService courseService = classService.getCourseService();

            // Remove this user from all courses
            Consumer<CourseEntity> detachCourses = course -> course.detachUser(courseService, userEntity.getId());
            int coursesSize = userEntity.getCourses().size();
            userEntity.getCourses().forEach(detachCourses);

            // Remove this user from all classes
            Consumer<ClassRoomEntity> detachClasses = clazz -> clazz.detachUser(classService, userEntity.getId());
            int classSize = userEntity.getClassRooms().size();
            userEntity.getClassRooms().forEach(detachClasses);

            getRepository().deleteById(id);

            String deleteMessage = "Deleted user {} from the system. Additionally, the user has been disassociated from {} groups, {} courses and {} classes.";
            LOGGER.info(deleteMessage, userEntity.getId(), groups.length, coursesSize, classSize);
            return true;
        }).orElse(false);
    }

    @Transactional public @NotNull Optional<String> login(@NotNull LoginModel loginModel)
    {
        Optional<UserEntity> userOptional = getRepository().findByLoginName(loginModel.loginName());
        return userOptional.filter(UserEntity::isAccountNonLocked).map(user -> getAuthorizeService().login(user.toModel(), user.getPassword(), loginModel));
    }

    @Transactional public @NotNull Optional<UsernamePasswordAuthenticationToken> validate(@NotNull String token)
    {
        try
        {
            Function<UserEntity, Set<? extends GrantedAuthority>> function = UserEntity::getAuthorities;
            AuthorityFactory authorityFactory = (id) -> loadEntityByID(id).map(function).orElse(new HashSet<>());
            return getAuthorizeService().validate(token, authorityFactory);
        }
        catch (ExpiredJwtException ignored)
        {
            LOGGER.warn("An incoming request has been received with an expired token.");
        }
        return Optional.empty();
    }
}
