package de.gaz.eedu;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserEntityService;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupEntityService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeEntityService;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component @AllArgsConstructor public class DataLoader implements CommandLineRunner
{

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final UserEntityService userService;
    private final GroupEntityService groupService;
    private final PrivilegeEntityService privilegeService;

    @Override @Transactional(Transactional.TxType.REQUIRED) public void run(String... args)
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
        String randomPassword = randomPassword(10);
        UserCreateModel userCreateModel = new UserCreateModel("root", "root", "root", randomPassword, true, false);
        GroupCreateModel groupCreateModel = new GroupCreateModel("admin", new HashSet<>(), new HashSet<>());
        PrivilegeCreateModel privilegeCreateModel = new PrivilegeCreateModel("ADMIN", new HashSet<>());

        PrivilegeEntity privilegeEntity =
                privilegeService.loadEntityByName("ADMIN").orElse(privilegeService.createEntity(
                        privilegeCreateModel));
        GroupEntity groupEntity = groupService.loadEntityByName("admin").orElse(groupService.createEntity(
                groupCreateModel));
        UserEntity userEntity = userService.createEntity(userCreateModel);

        groupEntity.grantPrivilege(privilegeEntity);
        groupService.saveEntity(groupEntity);

        userEntity.attachGroups(groupEntity);
        userService.saveEntity(userEntity);

        LOGGER.info("A default user has been created with the name {} and the password {}.",
                userEntity.getLoginName(),
                randomPassword);
    }

    //TODO maybe better
    private @NotNull String randomPassword(@SuppressWarnings("SameParameterValue") int length)
    {
        return "SafePassword123!!.";
    }
}
