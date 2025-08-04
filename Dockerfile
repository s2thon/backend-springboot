# Adım 1: Uygulamayı Maven ile derle
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Adım 2: Derlenmiş .jar dosyasını hafif bir Java runtime ortamına kopyala
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Uygulama 8080 portunu kullanacak
EXPOSE 8080

# Konteyner başladığında uygulamayı çalıştır
ENTRYPOINT ["java", "-jar", "app.jar"]```