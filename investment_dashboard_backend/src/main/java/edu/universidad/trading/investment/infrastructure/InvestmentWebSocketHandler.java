package edu.universidad.trading.investment.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.investment.application.InvestmentSignalPublisher;
import edu.universidad.trading.investment.application.SignalBook;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public final class InvestmentWebSocketHandler extends TextWebSocketHandler implements InvestmentSignalPublisher {
    private final ObjectMapper objectMapper;
    private final SignalBook signalBook;
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public InvestmentWebSocketHandler(ObjectMapper objectMapper, SignalBook signalBook) {
        this.objectMapper = objectMapper;
        this.signalBook = signalBook;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(session, 1000, 64 * 1024);
        sessions.add(safeSession);
        for (SignalPayloadRecord signal : signalBook.snapshot()) {
            safeSession.sendMessage(toMessage(signal));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.removeIf(active -> active.getId().equals(session.getId()));
    }

    @Override
    public void publish(SignalPayloadRecord signal) {
        TextMessage message = toMessage(signal);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException ex) {
                sessions.remove(session);
            }
        }
    }

    private TextMessage toMessage(SignalPayloadRecord signal) {
        try {
            return new TextMessage(objectMapper.writeValueAsString(signal));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("cannot serialize signal", ex);
        }
    }
}
