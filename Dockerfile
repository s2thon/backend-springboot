# Adım 1: Uygulamayı Maven ile derle
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Adım 2: Derlenmiş uygulamayı hafif bir Java ortamına taşı
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]```

#### Adım 1.2: `backend-fastapi` için `Dockerfile`

`backend-fastapi` projenizin ana dizinine `Dockerfile` adında yeni bir dosya oluşturun ve içine şunu yapıştırın:

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .

EXPOSE 8001
CMD ["uvicorn", "ai-service.main:app", "--host", "0.0.0.0", "--port", "8001"]