:: Windows launcher script for Rexster
@echo off

set LIBDIR=lib/EclipseLink
set PUBDIR=public

set EXTRA=
if "%1"=="-s" set EXTRA="-wr %PUBDIR%"

set JAVA_OPTIONS=-Xms32m -Xmx512m

:: Launch the application
java  %JAVA_OPTIONS% %JAVA_ARGS%  -cp %LIBDIR%/*  com.tinkerpop.rexster.Application %* -c config\rexster-eclipselink.xml %EXTRA% 