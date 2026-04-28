@echo off
cd /d C:\Users\shivr\OneDrive\Desktop\EIKOS_proj
echo ==============================================
echo Installing AI requirements...
echo ==============================================
pip install google-generativeai flask python-dotenv requests
echo.
echo ==============================================
echo Starting EIKOS AI Backend...
echo ==============================================
python app.py
pause
