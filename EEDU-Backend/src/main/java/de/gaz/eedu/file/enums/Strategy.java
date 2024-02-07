package de.gaz.eedu.file.enums;

public enum Strategy
{
    /**
     * Delete everything in directory, including subdirectories.
     */
    EVERYTHING,
    /**
     * Deletes all files in directory and subdirectories, but deletes
     * no subdirectories.
     */
    FILES_ONLY,
    /**
     * Only delete files in given directory. Ignores subdirectories.
     */
    DIRECT,
    /**
     * Delete all files in subdirectories.
     */
    SUBDIRECT,
}
