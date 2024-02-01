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
     * Configures the message broker for WebSocket communication in the application.
     *
     * Enables a simple message broker with the specified destinations and sets the application destination prefixes.
     * The simple message broker allows the application to send messages to connected clients and subscribe to specific
     * destination prefixes for message broadcasting.
     *
     * @param config The MessageBrokerRegistry to be configured.
     *
     * @throws IllegalArgumentException if the provided MessageBrokerRegistry (config) is {@code null}.
     *
     * @see MessageBrokerRegistry#enableSimpleBroker(String...)
     * @see MessageBrokerRegistry#setApplicationDestinationPrefixes(String...)
     */
    @Override
    public void configureMessageBroker(@NotNull MessageBrokerRegistry config){
        config.enableSimpleBroker(broker);
        config.setApplicationDestinationPrefixes(apd);
    }

    /**
     * Registers STOMP (Simple Text Oriented Messaging Protocol) endpoints for WebSocket communication in the application.
     *
     * STOMP is a messaging protocol that defines the format and rules for data exchange over WebSocket.
     * This method configures the WebSocket endpoint, allows cross-origin resource sharing, and adds SockJS support
     * for fallback options in case WebSocket is not supported by the client.
     *
     * @param registry The StompEndpointRegistry to register the STOMP endpoint with.
     *
     * @throws IllegalArgumentException if the provided StompEndpointRegistry (registry) is {@code null}.
     *
     * @see <a href="https://stomp.github.io/stomp-specification-1.2.html">STOMP Specification</a>
     * @see <a href="https://github.com/sockjs/sockjs-client">SockJS GitHub Repository</a>
     */
    @Override
    public void registerStompEndpoints(@NotNull StompEndpointRegistry registry){
        registry.addEndpoint(endpoint).setAllowedOrigins("*").withSockJS();
    }
}
