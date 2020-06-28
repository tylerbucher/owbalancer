FROM openjdk:8u171-jdk-alpine AS build

COPY . /

RUN set -ex; \
    chmod +x ./gradlew ; \
    ./gradlew -q build ; \
    mv build/libs/OWBalancer.jar / ;

FROM node:13-alpine AS client

COPY src/javascript/resources/ui .

RUN set -ex; \
    npm install ; \
    npm run build ; \
    mv build/ /client

FROM openjdk:8u171-jre-alpine

COPY --from=build OWBalancer.jar /opt/owbalancer/
COPY --from=client /client /opt/owbalancer/public

WORKDIR /opt/owbalancer

ENTRYPOINT ["java", "-jar", "/opt/owbalancer/OWBalancer.jar"]