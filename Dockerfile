# Use the OpenJDK 17 base image
FROM openjdk:17

# Set the working directory to /app
WORKDIR /app

# Set JVM options
ENV JVM_OPTS="--add-opens java.base/sun.nio.ch=ALL-UNNAMED \
--add-exports java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens java.base/jdk.internal.ref=ALL-UNNAMED \
--add-exports java.base/jdk.internal.ref=ALL-UNNAMED"

# Copy all files from the host to /app in the container
COPY . .

# Expose the port the app runs on
EXPOSE 8080

# Run the Java application with the specified JAR file and JVM options
ENTRYPOINT ["java", "$JVM_OPTS", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/target/cassandra-cdc-0.0.1-SNAPSHOT.jar"]
