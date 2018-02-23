FROM openjdk:8-jre-alpine

VOLUME /tmp
ADD build/libs/pegnu-files-*.jar springApp.jar
RUN sh -c 'touch /springApp.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/springApp.jar"]