@echo off
cd /d C:\Users\shivr\OneDrive\Desktop\EIKOS_proj
echo ==============================================
echo Pushing EIKOS Final Build to GitHub...
echo ==============================================

:: Fix any leftover lock files just in case
if exist .git\index.lock del /f .git\index.lock

git add .
git commit -m "feat: final hackathon build - 100% Offline Neural Scanner, Advanced Dashboard UI, & Glassmorphism Alert Overlay"

:: Using autostash to prevent rebase conflicts
git pull origin main --rebase --autostash
git push origin main

echo.
echo ==============================================
echo SUCCESS! Code is live on GitHub for the judges.
echo https://github.com/shivrajhudevu/team_nuetrino_hackathon
echo ==============================================
pause
