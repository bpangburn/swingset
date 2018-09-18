REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR SWINGSET
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR SWINGSET
REM
echo off
rmdir .\bin /s
mkdir .\bin
javac -source 1.5 -target 1.5 -Xlint:deprecation -Xlint:unchecked -d .\bin -classpath .\lib\glazedlists-1.11.0.jar .\src\com\nqadmin\swingSet\*.java .\src\com\nqadmin\swingSet\datasources\*.java .\src\com\nqadmin\swingSet\formatting\*.java .\src\com\nqadmin\swingSet\formatting\helpers\*.java .\src\com\nqadmin\swingSet\utils\*.java 