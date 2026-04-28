@echo off
cd /d C:\Users\shivr\OneDrive\Desktop\EIKOS_proj
echo ==============================================
echo Pushing to GitHub (Judge-Ready Version)...
echo ==============================================
git add .
git commit -m "docs: professional judge-ready README with architecture and demo guide"
git pull origin main --rebase
git push origin main
echo ==============================================
echo SUCCESS! Code is live on GitHub.
echo https://github.com/shivrajhudevu/team_nuetrino_hackathon
echo ==============================================
pause >nul
