# TMMC Assignment 1

A vertical line counter for the TMMC Assignment 1

## CLI

Run the following commands to compile and run class files:
```
javac -d out $(find src/main/java/com/amrelshafei/tmmc/assignment1/cli -name "*.java")
java -cp out com.amrelshafei.tmmc.assignment1.cli.TmmcAssignment1ConsoleApp ./src/test/resources/sample-images/img_1.jpg
```

Run the following commands to compile a jar file instead:
```
javac -d out $(find src/main/java/com/amrelshafei/tmmc/assignment1/cli -name "*.java")
jar cfe target/tmmc-assignment1-cli.jar com.amrelshafei.tmmc.assignment1.cli.TmmcAssignment1ConsoleApp -C out .
java -jar target/tmmc-assignment1-cli.jar ./src/test/resources/sample-images/img_1.jpg
```

## Web App

> Requires Java 17

Run the following command to compile the Springboot app and serve it locally at port `8080`:
```
mvn clean spring-boot:run
```

## Problems that had to be tackled

JPEG compression introduces noise, i.e., pixels that are not true black. 
- This introduces inconsistent column heights in a vertical line or irregular colors within a pixel. Solution: 
  - Use kernel and stride to average kernel colors instead of picking a single pixel
  - Use threshold to detect near black colors
  - Average column heights within a sliding window to tolerate inconsistencies
- Also, these noise can introduce whole columns that are not black within a vertical line in the image.
