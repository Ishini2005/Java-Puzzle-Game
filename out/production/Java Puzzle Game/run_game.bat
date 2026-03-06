@echo off
echo ========================================
echo     JAVA PUZZLE GAME LAUNCHER
echo ========================================
echo.

REM Set the MySQL connector JAR path
set MYSQL_JAR=lib\mysql-connector-j-9.2.0.jar

REM Check if MySQL connector exists
if not exist %MYSQL_JAR% (
    echo ERROR: MySQL connector not found!
    echo Please download from:
    echo https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-9.2.0.zip
    echo Extract and copy mysql-connector-j-9.2.0.jar to the lib folder
    pause
    exit /b
)

echo 1. Testing MySQL connection...
java -cp ".;%MYSQL_JAR%" Code.TestMySQLConnection
if %errorlevel% neq 0 (
    echo.
    echo MySQL connection failed. Please check:
    echo - Is XAMPP MySQL running?
    echo - Is database 'game_db' created?
    echo - Are credentials correct?
    pause
    exit /b
)

echo.
echo 2. Compiling game files...
javac -cp ".;%MYSQL_JAR%" Code/*.java Code/banana/engine/*.java Code/database/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b
)

echo.
echo 3. Starting game...
java -cp ".;%MYSQL_JAR%" Code.Main

pause