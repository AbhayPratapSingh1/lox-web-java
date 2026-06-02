# ---------------- FRONTEND ----------------
FROM node:20 AS frontend
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build


# ---------------- BACKEND BUILD ----------------
FROM gradle:8-jdk21 AS backend
WORKDIR /app

COPY backend/ .

COPY --from=frontend /app/dist src/main/resources/META-INF/resources

RUN gradle clean build -x test


# ---------------- RUNTIME ----------------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=backend /app/build/quarkus-app/ .

EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]