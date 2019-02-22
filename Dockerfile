# 在外面先用docker pull java:8-jre-alpine下载镜像，否则maven执行的过程会慢到你吐血
FROM java:8-jre-alpine
MAINTAINER jianghui

ENTRYPOINT ["java", "-DLOG_FILE=./allInOne.log", "-jar", "/usr/share/myservice/myservice.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/myservice/myservice.jar

# FROM openjdk:8-jre
# MAINTAINER jianghui

# ENTRYPOINT ["/usr/bin/java", "-DLOG_FILE=./allInOne.log", "-jar", "/usr/share/myservice/myservice.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
# ADD target/lib           /usr/share/myservice/lib
# Add the service itself
# ARG JAR_FILE
# ADD target/${JAR_FILE} /usr/share/myservice/myservice.jar