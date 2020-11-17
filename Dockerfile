FROM alpine:3.7

RUN apk add --update \
#    python2 \
#    python2-dev \
#    py-pip \
#    build-base \
    openjdk8 \
    curl \
#  && pip install virtualenv \
   && rm -rf /var/cache/apk/*

#############################################
# MAVEN
#############################################
ARG MAVEN_VERSION=3.5.4
ARG USER_HOME_DIR="/root"
ARG SHA=ce50b1c91364cb77efe3776f756a6d92b76d9038b0a0782f7d53acf1e997a14d
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha256sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

#COPY mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
#COPY settings-docker.xml /usr/share/maven/ref/

ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
CMD ["mvn"]

#############################################
# MAVEN
#############################################

#############################################
# SCTK
#############################################

#ENV SCTK_FILENAME sctk-2.4.10-20151007-1312Z.tar.bz2
#ENV SCTK_PATH /opt/sctk-2.4.10
#
#RUN apk add --update \
#    alpine-sdk \
#    perl \
#    freetype-dev \
#  && rm -rf /var/cache/apk/*
#
#WORKDIR /opt
#
## Build and install all SCTK tools
#RUN curl -O ftp://jaguar.ncsl.nist.gov/pub/$SCTK_FILENAME \
#  && tar -xvf $SCTK_FILENAME \
#  && rm $SCTK_FILENAME \
#  && cd $SCTK_PATH \
#  && make config \
#  && make all \
#  && make install \
#  && make clean \
#  && cp $SCTK_PATH/bin/* /usr/local/bin/

#############################################
# SCTK
#############################################


#RUN ls -l
#RUN pwd
#RUN mvn clean install


COPY target/sttcustomization-1.0-SNAPSHOT.jar /app.jar

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
