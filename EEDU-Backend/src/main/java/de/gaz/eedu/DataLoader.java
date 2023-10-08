package de.gaz.eedu;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserModel;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupModel;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeModel;
import de.gaz.eedu.user.privileges.PrivilegeService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final UserService userService;
    private final GroupService groupService;
    private final PrivilegeService privilegeService;

    @Override
    public void run(String... args) {
        if(userService.findAll().isEmpty())
        {
            createDefaultUser();
        }
    }

    private void createDefaultUser() {
        String randomPassword = randomPassword(10);
        UserModel userModel = new UserModel(null, "root", "root", "root", "root@email.com", randomPassword, true, false, new HashSet<>());
        GroupModel groupModel = new GroupModel(null, "admin", new HashSet<>(), new HashSet<>());
        PrivilegeModel privilegeModel = new PrivilegeModel(null, "ADMIN", new HashSet<>());

        PrivilegeEntity privilegeEntity = privilegeService.loadEntityByName("ADMIN").orElse(privilegeService.createEntity(privilegeModel));
        GroupEntity groupEntity = groupService.loadEntityByName("admin").orElse(groupService.createEntity(groupModel));
        UserEntity userEntity = userService.createEntity(userModel);

        groupEntity.grantPrivilege(privilegeEntity);
        groupService.saveEntity(groupEntity);

        userEntity.attachGroups(groupEntity);
        userService.saveEntity(userEntity);

        LOGGER.info("A default user has been created with the name {} and the password {}.", userEntity.getLoginName(), randomPassword);
    }

    //TODO maybe better
    private @NotNull String randomPassword(@SuppressWarnings("SameParameterValue") int length)
    {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
