PROFILE_WEBFLUX = -Dspring.profiles.active=local
PROFILE_WEBMVC = -Dspring.profiles.active=local-mvc
APP = spring-webclient
VERSION = 0.0.1-SNAPSHOT
JAR = target/${APP}-${VERSION}.jar
DELAY_DURATION = 500

DOCKER_FOLDER = src/main/docker
DOCKER_CONF = ${DOCKER_FOLDER}/docker-compose.yml
DOCKER_IMAGE = ${APP}:latest

AB_FOLDER = ab
AB_TIME = 10
AB_CONCURRENCY = 500
WEBFLUX_SERVER_URL = localhost:8080/invoices/1
WEBFLUX_CLIENT_URL = localhost:8081/invoices/1
WEBMVC_SERVER_URL = localhost:8090/invoices/1
WEBMVC_CLIENT_URL = localhost:8082/invoices/1

# Common

clean:
	mvn clean

all: clean
	mvn compile

install: clean
	mvn install

check: clean
	mvn verify

dist: clean
	mvn package -DskipTests

dist-run: dist-run

dist-run-webflux: dist run-webflux

dist-run-webmvc: dist run-webmvc

run: run-webflux

run-webflux:
	java ${PROFILE_WEBFLUX} -jar ${JAR}

run-webmvc:
	java ${PROFILE_WEBMVC} -jar ${JAR}

# Docker

start-docker: dist copy-jar-docker
	docker-compose -f ${DOCKER_CONF} up

copy-jar-docker:
	cp ${JAR} ${DOCKER_FOLDER}

stop-docker: docker-down rm-docker-image

docker-down:
	docker-compose -f ${DOCKER_CONF} down

rm-docker-image:
	docker rmi ${DOCKER_IMAGE}

# Benchmark

ab-all: ab-webflux-server ab-webflux-client ab-webmvc-server ab-webmvc-client

ab-all-server: ab-webflux-server ab-webmvc-server

ab-all-client: ab-webflux-client ab-webmvc-client

ab-webflux-server:
	ab -t ${AB_TIME} -c ${AB_CONCURRENCY} ${WEBFLUX_SERVER_URL} > ${AB_FOLDER}/webflux-server-s${DELAY_DURATION}-t${AB_TIME}-c${AB_CONCURRENCY}.txt

ab-webflux-client:
	ab -t ${AB_TIME} -c ${AB_CONCURRENCY} ${WEBFLUX_CLIENT_URL} > ${AB_FOLDER}/webflux-client-s${DELAY_DURATION}-t${AB_TIME}-c${AB_CONCURRENCY}.txt

ab-webmvc-server:
	ab -t ${AB_TIME} -c ${AB_CONCURRENCY} ${WEBMVC_SERVER_URL} > ${AB_FOLDER}/webmvc-server-s${DELAY_DURATION}-t${AB_TIME}-c${AB_CONCURRENCY}.txt

ab-webmvc-client:
	ab -t ${AB_TIME} -c ${AB_CONCURRENCY} ${WEBMVC_CLIENT_URL} > ${AB_FOLDER}/webmvc-client-s${DELAY_DURATION}-t${AB_TIME}-c${AB_CONCURRENCY}.txt
