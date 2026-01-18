# ---------- build stage ----------
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

COPY contract /app/contract
COPY backend/analysis /app

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl ca-certificates \
 && curl -fsSL https://deb.nodesource.com/setup_22.x | bash - \
 && apt-get install -y --no-install-recommends nodejs \
 && node --version \
 && npm --version \
 && rm -rf /var/lib/apt/lists/*

RUN ./gradlew --no-daemon dependencies || true
RUN ./gradlew --no-daemon clean bootJar -x test --info --stacktrace

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

USER root

RUN apt-get update \
 && apt-get install -y --no-install-recommends ffmpeg \
 && rm -rf /var/lib/apt/lists/*

RUN useradd -m appuser
USER appuser

COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-Duser.timezone=UTC"
ENV SPRING_PROFILES_ACTIVE=cloud

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
