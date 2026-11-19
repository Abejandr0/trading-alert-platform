# Plataforma de Trading y Alertas de Inversión

Monorepo Maven con Java 17 y Spring Boot 3.5.16. El feed distribuye ticks por RabbitMQ: un consumidor los envía al ticker por WebSocket y otro los transfiere a Kafka. El motor analítico guarda el historial en PostgreSQL, calcula señales y las publica en Kafka. El router distribuye cada señal a los workers de RabbitMQ.

## Arquitectura

```text
market_data_feed ─ RabbitMQ market_ticks ─┬─ live_dashboard_backend ─ WebSocket :8081
                                         └─ ingestion_bridge ─ Kafka historical_ticks
                                                             └─ analytics_engine ─ PostgreSQL
                                                                                 └─ Kafka trading_signals
                                                                                    ├─ investment_dashboard_backend ─ WebSocket :8082
                                                                                    └─ notification_router ─ RabbitMQ
                                                                                       ├─ email_queue ─ email_worker ×2
                                                                                       └─ console_alerts ─ console_alert_worker
```

RabbitMQ distribuye precios transitorios sin consultar la base de datos en la ruta caliente. Kafka conserva el historial y ordena los ticks de cada activo por su clave de partición. PostgreSQL audita ticks, señales y despachos; la tabla de salida permite reintentar publicaciones de señales sin perder el registro transaccional.

## Iniciar

```bash
docker compose up --build -d
docker compose ps
```

- Ticker: http://localhost:8081/live_ticker.html
- Señales: http://localhost:8082/investment.html
- RabbitMQ: http://localhost:15672 (usuario y clave locales: `trader`)

El primer pico de BTC ocurre en el tick 20 del feed, unos 20 segundos después de iniciarlo. Para seguir el flujo:

```bash
docker compose logs -f analytics_engine notification_router email_worker console_alert_worker
```

`docker compose down` detiene los servicios y conserva los volúmenes de PostgreSQL, Kafka y ZooKeeper. `docker compose down -v` también borra esos datos.

## Módulos

| Módulo | Función |
| --- | --- |
| `common-contracts` | Eventos y códigos inmutables, sin Spring |
| `market_data_feed` | Escenarios configurables y publicación RabbitMQ |
| `live_dashboard_backend` | RabbitMQ → WebSocket, sin base de datos |
| `ingestion_bridge` | RabbitMQ → Kafka con clave de partición por activo |
| `analytics_engine` | SMA, estados, JPA y salida transaccional de señales |
| `investment_dashboard_backend` | Kafka → WebSocket y última señal por activo |
| `notification_router` | Kafka → cola de email y fanout de consola; audita despachos en PostgreSQL |
| `email_worker` | Envío simulado por log; dos réplicas |
| `console_alert_worker` | Alerta de consola |

## Reglas

- El feed produce BTC, ETH, SOL, EUR/USD y GOOGL. En `market_data_feed/src/main/resources/application.yml` se configuran precio base, volatilidad, período y factor de pico; la semilla hace reproducible la simulación.
- Los contratos usan `BigDecimal` e `Instant` y validan los datos al construirlos.
- Los eventos publican `asset` como texto (`{"asset":"BTC", ...}`). Los consumidores siguen leyendo el formato anterior `assetCode.value` almacenado en Kafka.
- La SMA incluye los cinco ticks más recientes, incluido el actual. Se genera `BUY` si `precio > SMA × 1,05`.
- Tras la alerta, se ignoran diez ticks; el undécimo se vuelve a evaluar.
- Flyway crea `ticks`, `signals`, `signal_outbox` y `notification_dispatches`. Las tablas históricas y de despacho tienen índices `(asset_code, timestamp DESC)`; los ticks tienen además una clave única `(asset_code, timestamp)`.
- El motor omite ticks reentregados. Al reiniciar, recupera los últimos cuatro precios y el cooldown restante desde PostgreSQL.
- La cola del ticker es transitoria, caduca tras cinco segundos y conserva como máximo 50 mensajes. El envío WebSocket usa una cola acotada para que un cliente lento no frene el consumo RabbitMQ.
- La entrega entre brokers es al menos una vez: un fallo entre publicación y confirmación puede duplicar un evento.
- El estado operativo reside en memoria y se reconstruye desde el historial al reiniciar. Los ocho servicios asignados no incluyen un flujo de órdenes.

## Desarrollo

```bash
./mvnw clean verify
docker compose config --quiet
```

Las pruebas JUnit 5 y Mockito son unitarias, sin contexto Spring ni brokers. Cada servicio tiene Dockerfile multietapa con usuario no privilegiado. Compose limita CPU y RAM, y la JVM usa `-XX:MaxRAMPercentage=75.0`. Las credenciales locales pueden cambiarse con `DB_USER`, `DB_PASSWORD`, `RABBITMQ_USER` y `RABBITMQ_PASSWORD`.
