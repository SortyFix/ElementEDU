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
import de.gaz.eedu.user.theming.ThemeCreateModel;
import de.gaz.eedu.user.theming.ThemeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.function.Supplier;

@Component @AllArgsConstructor public class DataLoader implements CommandLineRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final UserService userService;
    private final GroupService groupService;
    private final PrivilegeService privilegeService;
    private final ThemeService themeService;

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
        String randomPassword = randomPassword(10);

        ThemeCreateModel themeCreateModel = new ThemeCreateModel("Dark", 0x0000000, 0x000000, 0x000000);
        UserCreateModel userCreateModel = new UserCreateModel("root", "root", "root", randomPassword, true, false, 1L);
        GroupCreateModel groupCreateModel = new GroupCreateModel("admin", false, new Long[0], new Long[0]);
        PrivilegeCreateModel privilegeCreateModel = new PrivilegeCreateModel("ADMIN", new HashSet<>());

        getEntity(themeService, themeCreateModel); // create theme

        PrivilegeEntity privilegeEntity = getEntity(privilegeService, privilegeCreateModel);
        GroupEntity groupEntity = getEntity(groupService, groupCreateModel);
        UserEntity userEntity = userService.createEntity(userCreateModel);

        groupEntity.grantPrivilege(groupService, privilegeEntity);
        if(!userEntity.attachGroups(userService, groupEntity))
        {
            throw new IllegalStateException("The system was not able to attach the admin group to the default user. This is very unusual behaviour. Please consider rechecking all information.");
        }

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

    @SuppressWarnings({"SpellCheckingInspection", "SameParameterValue"})
    private @NotNull String randomPassword(int length)
    {
        SecureRandom random = new SecureRandom();
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialSymbols = "!@#$%^&*_=+-/";
        String alphabet = upperCaseLetters + lowerCaseLetters + digits + specialSymbols;

        StringBuilder password = new StringBuilder();

        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialSymbols.charAt(random.nextInt(specialSymbols.length())));

        for (int i = 4; i < length; i++) {
            password.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return password.toString();
    }
}
