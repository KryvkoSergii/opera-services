# ---------- build stage ----------
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

COPY build.gradle* settings.gradle* gradle.properties gradlew gradlew.bat /app/
COPY gradle /app/gradle

RUN ./gradlew --no-daemon dependencies || true

COPY . /app

RUN ./gradlew --no-daemon clean bootJar -x test

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -m appuser
USER appuser

COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-Duser.timezone=UTC"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
