# syntax=docker/dockerfile:1.7

# ---------- build stage ----------
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

COPY --from=node:22-bookworm-slim /usr/local/ /usr/local/
RUN node -v && npm -v && npx -v

RUN ln -sf /usr/local/lib/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm \
 && ln -sf /usr/local/lib/node_modules/corepack/dist/corepack.js /usr/local/bin/corepack \
 && node --version \
 && npm --version

COPY contract /app/contract
COPY backend/analysis /app

RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon dependencies || true

RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon clean bootJar -x test --info --stacktrace

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

USER root

# ffmpeg install with apt cache mounts (BuildKit)
RUN --mount=type=cache,target=/var/cache/apt \
    --mount=type=cache,target=/var/lib/apt/lists \
    apt-get update \
 && apt-get install -y --no-install-recommends ffmpeg ca-certificates \
 && rm -rf /var/lib/apt/lists/*

RUN useradd -m appuser
USER appuser

COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-Duser.timezone=UTC"
ENV SPRING_PROFILES_ACTIVE=cloud

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]