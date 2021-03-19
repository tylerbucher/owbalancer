FROM openjdk:8u171-jdk-alpine AS build

COPY . /

RUN set -ex; \
    chmod +x ./gradlew ; \
    ./gradlew build --no-build-cache; \
    mv build/libs/OWBalancer.jar / ;

FROM openjdk:8u171-jre-alpine

COPY --from=build OWBalancer.jar /opt/owbalancer/

VOLUME /opt/owbalancer

WORKDIR /opt/owbalancer

ENTRYPOINT ["java", "-jar", "/opt/owbalancer/OWBalancer.jar"]