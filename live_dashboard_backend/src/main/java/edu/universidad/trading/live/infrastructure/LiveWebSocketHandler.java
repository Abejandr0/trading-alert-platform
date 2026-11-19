package edu.universidad.trading.live.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.TickPriceRecord;
import edu.universidad.trading.live.application.LiveTickPublisher;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public final class LiveWebSocketHandler extends TextWebSocketHandler implements LiveTickPublisher {
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ThreadPoolExecutor sender = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
            task -> {
                Thread thread = new Thread(task, "live-websocket-sender");
                thread.setDaemon(true);
                return thread;
            },
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public LiveWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(new ConcurrentWebSocketSessionDecorator(session, 1000, 64 * 1024));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.removeIf(active -> active.getId().equals(session.getId()));
    }

    @Override
    public void publish(TickPriceRecord tick) {
        if (sessions.isEmpty()) {
            return;
        }
        final TextMessage message;
        try {
            message = new TextMessage(objectMapper.writeValueAsString(tick));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("cannot serialize tick", ex);
        }
        sender.execute(() -> broadcast(message));
    }

    private void broadcast(TextMessage message) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException | IllegalStateException ex) {
                sessions.remove(session);
            }
        }
    }

    @PreDestroy
    public void stop() {
        sender.shutdownNow();
    }
}
