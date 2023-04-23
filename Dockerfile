FROM clojure:tools-deps as builder

WORKDIR /example

# Add and Build Application Code
COPY . /example
RUN clj -T:build uber

#FROM openjdk:20-oraclelinux8
FROM openjdk:17-jdk-slim-bullseye
ARG APP=/usr/src/app

ENV TZ=Etc/UTC \
    APP_USER=appuser

EXPOSE 80

RUN groupadd $APP_USER \
    && useradd -g $APP_USER $APP_USER \
    && mkdir -p ${APP}

COPY --from=builder /example/target/server-standalone.jar ${APP}
COPY --from=builder /example/env/dev/resources/config.edn ${APP}
COPY --from=builder /example/env/dev/resources/logback.xml ${APP}

RUN chown -R $APP_USER:$APP_USER ${APP}

USER $APP_USER
WORKDIR ${APP}

CMD ["java", "-Dconf=config.edn", "-Dlogback.configurationFile=logback.xml", "-jar", "server-standalone.jar"]