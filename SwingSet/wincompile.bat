REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR SWINGSET
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR SWINGSET
REM
echo off
rmdir .\bin /s
mkdir .\bin
javac -source 1.4 -target 1.4 -Xlint:deprecation -Xlint:unchecked -d .\bin .\src\com\nqadmin\swingSet\*.java .\src\com\nqadmin\swingSet\datasources\*.java .\src\com\nqadmin\swingSet\formatting\*.java .\src\com\nqadmin\swingSet\formatting\helpers\*.java .\src\com\nqadmin\swingSet\utils\*.java 