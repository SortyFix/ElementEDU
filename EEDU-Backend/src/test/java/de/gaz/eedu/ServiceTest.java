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

    protected @NotNull EDUEntityService<E, M, C> getService()
    {
        return service;
    }

    protected abstract @NotNull ServiceTest.Eval<C, M> successEval();

    protected abstract C occupiedEval();

    @Test public void testCreateEntitySuccess()
    {
        Eval<C, M> success = successEval();
        success.evaluateResult(getService().create(success.request()));
    }

    @Test public void testCreateEntityOccupied()
    {
        Assertions.assertThrows(OccupiedException.class, () -> getService().create(occupiedEval()));
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
