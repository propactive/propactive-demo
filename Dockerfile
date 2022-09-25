FROM bellsoft/liberica-openjdk-alpine:17

ARG version="DEV-SNAPSHOT"

ARG git_repository="Unknown"
ARG git_commit="Unknown"
ARG git_branch="Unknown"

ARG properties="./build/properties"

LABEL git.repository=$git_repository
LABEL git.commit=$git_commit
LABEL git.branch=$git_branch

ADD $properties /app/config
ADD ./build/libs/propactive-demo-$version.jar /app/propactive-demo.jar
CMD ["java", "-Xmx512m", "-XX:MaxMetaspaceSize=256m", "-Dproperties.config.path=/app/config", "-jar", "/app/propactive-demo.jar"]
