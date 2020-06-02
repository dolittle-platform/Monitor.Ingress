# Monitor Ingress



### Usage:


### Build:
```
mvn clean package
```

### Run:
```
java -jar target/<artifact>.jar
```

### Running as a docker container:
```
docker build -t <image_tag> . && docker run -p 8080:8080 <image_tag>
```