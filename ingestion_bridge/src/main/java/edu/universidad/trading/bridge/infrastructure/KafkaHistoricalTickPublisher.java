package edu.universidad.trading.bridge.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.bridge.application.HistoricalTickPublisher;
import edu.universidad.trading.contracts.TickPriceRecord;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public final class KafkaHistoricalTickPublisher implements HistoricalTickPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaHistoricalTickPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(TickPriceRecord tick) {
        try {
            String json = objectMapper.writeValueAsString(tick);
            // La confirmación precede al ACK de RabbitMQ para no perder ticks al fallar Kafka.
            kafkaTemplate.send("historical_ticks", tick.assetCode().value(), json).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kafka publication interrupted", ex);
        } catch (JsonProcessingException | ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("Kafka publication failed", ex);
        }
    }
}
