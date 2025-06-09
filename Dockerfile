# Sử dụng image chính thức của Maven để build project
FROM maven:3.8.5-openjdk-17 AS build

# Đặt thư mục làm việc
WORKDIR /app

# Copy toàn bộ mã nguồn vào image
COPY . .

# Build project sử dụng Maven
RUN mvn clean package -DskipTests

# Sử dụng một image nhẹ hơn để chạy ứng dụng
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/main-app/target/main-app-1.0.0.jar /app/wh-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/wh-app.jar"]