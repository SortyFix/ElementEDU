package de.gaz.eedu.livechat;

import de.gaz.eedu.user.UserService;
import de.gaz.eedu.user.verification.VerificationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class WSInterceptor implements HandshakeInterceptor
{
    @Getter private final UserService userService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes) throws Exception
    {
        String query = request.getURI().getQuery();
        if(query != null && query.contains("token="))
        {
            String token = query.split("token=")[1];
            return getUserService().validate(token).isPresent();
        }
        log.info("Incoming handshake request could not be resolved.");
        return false;
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception)
    {
        log.info("Incoming handshake successfully completed.");
    }
}
