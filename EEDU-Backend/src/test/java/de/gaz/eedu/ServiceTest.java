package de.gaz.eedu;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.EntityModel;
import de.gaz.eedu.entity.model.EntityModelRelation;
import de.gaz.eedu.exception.OccupiedException;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code ServiceTest} is an abstract class, providing a generic way to test services.
 * This class is designed to be extended by other test classes, which can provide the specifics
 * about the entity E, the model M, and the creation model C.
 *
 * <p>
 * This class provides us the functionality to test services in the Spring context, which is ensured
 * by the {@link SpringBootTest} annotation. It also makes sure that these tests will be run with the "test"
 * profile active, which is done using the {@link ActiveProfiles} annotation. The need to use the Spring
 * context and a specific profile comes from the fact that these tests might have to mock beans and
 * use repositories for some data retrieval operations.
 * </p>
 *
 * <p>
 * Each of the type parameters has a specific purpose:
 * </p>
 *
 * <ul>
 *     <li>E is the entity that the service will be handling. This entity must extend {@link EntityModelRelation}.</li>
 *     <li>S is the actual service that will be handling everything. It must extend {@link EntityService}.</li>
 *     <li>M is the model for the entity - a lighter or specific view of the entity sometimes referred to as DTO. It must extend {@link EntityModel}</li>
 *     <li>C is a creation model for the entity - a model specifically designed for creating new instances of E. It must extend {@link CreationModel}</li>
 * </ul>
 *
 * @param <E> The entity type that the service under test handles.
 *            It must extend from the {@code EDUEntity}.
 * @param <M> The model of the entity for the service under test.
 * @param <C> The creation model for the entity, used when creating new instances.
 * @author ivo
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS) @SpringBootTest @ActiveProfiles("test")
public abstract class ServiceTest<P, S extends EntityService<P, ?, E, M, C>, E extends EntityModelRelation<P, M>, M extends EntityModel<P>, C extends CreationModel<P, E>>
{

    protected abstract @NotNull S getService();

    /**
     * This is an abstract method that must be implemented by any class inheriting from this one. The implementation of
     * this method must provide a {@link Eval} type that represents a successful case for the class under test.
     * <p>
     * It's marked as {@link NotNull}, requiring non-null values for its return.
     *
     * @return the successful {@link Eval} instance for the service under test
     */
    @Contract(value = "-> new", pure = true) protected abstract @NotNull Eval<C, M> successEval() throws IOException, URISyntaxException;

    /**
     * This is another abstract method that must provide a {@link C} type that represents the creation model of an
     * entity
     * that has
     * already been occupied.
     * <p>
     * The expected implementations of this method should be able to construct or fetch such kind of data according to
     * their own testing needs.
     *
     * @return the instance representing the occupied creation model
     */
    @Contract(value = "-> new", pure = true) protected abstract @NotNull C occupiedCreateModel();

    /**
     * This is a test case that checks if the service successfully creates an entity without any exception or error.
     * The test case involves creating an instance of {@link Eval} object which is representative of a successful
     * evaluation
     * (obtained using the helper method {@link #successEval()}).
     * <p>
     * The {@link Eval#evaluateResult(Object)} method of the {@link Eval} object is then called with the result of
     * the service's `create`
     * method.
     * The {@code create(C)} method is called with the request from the {@link Eval} object. In this way it
     * guarantees consistency in
     * the test.
     * <p>
     * Thus, this method is designed to test the successful case for the creation action. Any failure in this test may
     * indicate a
     * problem with either the {@code create(C)} method of the service, or the dependencies
     * (via{@link #successEval()}) used for this test.
     * If the test fails, the service may not be correctly creating entities.
     *
     * @see #successEval()
     */
    @Test @Transactional public void testCreateEntitySuccess() throws IOException, URISyntaxException
    {
        Eval<C, M> success = successEval();
        success.evaluateResult(getService().create(Set.of(success.request())).getFirst());
    }

    /**
     * This is a test case that checks if the service throws an OccupiedException when trying to create an entity
     * that has
     * already been occupied.
     * Firstly, it uses the JUnit's assertThrows() method to expect the specified exception (in this case,
     * OccupiedException).
     * Secondly, it asserts that running the service's create method with a CreateModel (obtained via the helper
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
        Assertions.assertThrows(OccupiedException.class, () -> getService().create(Set.of(occupiedCreateModel())));
    }

    /**
     * Method <code>test</code> is a generic method that abstracts
     * the testing process of an evaluation. It uses an evaluator
     * to perform some form of evaluation on the result of
     * tester's execution.
     *
     * <p>
     * It takes two parameters evaluator and tester, with evaluator
     * capable of evaluating some kind of result and tester capable
     * of executing request by evaluator and return a result.
     * </p>
     *
     * <p>
     * The purpose of this method is to evaluate the result of an
     * execution in a type-agnostic manner. By decoupling the evaluation
     * and testing process, we can write tests that are independent of
     * the evaluation mechanism.
     * </p>
     *
     * @param evaluator This is a functional interface that is capable
     *                  of evaluating the result from execution. It
     *                  provides a way to define the behavior of
     *                  evaluation through lambda expressions or method
     *                  references.
     * @param tester    This is the object on which the execution and
     *                  testing happens. It's execute method is invoked
     *                  with the request from evaluator. The result then
     *                  feeds back to evaluators for evaluation.
     * @param <R>       the type of the request object that the tester will
     *                  execute.
     * @param <T>       the type of the result after tester's execution,
     *                  and what evaluator will evaluate.
     */
    protected <R, T> void test(@NotNull Eval<R, T> evaluator, @NotNull Tester<R, T> tester)
    {
        evaluator.evaluateResult(tester.execute(evaluator.request()));
    }

