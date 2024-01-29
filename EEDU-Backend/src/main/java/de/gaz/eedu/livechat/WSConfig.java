package de.gaz.eedu.livechat;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Getter
public class WSConfig implements WebSocketMessageBrokerConfigurer
{
    private final String broker = "/topic";
    private final String apd = "/app";
    private final String endpoint = "/ws-endpoint";

    /**
     *     The enableSimpleBroker method configures a simple in-memory broker that allows broadcasting
     *     messages to clients subscribed to specific topics (in this case, "/topic").
     *     <br> <br>
     *     The application destination prefix is a way to organize
     *     and route messages within the WebSocket connection.
     *     When clients send messages to the server, they need to specify
     *     destinations starting with the configured application
     *     destination prefix. This helps differentiate between messages intended
     *     for application-specific handling and those meant for broker-level handling.
     */
    @Override
    public void configureMessageBroker(@NotNull MessageBrokerRegistry config){
        config.enableSimpleBroker(broker);
        config.setApplicationDestinationPrefixes(apd);
    }

    /**
     * The endpoint is responsible for establishing the WebSocket
     * connection between the client and the server.
     * Can be contacted at <code>ws://localhost:8080/ws-endpoint</code>
     */
    @Override
    public void registerStompEndpoints(@NotNull StompEndpointRegistry registry){
        registry.addEndpoint(endpoint).setAllowedOrigins("*").withSockJS();
    }
}
