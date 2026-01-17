@echo off
title CAT WARS - KLIENT
echo Uruchamianie trybu Goscia...
echo.

if not exist "client.jar" (
    echo [BLAD] Nie znaleziono client.jar!
    echo Najpierw uruchom skrypt build.bat
    pause
    exit /b
)

java -jar client.jar
pause