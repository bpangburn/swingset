REM $Id$
REM
REM WINDOWS JAVADOC GENERATION SCRIPT FOR SWINGSET
REM
echo off
rmdir .\javadoc /s
mkdir .\javadoc
javadoc -d javadoc -classpath .\lib\glazedlists-1.11.0.jar .\src\com\nqadmin\swingSet\*.java .\src\com\nqadmin\swingSet\datasources\*.java .\src\com\nqadmin\swingSet\formatting\*.java .\src\com\nqadmin\swingSet\formatting\helpers\*.java .\src\com\nqadmin\swingSet\utils\*.java 