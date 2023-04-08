FROM openjdk:17-jdk-slim-bullseye as builder

WORKDIR /example

# Add just the .project.clj file to capture dependencies
COPY ./project.clj /example/project.clj

RUN apt-get update && apt-get -y install leiningen
# Docker will cache this command as a layer, freeing us up to
# modify source code without re-installing dependencies
RUN lein deps

# Add and Build Application Code
COPY . /example
RUN lein uberjar

#FROM openjdk:20-oraclelinux8
FROM openjdk:17-jdk-slim-bullseye
ARG APP=/usr/src/app

ENV TZ=Etc/UTC \
    APP_USER=appuser

EXPOSE 3000

RUN groupadd $APP_USER \
    && useradd -g $APP_USER $APP_USER \
    && mkdir -p ${APP}

COPY --from=builder /example/target/ring-example-0.1.0-SNAPSHOT-standalone.jar ${APP}
COPY --from=builder /example/env/dev/resources/config.edn ${APP}
COPY --from=builder /example/env/dev/resources/logback.xml ${APP}

RUN chown -R $APP_USER:$APP_USER ${APP}

USER $APP_USER
WORKDIR ${APP}

CMD ["java", "-Dconf=config.edn", "-Dlogback.configurationFile=logback.xml", "-jar", "ring-example-0.1.0-SNAPSHOT-standalone.jar"]