    @Test
    public void testDeleteEntitySuccess()
    {
        TestData<P, Boolean>[] deleteData = deleteEntities();
        if(deleteData.length == 0)
        {
            Assumptions.abort();
        }

        for(TestData<P, Boolean> current : deleteData)
        {
            test(Eval.eval(current.entityID(), current.expected(), Validator.equals()), (id) -> {
                preDeleteValidation().evaluate(id, current.expected(), current.expected());
                boolean deleted = getService().delete(id);
                verifyDeletionOutcome().evaluate(id, current.expected(), deleted);
                return deleted;
            });
        }
    }

    protected @NotNull Validator<P, Boolean> preDeleteValidation()
    {
        return ((request, expect, result) -> {});
    }

    @Contract(pure = true, value = "-> new")
    protected @NotNull Validator<P, Boolean> verifyDeletionOutcome()
    {
        return ((request, expect, result) -> {});
    }

    @Contract(pure = true, value = "-> new") protected @NotNull TestData<P, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[0];
    }

    /**
     * Interface <code>Validator</code> is designed as a FunctionalInterface
     * to abstract the concept of valuation. This gives us the flexibility
     * to define its behavior while using it.
     *
     * <p>
     * The Validator's role is to perform a validation by comparing
     * the expected result to the actual result of a request.
     * </p>
     *
     * @param <R> The type of the request which is to be validated.
     * @param <E> The type of the expected and result which needs equivalent to perform validation.
     */
    @FunctionalInterface protected interface Validator<R, E>
    {
        /**
         * This method returns a {@link Validator} that performs an equality check
         * between the expected and actual results using the {@link Assertions#assertEquals} method from JUnit.
         * The contract of this method is to always return a new instance every time it is called.
         *
         * <p>
         * The {@code @Contract(pure = true)} annotation indicates that this method is pure,
         * which means that the returned {@link Validator} doesn't depend on any mutable state
         * and doesn't produce any side effects. The "pureness" of this method allows for safe
         * usage in any context, even in a multi-threaded environment.
         * </p>
         * <p>
         * Example Usage:
         * </p>
         * <pre>{@code
         * // Define a sample request
         * MyRequest request = new SomeRequest();
         *
         * // Define the expected result
         *
         * String expectedResult = "some result";
         *
         * Validator<MyRequest, String> equalityValidator = Validator.equals();
         * test(Eval.eval(request, expectedResult, equalityValidator), (request) ->
         * {
         *      // Perform the actual operation that produces the result
         *      String actualResult = performOperation(request);
         *      return actualResult;
         * });
         * *
         * }</pre>
         *
         * @param <R> The type of the request.
         * @param <E> The type of the expected and the result of the request.
         * @return A new instance of {@link Validator}.
         * @see Validator
         * @see Assertions#assertEquals(Object, Object)
         */
        @Contract(pure = true) static <R, E> @NotNull Validator<R, E> equals()
        {
            return (request, expected, result) -> Assertions.assertEquals(expected, result);
        }

        /**
         * This method returns a {@link Validator} that performs an array equality check between the expected and actual results
         * using the {@link Assertions#assertTrue(boolean)} method from JUnit.
         * The contract of this method is to always return a new instance every time it is called.
         * <p>
         * The {@code @Contract(pure = true)} annotation indicates that this method is pure, which means that the returned
         * {@link Validator} doesn't depend on any mutable state and doesn't produce any side effects. The "pureness" of this method
         * allows for safe usage in any context, even in a multi-threaded environment.
         * </p>
         * <p>
         * The array equality check is performed by calling {@link Assertions#assertArrayEquals(Object[], Object[])}.
         * </p>
         * <p>
         * Example Usage:
         * </p>
         * <pre>{@code
         * // Define a sample request
         * MyRequest request = new SomeRequest();
         *
         * // Define the expected array of results
         *
         * String[] expectedResults = {
         *      "result1",
         *      "result2",
         *      "result3"
         * };
         *
         * Validator<MyRequest, String[]> arrayValidator = Validator.arrayEquals();
         * test(Eval.eval(request, expectedResults, arrayValidator), (request) ->
         * {
         *      // Perform the actual operation that produces the result array
         *      String[] actualResults = performOperation(request);
         *      return actualResults;
         * });
         *
         * }</pre>
         *
         * @param <R> The type of the request.
         * @param <E> The type of the elements in the expected and result arrays.
         * @return A new instance of {@link Validator} for array equality checks.
         * @see Validator
         * @see Assertions#assertTrue(boolean)
         */
        @Contract(pure = true) static <R, E> @NotNull Validator<R, E[]> exactArrayEquals()
        {
            return (request, expect, result) -> Assertions.assertArrayEquals(expect, result);
        }

        /**
         * This method returns a {@link Validator} that performs an array equality check between the expected and actual results
         * using the {@link Assertions#assertTrue(boolean)} method from JUnit.
         * The contract of this method is to always return a new instance every time it is called.
         * <p>
         * The {@code @Contract(pure = true)} annotation indicates that this method is pure, which means that the returned
         * {@link Validator} doesn't depend on any mutable state and doesn't produce any side effects. The "pureness" of this method
         * allows for safe usage in any context, even in a multi-threaded environment.
         * </p>
         * <p>
         * The array equality check is performed by converting the expected array into a {@link Set} using the
         * {@link Arrays#stream(Object[])} and {@link Collectors#toSet()} methods, and then iterating through the actual result
         * array to ensure each element is present in the expected set. If any element in the result array is not present in the
         * expected set, an assertion failure will be triggered using {@link Assertions#assertTrue(boolean)}.
         * </p>
         * <p>
         * Example Usage:
         * </p>
         * <pre>{@code
         * // Define a sample request
         * MyRequest request = new SomeRequest();
         *
         * // Define the expected array of results
         *
         * String[] expectedResults = {
         *      "result1",
         *      "result2",
         *      "result3"
         * };
         *
         * Validator<MyRequest, String[]> arrayValidator = Validator.arrayEquals();
         * test(Eval.eval(request, expectedResults, arrayValidator), (request) ->
         * {
         *      // Perform the actual operation that produces the result array
         *      String[] actualResults = performOperation(request);
         *      return actualResults;
         * });
         *
         * }</pre>
         *
         * @param <R> The type of the request.
         * @param <E> The type of the elements in the expected and result arrays.
         * @return A new instance of {@link Validator} for array equality checks.
         * @see Validator
         * @see Assertions#assertTrue(boolean)
         */
        @Contract(pure = true) static <R, E> @NotNull Validator<R, E[]> arrayEquals()
        {
            return (request, expect, result) ->
            {
                List<E> expected = Arrays.asList(expect);
                for (E current : result)
                {
                    Assertions.assertTrue(expected.contains(current));
                }
            };
        }

        /**
         * Evaluates a request, expected result and actual result.
         *
         * @param request The request which is to be validated.
         * @param expect  The expected result of the request.
         * @param result  The result of the request which is to be compared to the expected result.
         */
        void evaluate(@NotNull R request, @NotNull E expect, @NotNull E result);
    }

    /**
     * Interface <code>Tester</code> is designed as a FunctionalInterface
     * to execute a particular request. The execute method will execute the
     * request and return the result which the Validator can use to evaluate.
     *
     * @param <R> The type of the request which the tester will execute.
     * @param <E> The type of the result after tester's execution.
     */
    @FunctionalInterface protected interface Tester<R, E>
    {
        /**
         * Executes a given request and returns the result of the execution.
         *
         * @param request The request which is to be executed.
         * @return The result of the execution of the request.
         */
        @NotNull E execute(@NotNull R request);
    }

    /**
     * The record <code>Eval</code> is designed as a holder for request, expected result and a
     * validator. It uses the Java record feature that not only acts as a data carrier but
     * also provides logic to evaluate results.
     *
     * <p>
     * A Eval record encapsulates the details of a request that needs be executed and
     * validated. It also contains a Validator which is capable of evaluating the
     * expected and actual result of the request.
     * </p>
     *
     * @param request   The request which is to be validated.
     * @param expect    The expected result of the request.
     * @param validator The validator which is used to validate the request's result.
     * @param <R>       The type of the request.
     * @param <E>       The type of the expected result.
     */
    protected record Eval<R, E>(@NotNull R request, @NotNull E expect, @NotNull ServiceTest.Validator<R, E> validator)
    {
        /**
         * Factory method for creating a new instance of Eval.
         *
         * @param request   The request which is to be validated.
         * @param expect    The expected result of the request.
         * @param validator The validator which is used to validate the request's result.
         * @param <R>       The type of the request.
         * @param <E>       The type of the expected result.
         * @return A new instance of Eval.
         */
        @Contract("_, _, _ -> new") public static <R, E> @NotNull Eval<R, E> eval(
                @NotNull R request, @NotNull E expect, @NotNull Validator<R, E> validator)
        {
            return new Eval<>(request, expect, validator);
        }

        /**
         * Method for evaluating a request's result. It uses an instance of the Validator passed
         * at the construction time to perform the evaluation.
         *
         * @param result The result of the request which is to be evaluated.
         */
        public void evaluateResult(@NotNull E result)
        {
            validator().evaluate(request(), expect(), result);
        }
    }
}
