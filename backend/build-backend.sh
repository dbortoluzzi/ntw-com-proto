#!/bin/bash

export JAVA_HOME="/usr/lib/jvm/jdk-11.0.12/"
export PATH=$JAVA_HOME/bin:$PATH

echo $(java -version)
echo $(mvn --version | grep -i java )

cd commons/
mvn clean install package
cd ../
cd consumer-service/
mvn clean install package
cd ../
cd producer-service/
mvn clean install package
cd ../
