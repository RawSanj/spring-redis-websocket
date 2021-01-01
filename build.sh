#!/usr/bin/env bash

# Runs Tests and Creates Graal Native Image - Uses TestContainers to start the Redis Container required for Test (Docker is required)
mvn -Pnative clean package -DskipNativeImage=false

# Runs Tests and Creates Graal Native Docker Image - Uses TestContainers to start the Redis Container required for Test (Docker is required)
mvn -Pnative clean spring-boot:build-image

# Creates Docker Image - Skips Integration Tests and hence Docker is not required
mvn clean spring-boot:build-image
