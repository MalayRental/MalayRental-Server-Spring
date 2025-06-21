# 使用官方OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-alpine

# 创建目录并设置工作目录
WORKDIR /app

# 复制jar包到容器中
COPY target/malayrental-server.jar app.jar

# 暴露端口
EXPOSE 4433

# 启动Spring Boot应用
ENTRYPOINT ["java", "-jar", "app.jar"] 