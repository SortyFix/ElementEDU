package de.gaz.eedu.entity.model;


import org.jetbrains.annotations.NotNull;

public interface EntityObject<P>
{
    @NotNull P getId();

    default boolean deleteManagedRelations() { return false; }

    // TODO javadoc (delete safe)
    default boolean isDeletable()
    {
        return true;
    }
}
