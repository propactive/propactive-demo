FROM bellsoft/liberica-openjdk-alpine:17

ARG git_repository="Unknown"
ARG git_commit="Unknown"
ARG git_branch="Unknown"
ARG version

LABEL git.repository=$git_repository
LABEL git.commit=$git_commit
LABEL git.branch=$git_branch

ADD propactive-demo/build/libs/nebula-app-$version-fat.jar propactive-demo.jar
CMD ["java", "-Xmx512m", "-XX:MaxMetaspaceSize=256m", "-DconfigDir=/app/config", "-jar", "propactive-demo.jar"]
