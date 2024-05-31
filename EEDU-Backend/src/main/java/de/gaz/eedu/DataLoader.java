package de.gaz.eedu;

import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.UserStatus;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeService;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.theming.ThemeCreateModel;
import de.gaz.eedu.user.theming.ThemeEntity;
import de.gaz.eedu.user.theming.ThemeService;
import de.gaz.eedu.user.verification.credentials.CredentialService;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component @RequiredArgsConstructor @Slf4j @Getter(AccessLevel.PROTECTED)
public class DataLoader implements CommandLineRunner
{
    private final UserService userService;
    private final CredentialService credentialService;
    private final GroupService groupService;
    private final PrivilegeService privilegeService;
    private final ThemeService themeService;
    @Value("${development:false}") private boolean development;

    @Override @Transactional public void run(@NotNull String... args)
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
     */
    private void createDefaultUser()
    {
        String randomPassword = randomPassword(15);

        PrivilegeEntity privilegeEntity = createDefaultPrivilege();
        GroupEntity groupEntity = createDefaultGroup(privilegeEntity);
        UserEntity userEntity = createDefaultUser(createDefaultTheme(), groupEntity);
        setPassword(userEntity, randomPassword);

        log.info("A default user has been created");
        log.info("-".repeat(20));
        log.info("USERNAME: {}", userEntity.getLoginName());
        log.info("PASSWORD: {}", randomPassword);
        log.info("-".repeat(20));

        if (!development)
        {
            log.warn("It's advised to change the password as soon as possible.");
        }
    }

    private void setPassword(@NotNull UserEntity userEntity, @NotNull String randomPassword)
    {
        CredentialMethod password = CredentialMethod.PASSWORD;
        CredentialCreateModel credential = new CredentialCreateModel(userEntity.getId(), password, randomPassword);
        getCredentialService().create(credential);
    }

    private @NotNull PrivilegeEntity createDefaultPrivilege()
    {
        PrivilegeCreateModel privilege = new PrivilegeCreateModel("ADMIN", new Long[0]);
        return getPrivilegeService().createEntity(privilege);
    }

    private @NotNull GroupEntity createDefaultGroup(@NotNull PrivilegeEntity privilegeEntity)
    {
        GroupCreateModel group = new GroupCreateModel("admin", new Long[0], new Long[] { privilegeEntity.getId() });
        return getGroupService().createEntity(group);
    }

    private @NotNull ThemeEntity createDefaultTheme()
    {
        ThemeCreateModel theme = new ThemeCreateModel("Dark", 0x0000000, 0x000000, 0x000000); //TODO use real values
        return getThemeService().createEntity(theme);
    }

    private @NotNull UserEntity createDefaultUser(@NotNull ThemeEntity themeEntity, @NotNull GroupEntity groupEntity)
    {
        // long line -.-
        UserCreateModel user = new UserCreateModel("root",
                "root",
                "root",
                true,
                false,
                UserStatus.PROSPECTIVE,
                themeEntity.getId(),
                new Long[] { groupEntity.getId() });
        UserEntity userEntity = getUserService().createEntity(user);
        userEntity.setSystemAccount(true);

        return getUserService().saveEntity(userEntity);
    }

    @SuppressWarnings({
            "SpellCheckingInspection", "SameParameterValue"
    }) private @NotNull String randomPassword(int length)
    {
        if (development)
        {
            return "Development123!";
        }

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

        for (int i = 4; i < length; i++)
        {
            password.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return password.toString();
    }
}
