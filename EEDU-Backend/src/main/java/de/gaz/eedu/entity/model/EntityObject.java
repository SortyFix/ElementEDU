package de.gaz.eedu.entity.model;


public interface EntityObject
{
    default boolean deleteManagedRelations() { return false; }
}
