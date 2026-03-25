# TMMC Assignment 1

A vertical line counter for the TMMC Assignment 1

## CLI on MacOS

Run the following commands to compile and run class files:
```
javac -d out $(find src/main/java/com/amrelshafei/tmmc/assignment1/cli -name "*.java")
java -cp out com.amrelshafei.tmmc.assignment1.cli.TmmcAssignment1ConsoleApp ./src/test/resources/sample-images/img_1.jpg
```

Run the following commands to compile a jar file instead:
```
javac -d out $(find src/main/java/com/amrelshafei/tmmc/assignment1/cli -name "*.java")
mkdir -p target && jar cfe target/tmmc-assignment1-cli.jar com.amrelshafei.tmmc.assignment1.cli.TmmcAssignment1ConsoleApp -C out .
java -jar target/tmmc-assignment1-cli.jar ./src/test/resources/sample-images/img_1.jpg
```

## CLI on Windows Powershell

> I don't have a Windows machine at the moment. Only running commands on MacOS is confirmed and tested.

Run the following commands to compile and run class files on Windows:
```powershell
javac -d out (Get-ChildItem -Recurse -Filter "*.java" src\main\java\com\amrelshafei\tmmc\assignment1\cli | Select-Object -ExpandProperty FullName)
java -cp out com.amrelshafei.tmmc.assignment1.cli.TmmcAssignment1ConsoleApp .\src\test\resources\sample-images\img_1.jpg
```

Run the following commands to compile a jar file instead on Windows:
```powershell
javac -d out (Get-ChildItem -Recurse -Filter "*.java" src\main\java\com\amrelshafei\tmmc\assignment1\cli | Select-Object -ExpandProperty FullName)
New-Item -ItemType Directory -Force -Path target | Out-Null; jar cfe target\tmmc-assignment1-cli.jar com.amrelshafei.tmmc.assignment1.cli.TmmcAssignment1ConsoleApp -C out .
java -jar target\tmmc-assignment1-cli.jar .\src\test\resources\sample-images\img_1.jpg
```

## Web App

> Requires Java 17 and Maven 3.9

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
