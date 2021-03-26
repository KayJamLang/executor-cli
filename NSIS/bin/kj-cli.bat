@echo off
setlocal

set dir=%CD%
chcp 65001 2>nul >nul

set java_exe=java.exe

if defined JAVA_HOME (
set java_exe="%JAVA_HOME%\bin\java.exe"
)

%java_exe% -jar -Duser.language=en -DpathFile=%dir% -DlibsPath="%KJ_HOME%\libs" -Dfile.encoding=UTF8 "%KJ_HOME%\bin\kj-cli.jar" %*

for /f "tokens=2" %%# in ("%cmdcmdline%") do if /i "%%#" equ "/c" pause
