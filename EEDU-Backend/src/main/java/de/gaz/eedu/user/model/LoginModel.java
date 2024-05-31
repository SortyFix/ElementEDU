package de.gaz.eedu.user.model;

import de.gaz.eedu.entity.model.Model;
import org.jetbrains.annotations.NotNull;

public interface LoginModel extends Model
{

    @NotNull String loginName();

}
