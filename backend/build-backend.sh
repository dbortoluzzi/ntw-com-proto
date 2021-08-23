#!/bin/bash

export JAVA_HOME="/usr/lib/jvm/jdk-11.0.12/"
export PATH=$JAVA_HOME/bin:$PATH

echo $(java -version)
echo $(mvn --version | grep -i java )

cd consumer-service/
mvn clean package
cd ../
cd auth-service/
mvn clean package
cd ../
