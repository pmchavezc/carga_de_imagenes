# Imagen base con JDK
FROM openjdk:17-jre-slim

# Directorio de trabajo en el contenedor
WORKDIR /app

# Copia el jar generado dentro del contenedor
COPY target/demo-0.0.1-SNAPSHOT.war app.jar

# Expone el puerto que usa la app (cámbialo según tu app)
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]