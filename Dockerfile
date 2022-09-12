FROM adoptopenjdk/openjdk11:ubi
WORKDIR /app
EXPOSE 5000
ADD ./ .
CMD ["java", "-jar", "sh-be-code-challenge-all.jar"]