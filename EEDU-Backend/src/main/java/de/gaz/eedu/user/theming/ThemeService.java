package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class ThemeService implements EntityService<ThemeRepository, ThemeEntity, ThemeModel, ThemeCreateModel>
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
        getRepository().findByName(model.name()).ifPresent(occupied ->
        {
            throw new NameOccupiedException(occupied.getName());
        });
        return getRepository().save(model.toEntity(new ThemeEntity()));
    }
}
