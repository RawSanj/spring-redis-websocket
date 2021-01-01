#!/usr/bin/env bash

# Runs Tests and Creates Graal Native Image - Uses TestContainers to start the Redis Container required for Test
mvn -Pnative clean install

# Runs Tests and Creates Graal Native Docker Image - Uses TestContainers to start the Redis Container required for Test
mvn -Pnative clean spring-boot:build-image
