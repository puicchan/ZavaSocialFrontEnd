# =============================================================================
# Zava Social — Java Frontend (JSP/Servlets on Tomcat)
# =============================================================================
# Multi-stage build: Maven compile → Tomcat runtime
# Java 8 runtime is fully compatible with Java 6-era source code.
#
# NOTE: This Dockerfile expects pom.xml and src/ to exist in zava-web/.
# Shuri is building those in parallel — until then, the build stage will fail.
# =============================================================================

# ---------------------------------------------------------------------------
# Stage 1: Build with Maven
# ---------------------------------------------------------------------------
FROM maven:3.8-eclipse-temurin-8 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source and build the WAR
COPY src/ src/
RUN mvn clean package -DskipTests -B

# ---------------------------------------------------------------------------
# Stage 2: Runtime — Tomcat 8.5 (supports Servlet 3.1, runs Java 6-era code)
# ---------------------------------------------------------------------------
FROM tomcat:8.5-jre8

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR from build stage — deployed as ROOT so it serves at /
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
