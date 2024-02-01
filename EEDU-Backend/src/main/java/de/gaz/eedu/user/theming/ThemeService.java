package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.EntityUnknownException;
import de.gaz.eedu.exception.NameOccupiedException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service @AllArgsConstructor
public class ThemeService implements EntityService<ThemeEntity, ThemeModel, ThemeCreateModel, ThemeRepository>
{
    private final ThemeRepository themeRepository;

    @Override
    public @NotNull ThemeRepository getRepository()
    {
        return themeRepository;
    }

    @Override
    public @NotNull ThemeEntity createEntity(@NotNull ThemeCreateModel model) throws CreationException
    {
        themeRepository.findByName(model.name()).ifPresent(occupied ->
        {
            throw new NameOccupiedException(occupied.getName());
        });
        return themeRepository.save(model.toEntity(new ThemeEntity()));
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
