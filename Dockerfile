FROM openjdk:14.0.1-jdk AS build

COPY . /

RUN set -ex; \
    chmod +x ./gradlew ; \
    ./gradlew build --no-build-cache; \
    mv build/libs/OWBalancer.jar / ;

FROM openjdk:14-alpine

COPY --from=build OWBalancer.jar /opt/owbalancer/

VOLUME /opt/owbalancer

WORKDIR /opt/owbalancer

ENTRYPOINT ["java", "-jar", "/opt/owbalancer/OWBalancer.jar"]