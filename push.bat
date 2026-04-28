@echo off
echo ==============================================
echo Pushing EIKOS local detection update...
echo ==============================================
git add .
git commit -m "feat: migrate to 100%% local on-device scam detection (no server needed)"
git pull origin main --rebase
git push origin main
echo ==============================================
echo DONE! Press any key to close.
pause >nul
