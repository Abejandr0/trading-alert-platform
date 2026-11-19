package edu.universidad.trading.feed.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.TickPriceRecord;
import edu.universidad.trading.feed.application.TickPublisher;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public final class RabbitTickPublisher implements TickPublisher {
    private static final long CONFIRM_TIMEOUT_SECONDS = 5;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitTickPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(TickPriceRecord tick, String routingKey) {
        try {
            CorrelationData correlation = new CorrelationData(
                    tick.assetCode().value() + ":" + tick.timestamp());
            rabbitTemplate.convertAndSend(
                    "market_ticks", routingKey, objectMapper.writeValueAsString(tick), correlation);
            var confirm = correlation.getFuture().get(CONFIRM_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!confirm.isAck()) {
                throw new IllegalStateException("RabbitMQ rejected tick: " + confirm.getReason());
            }
            if (correlation.getReturned() != null) {
                throw new IllegalStateException("RabbitMQ could not route tick for " + tick.assetCode());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while waiting for RabbitMQ tick confirmation", ex);
        } catch (ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("did not receive RabbitMQ tick confirmation", ex);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("cannot serialize tick", ex);
        }
    }
}
