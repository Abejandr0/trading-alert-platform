package edu.universidad.trading.email.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.email.application.EmailWorkerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public final class EmailSignalListener {
    private final ObjectMapper objectMapper;
    private final EmailWorkerService service;

    public EmailSignalListener(ObjectMapper objectMapper, EmailWorkerService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @RabbitListener(queues = "email_queue")
    public void receive(String json) throws JsonProcessingException {
        service.dispatch(objectMapper.readValue(json, SignalPayloadRecord.class));
    }
}
