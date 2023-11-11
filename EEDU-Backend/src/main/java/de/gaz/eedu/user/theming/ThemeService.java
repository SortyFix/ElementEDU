package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.NameOccupiedException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service @AllArgsConstructor
public class ThemeService implements EntityService<ThemeEntity, ThemeModel, ThemeCreateModel>
{
    private final ThemeRepository themeRepository;

    @Override
    public @NotNull Optional<ThemeEntity> loadEntityByID(long id)
    {
        return themeRepository.findById(id);
    }

    @Override
    public @NotNull Optional<ThemeEntity> loadEntityByName(@NotNull String name)
    {
        return themeRepository.findByName(name);
    }


    @Override
    public @Unmodifiable @NotNull List<ThemeEntity> findAllEntities()
    {
        return themeRepository.findAll();
    }

    @Override
    public @NotNull ThemeEntity createEntity(@NotNull ThemeCreateModel model) throws CreationException
    {
        themeRepository.findByName(model.name()).ifPresent(occupied ->
        {
            throw new NameOccupiedException(occupied.getName());
        });
        return themeRepository.save(model.toEntity());
    }

    @Override
    public boolean delete(long id)
    {
        return themeRepository.findById(id).map(userEntity ->
        {
            themeRepository.deleteById(id);
            return true;
        }).orElse(false);
    }


    @Override
    public @NotNull ThemeEntity saveEntity(@NotNull ThemeEntity entity)
    {
        return themeRepository.save(entity);
    }

    @Override @Transactional
    public @NotNull Function<ThemeModel, ThemeEntity> toEntity()
    {
        return themeModel -> loadEntityByID(themeModel.id()).orElseThrow(() -> new EntityUnknownException(themeModel.id()));
    }

    @Override
    public @NotNull Function<ThemeEntity, ThemeModel> toModel()
    {
        return ThemeEntity::toModel;
    }
}
