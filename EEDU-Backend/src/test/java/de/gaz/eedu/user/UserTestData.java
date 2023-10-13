package de.gaz.eedu.user;

/**
 * Provides test data.
 * <p>
 * This class only exists to provide test data for the {@link UserServiceMockitoTest} and the {@link UserServiceTest}.
 * <p>
 * Note that some tests may overwrite or adjust these values to their needs to work as they should.
 * Therefore, it is not safe to assume that every test uses all of this data exactly as given.
 *
 * @author ivo
 * @see UserServiceMockitoTest
 * @see UserServiceTest
 */
class UserTestData // only visible in the same package
{
    static final String FIRST_NAME = "John";
    static final String LAST_NAME = "Doe";
    static final String LOGIN_NAME = "john.doe";
    static final String PASSWORD = "Password123!";
    static final boolean ENABLED = true;
    static final boolean LOCKED = false;
}
