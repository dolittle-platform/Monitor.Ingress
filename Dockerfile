# Copyright (c) Dolittle. All rights reserved.
# Licensed under the MIT license. See LICENSE file in the project root for full license information.
FROM maven:3-jdk-11 as builder
# create app folder for sources
WORKDIR /build
COPY pom.xml /build
#Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins
#Copy source code
COPY src /build/src
# Build application
RUN mvn package


FROM openjdk:11-slim as runtime
RUN apt-get update && apt-get -y install cron
RUN service cron start
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

#Set app home folder
ENV APP_HOME /app
#Possibility to set JVM options (https://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html)
ENV JAVA_OPTS=""

WORKDIR $APP_HOME
#Copy executable jar file from the builder image
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]