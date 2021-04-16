Welcome to your new Kafka Connect connector!

# Running in development

```
 
```


# Kafka Streaming vs Spark Streaming, NiFi, Flink
- Micro batch vs per data streaming
- cluster required vs no cluster required
- scale easily by just adding java processes ( no reconfiguration required)
- Exactly Once semantics (vs at once for spark)


[What are the differences and similarities between Kafka and Spark Streaming?](https://www.quora.com/What-are-the-differences-and-similarities-between-Kafka-and-Spark-Streaming)



# Dependencies

```xml

<properties>
    <!-- Kafka build plugin -->
  <main.class.path>nz.co.dhaka.kafka.stream.MuleStream</main.class.path>
  
  </properties>

 <dependencies>
<!-- Kafka Streaming -->
<!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
    <version>2.7.0</version>
</dependency>

<!-- Logging -->
  <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-api -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.30</version>
</dependency>
  <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-log4j12 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.30</version>
    <scope>test</scope>
</dependency>
 </dependencies>

  <build>
        <plugins>
            <!--force java 8-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.6.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>

            <!--package as one fat jar-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>main.class.path</mainClass>
                        </manifest>
                    </archive>
                </configuration>
                <executions>
                    <execution>
                        <id>assemble-all</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```



# Stream App Properties
- Consumer and Producer inbuilt API being utilized behind the scene.
- bootstrap.servers: need to connect to kafka (usually port 9092)
- auto.offset.reset.config : set to 'earliest' to consume the topic from start
- application.id : specific to stream application, will be used for 
    - Consumer group.id=application.id (most important one)
    - default client.id prefix
    - prefix to internal changelog topics
- default.[key|value].serde (for serialization and deserilization)


# Java 8 Lambda Functions
```java
stream.filter(
    // filter all positive value;
(key,value) -> value > 0;
);
```