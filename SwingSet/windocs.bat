REM $Id$
REM
REM WINDOWS JAVADOC GENERATION SCRIPT FOR SWINGSET
REM
echo off
rmdir .\javadoc /s
mkdir .\javadoc
javadoc -d javadoc \src\com\nqadmin\swingSet\datasources\*.java .\src\com\nqadmin\swingSet\*.java