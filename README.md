# Plataforma de Trading y Alertas de Inversión

Monorepo Maven para implementar, por fases, una ruta caliente RabbitMQ → WebSocket y una ruta fría RabbitMQ → Kafka → análisis → PostgreSQL/Kafka → notificaciones.

## Fase 1: contratos y dominio en memoria

```text
trading-alert-platform/
├── pom.xml                         # Reactor Maven, Java 17, Spring Boot 3.5.16
├── mvnw                            # Maven Wrapper 3.9.16
├── common-contracts/               # Solo tipos inmutables de Java; sin Spring
│   ├── pom.xml
│   └── src/{main,test}/java/edu/universidad/trading/contracts/
├── market_data_feed/               # Esqueleto Maven
├── live_dashboard_backend/         # Esqueleto Maven
├── ingestion_bridge/               # Esqueleto Maven
├── analytics_engine/               # Dominio puro + pruebas
│   ├── pom.xml
│   └── src/{main,test}/java/edu/universidad/trading/analytics/domain/
├── investment_dashboard_backend/   # Esqueleto Maven
├── notification_router/            # Esqueleto Maven
├── email_worker/                    # Esqueleto Maven
└── console_alert_worker/           # Esqueleto Maven
```

`common-contracts` contiene `AssetCode`, `TickPriceRecord`, `SignalType` y `SignalPayloadRecord`. Los precios se representan con `BigDecimal`; los eventos llevan `Instant` y rechazan datos inválidos al construirse.

El dominio de `analytics_engine` usa `IndicatorStrategy` con `SmaStrategy(5)`, `AssetTracker`, `TradingSignal` y los estados `NormalState`, `MomentumHighState` y `AlertFiredState`. La SMA incluye el tick actual. Una compra requiere `precio > SMA × 1,05`. Tras emitir una alerta, se ignoran exactamente los diez ticks siguientes; el tick número once vuelve a poder generar una señal.

Los otros siete módulos ya forman parte del reactor, pero todavía no tienen aplicación Spring Boot. Se implementarán por fases con sus adaptadores de transporte, persistencia y despliegue.

## Ejecutar pruebas

```bash
./mvnw clean test
```

Las pruebas JUnit 5 son unitarias y no arrancan Spring, Kafka, RabbitMQ ni PostgreSQL. `analytics_engine` declara Mockito como dependencia de prueba para las próximas pruebas de puertos y orquestadores.

## Fases siguientes

1. Feed configurable por escenarios y publicación RabbitMQ; dashboard de ticker en WebSocket.
2. Puente RabbitMQ → Kafka con clave de partición `assetCode`.
3. Orquestador analítico con puertos, persistencia JPA desacoplada e índices de auditoría.
4. Dashboard de inversiones, router de notificaciones y workers.
5. Dockerfiles multietapa, Compose con límites de CPU/RAM y pruebas de integración del flujo completo.
