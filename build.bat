@echo off
echo Building PlotSquared Expansion Plugin...
echo.

if exist "C:\Program Files\Apache\maven\bin\mvn.cmd" (
    "C:\Program Files\Apache\maven\bin\mvn.cmd" clean package
) else if exist "C:\Program Files\Maven\bin\mvn.cmd" (
    "C:\Program Files\Maven\bin\mvn.cmd" clean package
) else (
    echo Maven not found in common locations.
    echo Please install Maven or add it to your PATH.
    echo.
    echo You can download Maven from: https://maven.apache.org/download.cgi
    echo.
    echo After installation, run: mvn clean package
)

echo.
echo Build completed! Check the target/ folder for the compiled JAR file.
pause 