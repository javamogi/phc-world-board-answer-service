FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/board-answer-service-1.0.jar AnswerService.jar
ENTRYPOINT ["java","-jar","AnswerService.jar"]