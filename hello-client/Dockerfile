FROM java:8-jre

ADD ./target/hello-client-0.0.1.jar /app/

ENTRYPOINT ["java", "-Xmx200m", "-jar", "/app/hello-client-0.0.1.jar"]
EXPOSE 8899