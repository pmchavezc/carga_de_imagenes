FROM ubuntu:latest
LABEL authors="Silvia Izabel"

ENTRYPOINT ["top", "-b"]
# Usa una imagen base con Java JDK
FROM eclipse-temurin:22-jdk-alpine

# Copia el JAR compilado al contenedor
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto donde corre la app (por ejemplo 8080)
EXPOSE 8080

# Comando para correr el JAR
ENTRYPOINT ["java", "-jar", "/app.jar"]
