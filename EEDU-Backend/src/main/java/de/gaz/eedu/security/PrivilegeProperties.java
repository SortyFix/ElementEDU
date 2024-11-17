package de.gaz.eedu.security;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "privilege")
@Getter(AccessLevel.PUBLIC)
public class PrivilegeProperties
{
    private Map<String, String> privileges;
}
