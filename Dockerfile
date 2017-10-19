FROM java:openjdk-8-jdk

ENV spark_ver 2.1.0

MAINTAINER Muhammad Ali Gulzar "maligulzar@ucla.edu"

# Update aptitude with new repo
RUN apt-get update

# Install software 
RUN apt-get install -y git && \
	apt-get install -y vim 
# Make ssh dir
RUN mkdir /root/.ssh/

# Copy over private key, and set permissions
ADD id_rsa /root/.ssh/id_rsa

ADD id_rsa.pub /root/.ssh/id_rsa.pub

# Create known_hosts
RUN touch /root/.ssh/known_hosts
# Add bitbuckets key
RUN ssh-keyscan github.com >> /root/.ssh/known_hosts

# Clone the conf files into the docker container
RUN git clone git@github.com:maligulzar/spark-lineage.git -b DistributedLineage

ENV PATH $PATH:/spark-lineage/bin

RUN cd /spark-lineage && \ 
	echo Installing Spark Now && \
	sbt/sbt assembly && \
	echo Spark Installed
