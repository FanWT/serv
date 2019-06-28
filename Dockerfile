FROM tomcat:latest
WORKDIR /usr/local
RUN rm -rf /usr/local/tomcat/webapps/*
ADD service2.war /usr/local/tomcat/webapps/ROOT.war
#RUN yes|unzip /usr/local/tomcat/webapps/ROOT.war -d /usr/local/tomcat/webapps/ROOT/
#RUN chmod 777 -Rf /usr/local/tomcat/webapps/*
EXPOSE 30683
ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh","run"]
