package edu.universidad.trading.analytics.infrastructure;

import edu.universidad.trading.analytics.domain.IndicatorStrategy;
import edu.universidad.trading.analytics.domain.SmaStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndicatorConfiguration {
    @Bean
    IndicatorStrategy indicatorStrategy() {
        return new SmaStrategy(5);
    }
}
