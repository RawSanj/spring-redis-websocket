#!/usr/bin/env bash

# Runs Tests and Creates Docker Image based on Spring Graal Native Image
echo '**************************************************************'
echo 'Creating Docker Image based on Spring Graal Native Image'
mvn -Pnative clean spring-boot:build-image
echo 'Docker Image based on Spring Graal Native Image Creation Completed!'
echo '**************************************************************'

# Creates Docker Image
echo '**************************************************************'
echo 'Creating Docker Image based on JVM'
mvn clean spring-boot:build-image
echo 'Docker Image based on JVM Creation Completed!'
echo '**************************************************************'

# Runs Tests and Creates Graal Native Image
echo '**************************************************************'
echo 'Creating Graal Native Image'
mvn -Pnative clean package -DskipNativeImage=false
echo 'Graal Native Image Creation Completed!'
echo '**************************************************************'
