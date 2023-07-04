FROM maven AS build
MAINTAINER ajay.kannu@zafin.com

WORKDIR /app-build
COPY . .

#RUN mvn -e -B dependency:resolve

RUN apt update -y && apt upgrade openssl -y
RUN mvn clean package


FROM registry.access.redhat.com/ubi9/ubi-minimal
LABEL BASE_IMAGE="registry.access.redhat.com/ubi9/ubi-minimal"
LABEL JAVA_VERSION="11"

ENV APP_ROOT=/app
WORKDIR ${APP_ROOT}


RUN microdnf install --nodocs java-11-openjdk-headless -y && microdnf clean all -y
RUN microdnf update -y && microdnf upgrade -y

RUN chown -R 1001:0 ${APP_ROOT} && chmod -R u+rwx ${APP_ROOT} && \
    chmod -R g=u ${APP_ROOT}


COPY --from=build /app-build/target/*.jar application.jar

RUN mkdir -p /brokered-product/configmap

USER 1001
EXPOSE 8080

CMD ["java", "-jar", "application.jar", "--spring.config.location=file:///brokered-product/configmap/application.properties"]