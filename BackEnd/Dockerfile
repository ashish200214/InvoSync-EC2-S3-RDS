# -------- Build stage --------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /backend
COPY . .
RUN mvn clean package -DskipTests

# -------- Run stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /backend/target/invoSync-v1.jar ./invoSync-v1.jar

CMD ["java", "-jar", "invoSync-v1.jar"]
