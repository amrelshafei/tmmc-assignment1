# TMMC Assignment 1

A vertical line counter for the TMMC Assignment 1

## CLI

Run the following commands to compile and run class files:
```
javac -d out $(find src/main/java/com/amrelshafei/tmmc/assignment1/cli -name "*.java")
java -cp out com.amrelshafei.tmmc.assignment1.cli.TmmcAssignment1App ./src/test/resources/sample-images/img_1.jpg
```

Run the following commands to compile a jar file instead:
```
jar cfe target/tmmc-assignment1-cli.jar com.amrelshafei.tmmc.assignment1.cli.TmmcAssignment1App -C out .
java -jar target/tmmc-assignment1-cli.jar ./src/test/resources/sample-images/img_1.jpg
```

## Web App (served for limited time at assignment1.tmmc.amrelshafei.com through AWS Lambda)

> Requires Java 17

Run the following command to compile the Springboot app and serve it locally at port `8080`:
```
mvn clean spring-boot:run
```
