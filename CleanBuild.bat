@echo off
cd /d C:\Users\shivr\OneDrive\Desktop\EIKOS_proj\android
echo ==============================================
echo NUCLEAR CLEAN: REMOVING ALL ANDROID LOCKS
echo ==============================================

echo 1. Killing all possible locking processes...
taskkill /F /IM java.exe /T >nul 2>&1
taskkill /F /IM studio64.exe /T >nul 2>&1
taskkill /F /IM adb.exe /T >nul 2>&1
taskkill /F /IM conhost.exe /T >nul 2>&1

echo 2. Forcing deletion of the build folder...
if exist app\build (
    echo Deleting app/build...
    rd /s /q app\build
)
if exist .gradle (
    echo Deleting .gradle...
    rd /s /q .gradle
)

echo.
echo ==============================================
echo SUCCESS! All locks are cleared.
echo ==============================================
echo 1. Open Android Studio (select 'android' folder)
echo 2. Let the sync finish.
echo 3. Click Run.
pause
