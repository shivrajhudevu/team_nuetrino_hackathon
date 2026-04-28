@echo off
cd /d C:\Users\shivr\OneDrive\Desktop\EIKOS_proj
echo ==============================================
echo Pushing to GitHub (Judge-Ready Version)...
echo ==============================================
git add .
git commit -m "feat: complete EIKOS v1.0 with Gemini Pro AI, multilingual support, and forensic overlay"
git pull origin main --rebase
git push origin main
echo ==============================================
echo SUCCESS! Code is live on GitHub.
echo https://github.com/shivrajhudevu/team_nuetrino_hackathon
echo ==============================================
pause >nul
