package de.gaz.eedu.course.model;

import de.gaz.eedu.course.subjects.model.SubjectModel;
import de.gaz.eedu.entity.model.EntityModel;
import de.gaz.eedu.user.model.UserModel;
import org.jetbrains.annotations.NotNull;

public record CourseModel(@NotNull Long id, @NotNull String name, @NotNull SubjectModel subject,
                          @NotNull UserModel[] users) implements EntityModel { }
