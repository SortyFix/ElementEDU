package de.gaz.eedu.livechat;
import org.junit.jupiter.api.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@SpringJUnitConfig(classes = WebsocketTest.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebsocketTest
{
    private BlockingQueue<String> blockingQueue;
    private WebSocketStompClient stompClient;

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory(0);
    }

    @Bean
    public WSIdentifiers wsIdentifiers(){
        return new WSIdentifiers();
    }

    @BeforeEach
    public void wsSetup()
    {
        TestRestTemplate restTemplate = new TestRestTemplate();
        blockingQueue = new LinkedBlockingQueue<>();
        SockJsClient sockJsClient = new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient())));
        stompClient = new WebSocketStompClient(sockJsClient);
    }

    @Test
    // This test needs to run with a Spring Boot Application/Server running in the background, as Websockets require one.
    public void wsMessageRecieveTest()
    {
        try{
            StompSession session = stompClient.connectAsync(getWebsocketURL(), new StompSessionHandlerAdapter() {})
                                              .get(1, TimeUnit.SECONDS);
            session.subscribe(wsIdentifiers().getBroker(), new DefaultStompFrameHandler());

            String testMessage = "pls work";
            session.send(wsIdentifiers().getBroker(), testMessage.getBytes());
            Assertions.assertEquals(testMessage, blockingQueue.poll(1, TimeUnit.SECONDS));
        }
        catch (Exception exception)
        {
            Assumptions.abort("Spring Boot Application is not currently running.");
        }
    }

    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public @NotNull Type getPayloadType(@NotNull StompHeaders headers)
        {
            return byte[].class;
        }

        @Override
        public void handleFrame(@NotNull StompHeaders headers, Object payload)
        {
            blockingQueue.offer(new String((byte[]) payload));
        }
    }

    public String getWebsocketURL()
    {
        return String.format("ws://localhost:8080/%s", wsIdentifiers().getEndpoint());
    }
}
