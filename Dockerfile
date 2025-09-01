# Utiliza una imagen oficial de Maven con Java 21 para construir la app
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q clean package -DskipTests

# Utiliza una imagen ligera de Java 21 para ejecutar la app
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copia el JAR compilado desde la etapa de construcción
COPY --from=build /app/target/urlobject-1.0-SNAPSHOT.jar app.jar

# Expone el puerto por defecto
ENV PORT=35000
EXPOSE 35000

# Ejecuta la aplicación
CMD ["sh", "-lc", "exec java -jar app.jar"]
