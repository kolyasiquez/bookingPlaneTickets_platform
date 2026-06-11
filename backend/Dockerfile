# Build stage
FROM maven:3.8.4-openjdk-8-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM payara/server-full:5.2022.5
USER root
RUN mkdir -p /app/uploads && chown -R payara:payara /app/uploads
USER payara
COPY --from=builder /app/target/airline-service.war $DEPLOY_DIR
