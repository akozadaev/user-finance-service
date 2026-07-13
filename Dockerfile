FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src
RUN chmod +x mvnw && ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S application && adduser -S application -G application
WORKDIR /app
COPY --from=builder --chown=application:application /build/target/user-finance-service-*.jar application.jar
USER application
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
