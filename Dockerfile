FROM adoptopenjdk/openjdk11:ubi
WORKDIR /app
EXPOSE 5000
ADD ./build/libs/sh-be-code-challenge-all.jar .
CMD ["java", "-jar", "sh-be-code-challenge-all.jar"]