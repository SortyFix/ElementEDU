package de.gaz.eedu;

import de.gaz.eedu.security.PrivilegeProperties;
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
import java.util.List;
import java.util.Set;

@Component @RequiredArgsConstructor @Slf4j @Getter(AccessLevel.PROTECTED)
public class DataLoader implements CommandLineRunner
{
    private final UserService userService;
    private final CredentialService credentialService;
    private final GroupService groupService;
    private final PrivilegeService privilegeService;
    private final ThemeService themeService;

    private final PrivilegeProperties privilegeProperties;

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

        PrivilegeEntity privilegeEntity = createDefaultPrivileges();
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
        int bitMask = CredentialMethod.bitMask(CredentialMethod.PASSWORD, CredentialMethod.TOTP);

        CredentialCreateModel credential = new CredentialCreateModel(userEntity.getId(), password, bitMask, randomPassword);
        getCredentialService().createEntity(Set.of(credential));
    }

    private @NotNull PrivilegeEntity createDefaultPrivileges()
    {
        getPrivilegeProperties().getPrivileges();
        PrivilegeCreateModel privilege = new PrivilegeCreateModel("ADMIN");
        return getPrivilegeService().createEntity(Set.of(privilege)).getFirst();
    }

    private @NotNull GroupEntity createDefaultGroup(@NotNull PrivilegeEntity privilegeEntity)
    {
        GroupCreateModel group = new GroupCreateModel("admin", new Long[] { privilegeEntity.getId() });
        return getGroupService().createEntity(Set.of(group)).getFirst();
    }

    private @NotNull ThemeEntity createDefaultTheme()
    {
        ThemeCreateModel theme = new ThemeCreateModel("default", new short[]{5, 5, 5}, new short[]{10, 10, 10});
        return getThemeService().createEntity(Set.of(theme)).getFirst();
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
        UserEntity userEntity = getUserService().createEntity(Set.of(user)).getFirst();
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
