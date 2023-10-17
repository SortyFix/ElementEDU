package de.gaz.eedu.user.model;


import de.gaz.eedu.entity.model.SimpleModel;

public record SimpleUserModel(long id, String firstName, String lastName, String loginName,
                              boolean enabled, Boolean locked) implements SimpleModel {}
