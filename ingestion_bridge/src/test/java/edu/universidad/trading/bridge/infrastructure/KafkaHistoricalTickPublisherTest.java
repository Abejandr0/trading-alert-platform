package edu.universidad.trading.bridge.infrastructure;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class KafkaHistoricalTickPublisherTest {
    @Mock KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void usesAssetCodeAsPartitionKey() throws Exception {
        TickPriceRecord tick = new TickPriceRecord(new AssetCode("BTC"), new BigDecimal("60000"),
                Instant.parse("2026-01-01T00:00:00Z"));
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        String json = objectMapper.writeValueAsString(tick);
        when(kafkaTemplate.send("historical_ticks", "BTC", json))
                .thenReturn(CompletableFuture.<SendResult<String, String>>completedFuture(null));

        new KafkaHistoricalTickPublisher(kafkaTemplate, objectMapper).publish(tick);

        verify(kafkaTemplate).send("historical_ticks", "BTC", json);
    }
}
