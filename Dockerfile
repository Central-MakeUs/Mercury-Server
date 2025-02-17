FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]