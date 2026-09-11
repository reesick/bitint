@echo off
REM ---------------------------------------------------------------------
REM  Setu - push this folder to your own GitHub repo so the Actions
REM  runner builds the APK for you.
REM
REM  Needs: git. Nothing else.
REM
REM  Before running: create an EMPTY repo on GitHub (any name, public or
REM  private) and copy its URL.
REM ---------------------------------------------------------------------

setlocal
cd /d "%~dp0"

where git >nul 2>&1
if errorlevel 1 (
    echo.
    echo  git is not installed or not on PATH.
    echo  Get it from https://git-scm.com/download/win then run this again.
    echo.
    pause
    exit /b 1
)

echo.
set /p REPOURL=Paste your empty GitHub repo URL (https://github.com/you/setu.git): 
if "%REPOURL%"=="" (
    echo No URL given, stopping.
    pause
    exit /b 1
)

if not exist ".git" (
    echo Initialising a fresh repo here...
    git init -q -b main || goto :fail
)

echo Staging...
git add -A || goto :fail

git -c user.email=setu@local -c user.name=Setu commit -q -m "Setu: messenger reskin of bitchat" 2>nul
if errorlevel 1 echo Nothing new to commit, continuing.

git remote remove origin >nul 2>&1
git remote add origin "%REPOURL%" || goto :fail

echo Pushing...
git push -u origin main || goto :fail

echo.
echo  ============================================================
echo   Pushed. Now open your repo on GitHub:
echo.
echo     Actions tab  ^>  "Build Setu APK"  ^>  Run workflow
echo.
echo   Six to ten minutes, then scroll to Artifacts on the run page
echo   and download setu-debug-apk.zip. The APK is inside.
echo  ============================================================
echo.
pause
exit /b 0

:fail
echo.
echo  Something failed above. Read the last error line.
pause
exit /b 1
