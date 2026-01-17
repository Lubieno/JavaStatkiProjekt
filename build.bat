@echo off
echo ==========================================
echo BUDOWANIE PROJEKTU CAT WARS
echo ==========================================

REM Definiujemy sciezke, gdzie IntelliJ zapisal plik (wg Twojej informacji)
set "SOURCE_JAR=out\artifacts\battleship_jar\battleship.jar"

REM Sprawdzamy, czy plik tam faktycznie jest
if not exist "%SOURCE_JAR%" (
    echo [BLAD] Nie znaleziono pliku w lokalizacji:
    echo %SOURCE_JAR%
    echo.
    echo Upewnij sie, ze w IntelliJ wykonales: Build -> Build Artifacts... -> battleship:jar -> Build
    pause
    exit /b 1
)

echo Znaleziono battleship.jar. Kopiowanie do wymaganych nazw...

REM Kopiowanie pliku do server.jar i client.jar w biezacym folderze
copy /Y "%SOURCE_JAR%" "server.jar" >nul
copy /Y "%SOURCE_JAR%" "client.jar" >nul

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUKCES] Utworzono pliki:
    echo  - server.jar
    echo  - client.jar
    echo.
    echo Mozesz teraz uruchamiac run_server.bat i run_client.bat
) else (
    echo [BLAD] Wystapil problem przy kopiowaniu plikow.
)

pause