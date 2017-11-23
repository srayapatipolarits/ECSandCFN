FROM 974293703167.dkr.ecr.us-east-1.amazonaws.com/nonprod:sp

# Expose 8080 to host
EXPOSE 8080

#Env Variables
ENV JAVA_HOME /opt/java
ENV PATH $PATH:/opt/java/bin

ADD calendar.war /opt/tomcat/webapps/
ADD sample.war /opt/tomcat/webapps/

# Start tomcat
CMD ["/opt/tomcat/scripts/tomcat.sh"]
