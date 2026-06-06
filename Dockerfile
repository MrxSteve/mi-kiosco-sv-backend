FROM maven:3.9-eclipse-temurin-21-alpine AS deps

WORKDIR /build
COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B --no-transfer-progress

FROM deps AS builder

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests -B --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder /build/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
