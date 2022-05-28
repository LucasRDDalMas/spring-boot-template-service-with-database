FROM public.ecr.aws/docker/library/gradle:jdk17 as builder
WORKDIR /usr/app
COPY --chown=gradle:gradle . .
RUN gradle classes --build-cache
RUN gradle assemble

FROM public.ecr.aws/amazoncorretto/amazoncorretto:17-al2-jdk as runner
EXPOSE 8001
RUN mkdir /app
COPY --from=builder /usr/app/build/libs/*.jar /app/application.jar
ENTRYPOINT ["java", "-jar", "/app/application.jar"]