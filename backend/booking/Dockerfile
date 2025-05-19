FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY ./build/libs/booking-0.0.1-SNAPSHOT.jar ./booking.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "booking.jar"]
