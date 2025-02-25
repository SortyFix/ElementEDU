package de.gaz.eedu.user.privileges;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum SystemPrivileges
{
    CLASS_CREATE,
    CLASS_DELETE,

    SUBJECT_CREATE,
    SUBJECT_DELETE,

    ROOM_CREATE,
    ROOM_DELETE,

    COURSE_CREATE,
    COURSE_DELETE,

    USER_DELETE,
    USER_CREATE,
    USER_OTHERS_GET,
    USER_GROUP_ATTACH,
    USER_GROUP_DETACH,

    GROUP_GET,
    GROUP_CREATE,
    GROUP_DELETE,
    GROUP_PRIVILEGE_GRANT,
    GROUP_PRIVILEGE_REVOKE,

    PRIVILEGE_GET,
    PRIVILEGE_CREATE,
    PRIVILEGE_DELETE,

    USER_CREDENTIAL_OTHERS_CREATE,
    USER_CREDENTIAL_OTHERS_DELETE,
    USER_CREDENTIAL_OTHERS_CREATE_TEMPORARY;

    public static boolean isSystemPrivilege(@NotNull String privilege)
    {
        return get(privilege).isPresent();
    }

    public @NotNull static Optional<SystemPrivileges> get(@NotNull String privilege)
    {
        String upperCasePrivilege = privilege.toUpperCase();
        return Arrays.stream(SystemPrivileges.values()).filter(current ->
        {
            String currentPrivilege = current.name();
            return Objects.equals(currentPrivilege, upperCasePrivilege);
        }).findFirst();
    }

    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return name();
    }
}
