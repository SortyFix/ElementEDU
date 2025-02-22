package de.gaz.eedu;

import de.gaz.eedu.user.AccountType;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component @RequiredArgsConstructor @Slf4j @Getter(AccessLevel.PROTECTED) public class DataLoader implements CommandLineRunner
{
    private final UserService userService;
    private final GroupService groupService;
    private final PrivilegeService privilegeService;
    private final CredentialService credentialService;
    private final ThemeService themeService;

    private final Environment environment;

    @Value("${development:false}") private boolean development;

    @Override @Transactional public void run(@NotNull String... args)
    {
        if (userService.getRepository().findByLoginName("root").isEmpty())
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
        String randomPassword = randomPassword();

        createDefaultPrivileges();
        createDefaultGroup();
        UserEntity userEntity = createDefaultUser(createDefaultTheme());
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
        int bitMask = CredentialMethod.bitMask(password);
        CredentialCreateModel credential = new CredentialCreateModel(userEntity.getId(), password, bitMask, randomPassword);
        getCredentialService().createEntity(Set.of(credential));
    }

    private void createDefaultPrivileges()
    {
        Stream<String> privileges = PrivilegeEntity.getProtectedPrivileges().stream();
        getPrivilegeService().createEntity(privileges.map(PrivilegeCreateModel::new).collect(Collectors.toSet()));
    }

    private void createDefaultGroup()
    {
        getGroupService().createEntity(AccountType.groupSet().stream().map(
                (currentGroup) -> new GroupCreateModel(currentGroup, new String[0])
        ).collect(Collectors.toSet()));
    }

    private @NotNull ThemeEntity createDefaultTheme()
    {
        ThemeCreateModel defaultDark = new ThemeCreateModel("defaultDark",
                new byte[]{Byte.MIN_VALUE + 5, Byte.MIN_VALUE + 5, Byte.MIN_VALUE + 5},
                new byte[]{Byte.MIN_VALUE + 10, Byte.MIN_VALUE + 10, Byte.MIN_VALUE + 10});
        ThemeCreateModel defaultLight = new ThemeCreateModel("defaultLight",
                new byte[]{Byte.MIN_VALUE + 255, Byte.MIN_VALUE + 255, Byte.MIN_VALUE + 255},
                new byte[]{Byte.MIN_VALUE + 235, Byte.MIN_VALUE + 235, Byte.MIN_VALUE + 235});

        return getThemeService().createEntity(Set.of(defaultDark, defaultLight)).stream().filter(theme ->
        {
            // Dark will be set as default
            return Objects.equals(theme.getName(), "defaultDark");
        }).findFirst().orElseThrow();
    }

    private @NotNull UserEntity createDefaultUser(@NotNull ThemeEntity themeEntity)
    {
        return getUserService().saveEntity(getUserService().createEntity(Set.of(new UserCreateModel(
                "root", // first id
                "root", // last id
                "root", // login id
                AccountType.ADMINISTRATOR,
                true, // enabled
                false, // locked
                UserStatus.PROSPECTIVE,
                themeEntity.getId(),
                new String[] {  } // groups
        ))).getFirst());
    }

    private @NotNull String randomPassword()
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

        for (int i = 4; i < 15; i++)
        {
            password.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return password.toString();
    }
}