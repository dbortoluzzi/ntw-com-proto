#!/bin/bash

echo $(java -version)
echo $(mvn --version | grep -i java )

export JAVA_HOME="/usr/lib/jvm/jdk-11.0.12/"
export PATH=$JAVA_HOME/bin:$PATH

echo $(java -version)
echo $(mvn --version | grep -i java )

# REVERT TO JAVA8
#export JAVA_HOME="/usr/lib/jvm/java-8-oracle/"