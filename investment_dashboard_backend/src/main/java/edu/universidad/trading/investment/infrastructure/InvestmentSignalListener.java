package edu.universidad.trading.investment.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.investment.application.InvestmentDashboardService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public final class InvestmentSignalListener {
    private final ObjectMapper objectMapper;
    private final InvestmentDashboardService service;

    public InvestmentSignalListener(ObjectMapper objectMapper, InvestmentDashboardService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @KafkaListener(topics = "trading_signals", groupId = "investment-dashboard")
    public void receive(String json) throws JsonProcessingException {
        service.onSignal(objectMapper.readValue(json, SignalPayloadRecord.class));
    }
}
