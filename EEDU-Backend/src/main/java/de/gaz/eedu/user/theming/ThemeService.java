package de.gaz.eedu.user.theming;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.exception.NameOccupiedException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.ui.context.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service @AllArgsConstructor
public class ThemeService extends EntityService<Long, ThemeRepository, ThemeEntity, ThemeModel, ThemeCreateModel>
{
    private final ThemeRepository themeRepository;

    @Override
    public @NotNull ThemeRepository getRepository()
    {
        return themeRepository;
    }

    @Override
    public @NotNull List<ThemeEntity> createEntity(@NotNull Set<ThemeCreateModel> model) throws CreationException
    {
        List<ThemeEntity> entities = model.stream().map(createModel -> {
            getRepository().findByName(createModel.name()).ifPresent(occupied ->
            {
                throw new NameOccupiedException(occupied.getName());
            });
            return createModel.toEntity(new ThemeEntity());
        }).toList();
        return getRepository().saveAll(entities);
    }
}
