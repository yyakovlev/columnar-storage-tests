
#FROM maven:3.5-jdk-11 AS build 
FROM maven:3.6.3-openjdk-14-slim AS build
COPY carbondata-poc /usr/src/app/carbondata-poc  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:11.0.6-jre
WORKDIR /app
COPY --from=build /usr/src/app/carbondata-poc/target/carbondata-poc-0.0.1-SNAPSHOT.jar /app
CMD ["/usr/local/openjdk-11/bin/java",  "-jar", "carbondata-poc-0.0.1-SNAPSHOT.jar"]
#CMD ["/bin/bash"]