FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace
COPY settings.gradle.kts build.gradle.kts ./
COPY src ./src
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S noizy && adduser -S noizy -G noizy
COPY --from=build /workspace/build/libs/*.jar /app/app.jar
USER noizy
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
