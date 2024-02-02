package de.gaz.eedu.course.subjects.model;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

public record SubjectModel(@NotNull Long id, @NotNull String name) implements EntityModel { }
