# Use base image for Java 8
FROM egovio/alpine-jre:8u121

# Maintainer information
MAINTAINER Vaibhav Jain <vaibhav.jain@wtt.com>

# Copy the built jar into the container
COPY /target/legal-services-0.0.1-SNAPSHOT.jar /opt/egov/legal-services.jar

# Copy startup script
COPY start.sh /usr/bin/start.sh

# Give execution permissions to the script
RUN chmod +x /usr/bin/start.sh

# Start the service
CMD ["/usr/bin/start.sh"]
