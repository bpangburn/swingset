REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR SWINGSET
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR SWINGSET
REM
echo off
rmdir .\bin /s
mkdir .\bin
javac -d .\bin .\src\com\nqadmin\swingSet\datasources\*.java .\src\com\nqadmin\swingSet\*.java