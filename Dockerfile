FROM gradle:6.8-jdk15-hotspot as builder
WORKDIR /build

# Simple files
COPY ["build.gradle", "gradlew", "VERSION", "./"]
# Directories
COPY .git/ ./.git
COPY src/ ./src
COPY gradle/ ./gradle

# Build JAR
RUN chmod +x gradlew && ./gradlew --no-daemon build


FROM adoptopenjdk:15-jre-hotspot-focal

EXPOSE 8080

WORKDIR /opt/pegnu-files
VOLUME /tmp

COPY --from=builder /build/build/libs/pegnu-files-*.jar springApp.jar
RUN sh -c 'touch springApp.jar'

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "springApp.jar"]
