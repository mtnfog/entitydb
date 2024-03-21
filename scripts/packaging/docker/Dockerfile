# Dockerfile for Philterd's EntityDB.
FROM ubuntu:16.04
MAINTAINER Philterd <support@philterd.ai>

RUN apt-get -qq update 
RUN apt-get -qq -y upgrade
RUN apt-get -qq -y install openssh-server openjdk-8-jre

# Enable SSH in the container. This step is not required.
RUN mkdir /var/run/sshd
RUN echo 'root:entitydb' | chpasswd
RUN sed -i 's/PermitRootLogin without-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd
ENV NOTVISIBLE "in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile
EXPOSE 22

# Copy and install EntityDB into the container.
RUN mkdir /opt/entitydb
ADD ../../entitydb-app/target/entitydb.jar /opt/entitydb/entitydb.jar
ADD ../../entitydb-app/target/entitydb.conf /opt/entitydb/entitydb.conf
ADD ../../entitydb-app/target/entitydb.properties /opt/entitydb/entitydb.properties
ADD ../../LICENSE /opt/entitydb/LICENSE
ADD ../../README.md /opt/entitydb/README.md
RUN ln -s /opt/entitydb/entitydb.jar /etc/init.d/entitydb
EXPOSE 8080

# Start EntityDB
CMD service entitydb start && /usr/sbin/sshd -D
