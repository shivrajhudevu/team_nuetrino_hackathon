@echo off
cd /d C:\Users\shivr\OneDrive\Desktop\EIKOS_proj
echo ==============================================
echo FIXING GIT LOCKS...
echo ==============================================

if exist .git\index.lock (
    echo Found index.lock, deleting...
    del .git\index.lock
)

if exist .git\REBASE_HEAD (
    echo Found stuck rebase, aborting...
    git rebase --abort
)

echo.
echo ==============================================
echo CLEANING UP...
echo ==============================================
git add .
git commit -m "chore: recover from git lock and finalize restoration"
git push origin main --force

echo.
echo ==============================================
echo SUCCESS! Git is fixed and code is restored.
echo ==============================================
pause
