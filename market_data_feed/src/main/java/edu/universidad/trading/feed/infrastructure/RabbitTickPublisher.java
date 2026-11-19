package edu.universidad.trading.feed.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.TickPriceRecord;
import edu.universidad.trading.feed.application.TickPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public final class RabbitTickPublisher implements TickPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitTickPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(TickPriceRecord tick, String routingKey) {
        try {
            rabbitTemplate.convertAndSend("market_ticks", routingKey, objectMapper.writeValueAsString(tick));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("cannot serialize tick", ex);
        }
    }
}
