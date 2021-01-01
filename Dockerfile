# Use the official gradle image to create a build artifact.
# https://hub.docker.com/_/gradle
FROM gradle as builder

# Copy local code to the container image.
COPY . ./
COPY gitCopy/ ./.git

# Build a release artifact.
RUN gradle :webscrambles:shadowJar --no-daemon --warning-mode all --stacktrace

# Use the Official OpenJDK image for a lean production stage of our multi-stage build.
# https://hub.docker.com/_/openjdk
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM openjdk:8-jre

# Copy the jar to the production image from the builder stage.
COPY --from=builder /home/gradle/webscrambles/build/libs/webscrambles-1.0.3-all.jar /scrambler.jar
#COPY --from=builder /home/gradle/build/libs/gradle.jar /scrambler.jar

# Run the web service on container startup.
CMD [ "java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "/scrambler.jar" ]
