package edu.universidad.trading.analytics.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.analytics.application.AnalyticsService;
import edu.universidad.trading.contracts.TickPriceRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public final class KafkaHistoricalTickListener {
    private final ObjectMapper objectMapper;
    private final AnalyticsService service;

    public KafkaHistoricalTickListener(ObjectMapper objectMapper, AnalyticsService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @KafkaListener(topics = "historical_ticks", groupId = "analytics-engine")
    public void receive(String json) throws JsonProcessingException {
        service.process(objectMapper.readValue(json, TickPriceRecord.class));
    }
}
