@echo off
REM Script to run ArduinoML Scala scenarios

if "%1"=="" (
    echo Usage: run.bat [Switch^|StateBasedAlarm^|MultiStateAlarm]
    echo.
    echo Examples:
    echo   run.bat Switch
    echo   run.bat StateBasedAlarm
    echo   run.bat MultiStateAlarm
    exit /b 1
)

set CLASSPATH=target\classes;C:\Users\sacha\.m2\repository\io\github\mosser\arduinoml\kernel-jvm\1.0\kernel-jvm-1.0.jar;C:\Users\sacha\.m2\repository\org\scala-lang\scala-library\2.13.12\scala-library-2.13.12.jar

java -cp "%CLASSPATH%" io.github.mosser.arduinoml.samples.%1
