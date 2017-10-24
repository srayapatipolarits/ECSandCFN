FROM 974293703167.dkr.ecr.us-east-1.amazonaws.com/nonprod:apache

# Expose 80 to host
EXPOSE 80

#Env Variables
#ENV JAVA_HOME /opt/java
#ENV PATH $PATH:/opt/java/bin

#ADD calendar.war /opt/tomcat/webapps/


# Start tomcat
CMD ["/usr/sbin/httpd -D FOREGROUND"]
