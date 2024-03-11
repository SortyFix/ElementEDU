package de.gaz.eedu.livechat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class WSIdentifiers
{
    private final String broker = "/topic";
    private final String apd = "/app";
    private final String endpoint = "/ws-endpoint";
}
