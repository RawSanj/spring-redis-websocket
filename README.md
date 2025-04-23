# spring-redis-websocket

[![Docker JVM Build](https://img.shields.io/github/actions/workflow/status/RawSanj/spring-redis-websocket/docker-jvm-image.yml?style=flat-square&logo=docker&label=jvm%20build)](https://github.com/RawSanj/spring-redis-websocket/actions/workflows/docker-jvm-image.yml)
[![Docker GraalVM Native Build](https://img.shields.io/github/actions/workflow/status/RawSanj/spring-redis-websocket/docker-graalvm-native-image.yml?style=flat-square&logo=docker&label=graalvm%20build)](https://github.com/RawSanj/spring-redis-websocket/actions/workflows/docker-graalvm-native-image.yml)
[![Docker Pulls](https://img.shields.io/docker/pulls/rawsanj/spring-redis-websocket?style=flat-square&logo=docker&color=orange)](https://hub.docker.com/repository/docker/rawsanj/spring-redis-websocket/general)
[![Discord Server](https://img.shields.io/discord/465093591002513418?style=flat-square&logo=discord&label=discord)](https://discord.gg/4ebNhud)
[![GitHub License](https://img.shields.io/github/license/rawsanj/spring-redis-websocket?style=flat-square&logo=apache&logoColor=red)](https://github.com/RawSanj/spring-redis-websocket?tab=Apache-2.0-1-ov-file)
[![Spring Chat Website](https://img.shields.io/website?url=https%3A%2F%2Fchat.apps.dedyn.io%2Factuator%2Fhealth&up_message=online&up_color=green&down_message=offline&down_color=red&style=flat-square&logo=signal&logoColor=white&label=DEMO%20Chat&labelColor=purple)](https://chat.apps.dedyn.io)

## Multi-instance Reactive Chat App using Spring Boot WebFlux and Redis Pub/Sub

Scalable Java 21 Spring Boot 3.x WebFlux Chat Application to demonstrate use of Reactive Redis [Pub/Sub] using
Reactive [WebSocket Handler], without using any external Message Broker like RabbitMQ to sync messages between different
instances.

Both JVM based application and [GraalVM Native Image] is supported. 
Additionally, the Docker Image for JVM base is available for AMD64 and ARM64 architecture. 

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy/?template=https://github.com/RawSanj/spring-redis-websocket/tree/spring-boot-web-2.3)

> The older non-reactive servlet based spring-redis-websocket application can be found in below:
>1. [Spring-Boot 2.3: Java-11 version](https://github.com/RawSanj/spring-redis-websocket/tree/spring-boot-web-2.3)
>2. [Spring-Boot 1.5: Java-8 version](https://github.com/RawSanj/spring-redis-websocket/tree/spring-boot-1.5.x)

> The older reactive spring-boot 2.x (java 11) based spring-redis-websocket application can be found in below:
>1. [Spring-Boot 2.4.6: Java-11 Reactive JVM & GraalVM Native version](https://github.com/RawSanj/spring-redis-websocket/tree/spring-boot-webflux-graal-native-2.4.6)
>2. [Spring-Boot 2.5.2: Java-11 Reactive JVM & GraalVM Native version](https://github.com/RawSanj/spring-redis-websocket/tree/spring-boot-webflux-graal-native-2.5.2)

> The older reactive spring-boot 3.2.x (java 17) based spring-redis-websocket application can be found in below:
>1. [Spring-Boot 3.2.0: Java-11 Reactive JVM & GraalVM Native version](https://github.com/RawSanj/spring-redis-websocket/tree/spring-boot-webflux-graal-native-3.2.0)

### Deploy to Play-with-Docker

Ctrl + Click this button to deploy multiple instances of the spring-redis-websocket load balanced by [Traefik]:

[![Deploy to PWD](deploy-to-pwd.png)](https://labs.play-with-docker.com/?stack=https://raw.githubusercontent.com/RawSanj/spring-redis-websocket/master/src/main/docker/docker-compose.yml)

### Installation and Configuration

##### Pre-requisite for Java Image:
Install and run [Redis] locally or on Docker.

To run Redis in Docker:
```sh
$ docker run -d -p 6379:6379 -e REDIS_PASSWORD=SuperSecretRedisPassword bitnami/redis:7.2.3
```

##### Pre-requisite for GraalVM Native Image:
This application uses Spring Data Redis APIs which doesn't have default GraalVM hints/config and graalvm-native image fails to run with errors.

Hence, this application is configured to use GraalVMVM native image tracing agent allows intercepting reflection, resources or proxy usage on the JVM by running simple Integration Tests which requires Redis.

1. To run integration test which uses [Redis TestContainers](https://www.testcontainers.org/supported_docker_environment) so [Docker] should be configured properly to run [Testcontainers]
2. You also need to install [GraalVMVM JDK](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-22.3.0) and [native-image](https://www.graalvm.org/reference-manual/native-image) component:
   ```sh
   $ sdk install java 21.0.2-graalce         # Using [SDKMAN](https://sdkman.io/jdks) install GraalVMVM distribution of JDK
   $ sdk use java 21.0.2-graalce
   ```

##### Clone repo:
```sh
$ git clone https://github.com/RawSanj/spring-redis-websocket.git
```

#### Build and Run the application:

Build and run the **spring-redis-websocket** application:
```sh
$ cd spring-redis-websocket

$ mvn clean package

$ mvn spring-boot:run
```

#### Build GraalVM Native Image of the application:

Build and run the **spring-redis-websocket** native image:
```sh
$ cd spring-redis-websocket

$ mvn -Pnative clean package

$ target/spring-redis-websocket # run the executable binary
```

> **Note:** Above steps are applicable for Linux only and creates linux executable binary. To create Windows executable there are few additional set-ups required, follow this [Steps](https://www.graalvm.org/docs/getting-started/windows).

### Run in Docker

#### Build and run the *spring-redis-websocket* locally in Docker:

Build the JAR file:

```sh
$ mvn clean package
```

Build Docker image:

```sh
$ mvn clean spring-boot:build-image
```

Build GraalVM Native Docker image:

```sh
$ mvn -Pnative clean spring-boot:build-image
```

Run docker image:

```sh
$ docker run -d -p 8080:8080 rawsanj/spring-redis-websocket:3.4.4-jvm # JVM based Docker Image

$ docker run -d -p 8080:8080 rawsanj/spring-redis-websocket:3.4.4-native  # GraalVM Native Image based Docker Image
```

#### Run multiple instances using docker-compose locally

Run multiple instances of *spring-redis-websocket* locally load balanced via [Traefik] connected to redis container in Docker:

```sh
$ cd src/main/docker
$ docker-compose up
```

#### Or try [Play with Docker] to quickly setup Docker and run in browser:
1. Click Create Instance to quickly setup Docker host.
2. Install git by running: 
	```sh
	$ apk add git --no-cache
	```
3. Clone the repository:
	```sh
	$ git clone https://github.com/RawSanj/spring-redis-websocket.git
	```
4. Run multiple instances of *spring-redis-websocket*:
	```sh
	$ cd spring-redis-websocket/src/main/docker
	$ docker-compose up
	```

### Run in Kubernetes

#### Assuming you have a Kubernetes Cluster up and running locally:

```sh
$ kubectl apply -f src/main/k8s
```

#### Or try [Play with Kubernetes] to quickly setup a K8S cluster:
1. Follow the instructions to create Kuberenetes cluster.
2. Install git by running: 
	```sh
	$ yum install git -y
	```
3. Clone the repository:
	```sh
	$ git clone https://github.com/RawSanj/spring-redis-websocket.git
	```
4. Run multiple instances of *spring-redis-websocket* load balanced by native Kubernetes Service. All instances
   connected to a single [Redis] pod.
	```sh
	$ cd spring-redis-websocket
	$ kubectl apply -f src/main/k8s
	```
### Tech

**spring-redis-websocket** uses a number of open source projects:

* [Spring Boot] - An opinionated framework for building production-ready Spring applications. It favors convention over
  configuration and is designed to get you up and running as quickly as possible.
* [Spring Data Redis] - Spring Data Redis provides easy configuration and access to Redis from Spring applications.
* [GraalVM Native Image] - Native Image is a technology to ahead-of-time compile Java code to a standalone executable,
  called a native image.
* [Redis] - Redis is an open source (BSD licensed), in-memory data structure store, used as a database, cache and
  message broker.
* [Testcontainers] - Testcontainers is a Java library that supports JUnit tests, providing lightweight, throwaway
  instances of common databases or anything else that can run in a Docker container.
* [Bootstrap] - Bootstrap is an open source toolkit for developing with HTML, CSS, and JS. Custom Bootstrap theme
	- [Bootswatch Sketch].
* [Docker] - Docker is an open platform for developers and sysadmins to build, ship, and run distributed applications.
* [Traefik] - Traefik is a Cloud Native Application Proxy - Simplifies networking complexity while designing, deploying, and operating applications.
* [Kubernetes] - Kubernetes is an open-source system for automating deployment, scaling, and management of containerized
  applications.

License
----

Apache License 2.0

Copyright (c) 2025 Sanjay Rawat

[//]: #

[Spring Boot]:<https://projects.spring.io/spring-boot>

[Redis]: <https://redis.io>

[Runtime]: <https://github.com/kubeless/kubeless/blob/master/docs/runtimes.md#custom-runtime-alpha>

[Spring Data Redis]: <https://projects.spring.io/spring-data-redis>

[GraalVM Native Image]: <https://www.graalvm.org/reference-manual/native-image>

[Testcontainers]: <https://www.testcontainers.org>

[Bootstrap]: <https://getbootstrap.com>

[Bootswatch Sketch]: <https://bootswatch.com/sketchy>

[Docker]: <https://www.docker.com>

[Traefik]: <https://traefik.io/traefik>

[Kubernetes]: <https://kubernetes.io>

[Pub/Sub]: <https://redis.io/topics/pubsub>

[WebSocket Handler]: <https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-websockethandler>

[Play with Kubernetes]: <https://labs.play-with-k8s.com>

[Play with Docker]: <https://labs.play-with-docker.com>
