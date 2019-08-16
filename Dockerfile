FROM frolvlad/alpine-java:jdk8-slim
EXPOSE 8080 5080
ARG version
ARG environment
ENV ENVIRONMENT ${environment}
VOLUME /tmp
COPY build/libs/patogalla-api-${version}.jar app.jar
#COPY secrets-${environment}.yml secrets.yml
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Dnetworkaddress.cache.ttl=30 -Xms1m -Xmx1850m -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+AlwaysPreTouch -server -Xss1m -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -Xloggc:/var/log/gc.log"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5080 -Dspring.profiles.active=$ENVIRONMENT -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
#-Dspring.config.location=file:secrets.yml