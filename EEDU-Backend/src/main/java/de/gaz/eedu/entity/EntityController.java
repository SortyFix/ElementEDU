package de.gaz.eedu.entity;

import de.gaz.eedu.entity.model.CreationModel;
import de.gaz.eedu.entity.model.EntityModel;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.verification.JwtTokenType;
import de.gaz.eedu.user.verification.authority.VerificationAuthority;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j @AllArgsConstructor
public abstract class EntityController<S extends EntityService<?, ?, M, C>, M extends EntityModel, C extends CreationModel<?>> extends EntityExceptionHandler
{
    protected abstract @NotNull S getService();

    /**
     * This method is responsible for creating a new entity based on the provided model.
     * It is executed when a POST request hits the API endpoint associated with this method.
     *
     * @param model The model that represents the entity to be created. Must be not null.
     *              The model should include all necessary information required to create the entity.
     * @return A ResponseEntity object that encapsulates the HTTP response.
     * If the entity is successfully created,
     * the method returns a ResponseEntity with HTTP status 201 (Created),
     * and the body of the ResponseEntity will be the created entity.
     * <p>
     * In case an exception occurs during the creation (e.g. due to validation errors),
     * a CreationException is caught and handled. In such scenario, the method returns
     * a ResponseEntity with an HTTP status code associated with the error, and its body is null.
     * @throws CreationException If there is a problem with creating the entity,
     *                           this exception will be thrown. It contains the HTTP status code for the error response.
     */
    public @NotNull ResponseEntity<M[]> create(@NotNull C[] model) throws CreationException
    {
        log.info("Received an incoming create request from class {}.", getClass().getSuperclass());
        try
        {
            List<M> created = getService().create(new HashSet<>(List.of(model)));
            M[] models = created.toArray((M[]) Array.newInstance(created.getFirst().getClass(), created.size()));
            return ResponseEntity.status(HttpStatus.CREATED).body(models);
        }
        catch (CreationException creationException)
        {
            return ResponseEntity.status(creationException.getStatusCode()).body(null);
        }
    }

    /**
     * This method is responsible for deleting an existing entity based on the provided id.
     * It is executed when a DELETE request hits the API endpoint associated with this method.
     *
     * @param id The id of the entity to be deleted. Must be not null.
     * @return A Boolean value. If the deletion is successful, the method returns true.
     * Otherwise, it returns false (e.g. if no entity with the given id exists).
     */
    public @NotNull HttpStatus delete(@NotNull Long... id)
    {
        log.info("Received an incoming delete request from class {} with id(s) {}.", getClass().getSuperclass(), id);
        return getService().delete(id) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
    }

    /**
     * This method is responsible for fetching an existing entity based on the provided id.
     * It is executed when a GET request hits the API endpoint associated with this method.
     *
     * @param id The id of the entity to be fetched. Must be not null.
     * @return A ResponseEntity object that encapsulates the HTTP response.
     * If the entity is successfully fetched,
     * the method returns a ResponseEntity with HTTP status 200 (Ok),
     * and the body of the ResponseEntity will be the fetched entity.
     * <p>
     * If no entity with the given id exists, it returns a ResponseEntity
     * with HTTP status 404 (Not Found), and its body is null.
     */
    public @NotNull ResponseEntity<M> getData(@NotNull Long id)
    {
        log.info("Received an incoming get request from class {} with id {}.", getClass().getName(), id);
        return getService().loadById(id).map(ResponseEntity::ok) .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    public @NotNull ResponseEntity<Set<M>> fetchAll()
    {
        return fetchAll((m) -> true);
    }

    public @NotNull final ResponseEntity<Set<M>> fetchAll(@NotNull Predicate<M> predicate)
    {
        log.info("Received an incoming get all request from class {}.", getClass().getSuperclass());
        return ResponseEntity.ok(getService().findAll(predicate));
    }

    protected boolean isAuthorized(@NotNull String authority)
    {
        return isAuthorized(authority, SimpleGrantedAuthority.class);
    }

    protected boolean isAuthorized(@NotNull JwtTokenType jwtTokenType)
    {
        return isAuthorized(jwtTokenType.getAuthority().getAuthority(), VerificationAuthority.class);
    }

    protected boolean isAuthorized(@NotNull String authority, @NotNull Class<? extends GrantedAuthority> parent)
    {
        return getAuthentication().getAuthorities()
                                  .stream()
                                  .filter(grantedAuthority -> parent.isAssignableFrom(grantedAuthority.getClass()))
                                  .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

    protected @NotNull Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
