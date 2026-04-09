# =============================================================================
# Zava Social — Java Frontend (JSP/Servlets on Spring Boot 3.4 / Tomcat 10.x)
# =============================================================================
# Multi-stage build: Maven + Java 21 compile → Tomcat 10.x runtime
# Migrated from Java 1.6 / Tomcat 8.5 to Java 21 / Jakarta EE 10 (Tomcat 10.1)
# =============================================================================

# ---------------------------------------------------------------------------
# Stage 1: Build with Maven + Java 21
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source and build the WAR
COPY src/ src/
RUN mvn clean package -DskipTests -B

# ---------------------------------------------------------------------------
# Stage 2: Runtime — Tomcat 10.1 (Servlet 6.0 / Jakarta EE 10 / Java 21)
# ---------------------------------------------------------------------------
FROM tomcat:10.1-jre21

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR from build stage — deployed as ROOT so it serves at /
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
