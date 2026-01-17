@echo off
title CAT WARS - SERWER
echo Uruchamianie trybu Hosta...
echo.

if not exist "server.jar" (
    echo [BLAD] Nie znaleziono server.jar!
    echo Najpierw uruchom skrypt build.bat
    pause
    exit /b
)

java -jar server.jar
pause