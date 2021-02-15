FROM openjdk:11.0.10-jre
COPY . /var/www/java
WORKDIR /var/www/java
ENV ip=${ip}
CMD java -jar ThirdPod.jar $ip


