FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /workspace
ARG MODULE
COPY pom.xml ./
COPY common-contracts/pom.xml common-contracts/pom.xml
COPY market_data_feed/pom.xml market_data_feed/pom.xml
COPY live_dashboard_backend/pom.xml live_dashboard_backend/pom.xml
COPY ingestion_bridge/pom.xml ingestion_bridge/pom.xml
COPY analytics_engine/pom.xml analytics_engine/pom.xml
COPY investment_dashboard_backend/pom.xml investment_dashboard_backend/pom.xml
COPY notification_router/pom.xml notification_router/pom.xml
COPY email_worker/pom.xml email_worker/pom.xml
COPY console_alert_worker/pom.xml console_alert_worker/pom.xml
RUN --mount=type=cache,id=trading-m2,target=/root/.m2,sharing=locked \
    mvn -q -B -pl "${MODULE}" -am dependency:go-offline
COPY . .
RUN --mount=type=cache,id=trading-m2,target=/root/.m2,sharing=locked \
    mvn -q -B -pl "${MODULE}" -am clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
ARG MODULE
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=build /workspace/${MODULE}/target/${MODULE}-0.1.0-SNAPSHOT.jar /app/app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
USER appuser:appgroup
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
