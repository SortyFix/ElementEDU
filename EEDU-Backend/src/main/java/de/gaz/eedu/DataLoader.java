package de.gaz.eedu;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeService;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.theming.ThemeEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.Supplier;

@Component @AllArgsConstructor public class DataLoader implements CommandLineRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final UserService userService;
    private final GroupService groupService;
    private final PrivilegeService privilegeService;

    @Override @Transactional(Transactional.TxType.REQUIRED) public void run(@NotNull String... args)
    {
        if (userService.findAll().isEmpty())
        {
            createDefaultUser();
        }
    }

    /**
     * Creates a default user.
     * <p>
     * This method creates a default user which is named root.
     * This user can be accessed after a first time run and should provide
     * a direct entrance into the system.
     * <p>
     * It creates the following objects: A user named root, a group named admin and a privilege named ADMIN.
     * This {@link PrivilegeEntity} can then be added to the {@link GroupEntity}. Then the {@link UserEntity} is
     * attached to the admin group.
     * <p>
     * TODO implement two factor, so this account can be secured right after it gets accessed
     */
    private void createDefaultUser()
    {
        ThemeEntity themeEntity = new ThemeEntity();
        String randomPassword = randomPassword(10);
        UserCreateModel userCreateModel = new UserCreateModel("root", "root", "root", randomPassword, true, false, themeEntity);
        GroupCreateModel groupCreateModel = new GroupCreateModel("admin", new HashSet<>(), new HashSet<>());
        PrivilegeCreateModel privilegeCreateModel = new PrivilegeCreateModel("ADMIN", new HashSet<>());

        PrivilegeEntity privilegeEntity = getEntity(privilegeService, privilegeCreateModel);
        GroupEntity groupEntity = getEntity(groupService, groupCreateModel);
        UserEntity userEntity = userService.createEntity(userCreateModel);

        groupEntity.grantPrivilege(groupService, privilegeEntity);
        userEntity.attachGroups(userService, groupEntity);

        LOGGER.info("A default user has been created with the name {} and the password {}. It's advised to " +
                "change the password as soon as possible.", userEntity.getLoginName(), randomPassword);
    }

    /**
     * Attempts to load an entity by name using the provided service and creation model.
     * If an entity with the specified name doesn't exist, a new one is created using the creation model.
     *
     * @param service          the service used to load and potentially create the entity
     * @param groupCreateModel the creation model which describes the entity to be loaded or created
     * @param <E>              The type of the EDUEntity
     * @param <C>              The type of the CreationModel related to the EDUEntity
     * @return the loaded or created entity
     * @throws java.util.NoSuchElementException if the creation model didn't specify a name and an entity couldn't be
     *                                          loaded
     */
    private <E extends EDUEntity, C extends CreationModel<E>> @NotNull E getEntity(@NotNull EntityService<E, ?, C> service, @NotNull C groupCreateModel)
    {
        Supplier<E> create = () -> service.createEntity(groupCreateModel);
        return service.loadEntityByName(groupCreateModel.name()).orElseGet(create);
    }

    //TODO maybe better
    private @NotNull String randomPassword(@SuppressWarnings("SameParameterValue") int length)
    {
        return "SafePassword123!!.";
    }
}
