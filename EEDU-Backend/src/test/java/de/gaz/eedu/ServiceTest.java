package de.gaz.eedu;

import de.gaz.eedu.entity.EDUEntityService;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.exception.OccupiedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class ServiceTest<E extends EDUEntity, M extends Model, C extends CreationModel<E>>
{
    private final EDUEntityService<E, M, C> service;

    public ServiceTest(@NotNull EDUEntityService<E, M, C> service)
    {
        this.service = service;
    }

    /**
     * This method provides access to the EDUEntityService instance that serves the purpose of carrying out actions
     * related to entities within the application. This instance is typically initialized during the setup phase of a
     * test.
     * It is marked with the @NotNull annotation, meaning that this method should never return null in any
     * circumstances.
     *
     * @return the service instance, never null
     * @see EDUEntityService
     */
    protected @NotNull EDUEntityService<E, M, C> getService()
    {
        return service;
    }

    /**
     * This is an abstract method that must be implemented by any class inheriting from this one. The implementation of
     * this method must provide a `Eval<C, M>` type that represents a successful case for the class under test.
     * <p>
     * It's marked as @NotNull, requiring non-null values for its return.
     *
     * @return the successful `Eval<C, M>` instance for the service under test
     */
    protected abstract @NotNull ServiceTest.Eval<C, M> successEval();

    /**
     * This is another abstract method that must provide a `C` type that represents the create model of an entity
     * that has
     * already been occupied.
     * <p>
     * The expected implementations of this method should be able to construct or fetch such kind of data according to
     * their own testing needs.
     *
     * @return the instance representing the occupied creation model
     */
    protected abstract C occupiedCreateModel();

    /**
     * This is a test case that checks if the service successfully creates an entity without any exception or error.
     * The test case involves creating an instance of `Eval<C, M>` object which is representative of a successful
     * evaluation
     * (obtained using the helper method `successEval()`).
     * <p>
     * The `evaluateResult` method of the `Eval` object is then called with the result of the service's `create` method.
     * The `create` method is called with the request from the `Eval` object. In this way it garantees consistency in
     * the test.
     * <p>
     * Thus, this method is designed to test the successful case for the create action. Any failure in this test may
     * indicate a
     * problem with either the `create` method of the service, or the dependencies (via `successEval()`) used for
     * this test.
     * If the test fails, the service may not be correctly creating entities.
     */
    @Test public void testCreateEntitySuccess()
    {
        Eval<C, M> success = successEval();
        success.evaluateResult(getService().create(success.request()));
    }

    /**
     * This is a test case that checks if the service throws an OccupiedException when trying to create an entity
     * that has
     * already been occupied.
     * Firstly, it uses the JUnit's assertThrows() method to expect the specified exception (in this case,
     * OccupiedException).
     * Secondly, it asserts that running the service's create method with an CreateModel (obtained via the helper
     * method occupiedCreateModel()) will throw the expected exception.
     * <p>
     * The lambda expression inside the assertThrows is an executable, which can be any statement which throws an
     * exception.
     * This is an example of how JUnit can be used to test if a method under certain circumstances throws the
     * expected exception.
     *
     * @throws OccupiedException if the entity to be created is already occupied.
     * @see OccupiedException
     * @see org.junit.jupiter.api.Assertions#assertThrows(Class, Executable)
     */
    @Test public void testCreateEntityOccupied()
    {
        Assertions.assertThrows(OccupiedException.class, () -> getService().create(occupiedCreateModel()));
    }

    protected <R, T> void test(@NotNull Eval<R, T> evaluator, @NotNull Tester<R, T> tester)
    {
        evaluator.evaluateResult(tester.execute(evaluator.request()));
    }

    @ParameterizedTest(name = "{index} => request={0}")
    @ValueSource(longs = {4L, 15L}) public void testDeleteEntitySuccess(long id)
    {
        boolean expect = id == 4;
        boolean result = getService().delete(id);
        Assertions.assertEquals(expect, result);
    }

    protected record Eval<R, E>(@NotNull R request, @NotNull E expect, @NotNull ServiceTest.Validator<R, E> validator)
    {
        @Contract("_, _, _ -> new") public static <R, E> @NotNull Eval<R, E> eval(@NotNull R request, @NotNull E expect, @NotNull ServiceTest.Validator<R, E> validator)
        {
            return new Eval<>(request, expect, validator);
        }

        public void evaluateResult(@NotNull E result)
        {
            validator().evaluate(request(), expect(), result);
        }
    }

    @FunctionalInterface
    protected interface Validator<R, E>
    {
        void evaluate(@NotNull R request, @NotNull E expect, @NotNull E result);
    }

    @FunctionalInterface
    protected interface Tester<R, E>
    {
        @NotNull E execute(@NotNull R request);
    }
}
