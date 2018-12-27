FROM java:8-jre

ADD ./target/pricequeryservice-0.0.1-SNAPSHOT.jar /app/

ENTRYPOINT ["java", "-Xmx200m", "-jar", "/app/pricequeryservice-0.0.1-SNAPSHOT.jar"]
EXPOSE 8090