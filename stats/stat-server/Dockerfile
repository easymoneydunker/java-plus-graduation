# Указываем базовый образ с JDK 21
FROM openjdk:21-jdk-slim

# Настраиваем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем собранный JAR файл в контейнер
COPY target/stat-server*.jar /app/stat-server.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "/app/stat-server.jar"]