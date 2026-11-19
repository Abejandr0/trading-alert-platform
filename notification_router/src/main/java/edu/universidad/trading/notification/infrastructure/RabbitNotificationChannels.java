package edu.universidad.trading.notification.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.notification.application.NotificationChannels;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public final class RabbitNotificationChannels implements NotificationChannels {
    private static final long CONFIRM_TIMEOUT_SECONDS = 5;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitNotificationChannels(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void enqueueEmail(SignalPayloadRecord signal) {
        sendConfirmed("", "email_queue", signal, "email");
    }

    @Override
    public void broadcastConsole(SignalPayloadRecord signal) {
        sendConfirmed("console_alerts", "", signal, "console");
    }

    private void sendConfirmed(String exchange, String routingKey, SignalPayloadRecord signal, String channel) {
        CorrelationData correlation = new CorrelationData(signal.eventId() + ":" + channel);
        rabbitTemplate.convertAndSend(exchange, routingKey, json(signal), correlation);
        try {
            CorrelationData.Confirm confirm = correlation.getFuture()
                    .get(CONFIRM_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!confirm.isAck()) {
                throw new IllegalStateException("RabbitMQ rejected " + channel + " notification: " + confirm.getReason());
            }
            if (correlation.getReturned() != null) {
                throw new IllegalStateException("RabbitMQ could not route " + channel + " notification");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while waiting for RabbitMQ " + channel + " confirmation", ex);
        } catch (ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("did not receive RabbitMQ " + channel + " confirmation", ex);
        }
    }

    private String json(SignalPayloadRecord signal) {
        try {
            return objectMapper.writeValueAsString(signal);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("cannot serialize signal", ex);
        }
    }
}
