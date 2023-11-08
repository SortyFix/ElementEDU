package de.gaz.eedu;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.EDUEntity;
import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.exception.OccupiedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * This abstract class is used for testing with the framework Mockito to mock objects of service class type {@code S}
 * which is a subtype of {@link EntityService}. {@code E} is the entity type derived from {@link EDUEntity}, {@code M} is a
 * Model class and
 * {@code C} is a class for CreationModel of {@code E}.
 *
 * @param <S> Represents the service class which extends {@link EntityService}.
 * @param <E> Represents the entity class which extends {@link EDUEntity}.
 * @param <M> Represents the Model class.
 * @param <C> Represents the CreationModel of {@code E}.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class ServiceMockitoTest<S extends EntityService<E, M, C>, E extends EDUEntity, M extends Model, C extends CreationModel<E>>
{
    public S entityService;

    public @NotNull S getEntityService()
    {
        return entityService;
    }

    @Contract(pure = true) protected abstract Class<S> serviceClass();

    @BeforeEach public void initMock()
    {
        entityService = Mockito.mock(serviceClass());
    }

    protected abstract @NotNull ServiceMockitoTest.TestExpectation<C, M> successData();

    protected abstract <T extends OccupiedException> @NotNull TestExpectation<C, T> occupiedData();

    @Test public void testCreateEntitySuccessMockito()
    {
        TestExpectation<C, M> data = successData();

        Mockito.when(getEntityService().create(data.request())).thenReturn(data.expected());
        getEntityService().create(data.request());
        Mockito.verify(getEntityService(), Mockito.times(1)).create(data.request());
    }

    @Test public <T extends OccupiedException> void testCreateEntityOccupiedMockito()
    {
        TestExpectation<C, T> data = occupiedData();
        Mockito.when(getEntityService().create(data.request())).thenThrow(data.expected());
        try
        {
            getEntityService().create(data.request());
        }
        catch (OccupiedException occupiedException)
        {
            Mockito.verify(getEntityService(), Mockito.times(1)).create(data.request());
        }
    }

    @Test public void testDeleteEntityMockito()
    {
        Mockito.when(getEntityService().delete(4L)).thenReturn(true);
        getEntityService().delete(4L);
        Mockito.verify(getEntityService(), Mockito.times(1)).delete(4L);
    }

    protected record TestExpectation<R, E>(@NotNull R request, @NotNull E expected)
    {
        @Contract("_, _ -> new") public static <R, E> @NotNull TestExpectation<R, E> data(@NotNull R request, @NotNull E expected)
        {
            return new TestExpectation<>(request, expected);
        }
    }
}
