:: Windows launcher script for Gremlin Groovy

@echo off

set LIBDIR=lib\Hibernate

set OLD_CLASSPATH=%CLASSPATH%
set CP=

for %%i in (%LIBDIR%\*.jar) do call :concatsep %%i

set JAVA_OPTIONS=-Xms32m -Xmx512m -Djpagraph.unit-name=HibernateUnit

:: Launch the application

if "%1" == "" goto console
if "%1" == "-e" goto script
if "%1" == "-v" goto version

:console

set CLASSPATH=%CP%;%OLD_CLASSPATH%
java %JAVA_OPTIONS% %JAVA_ARGS% com.tinkerpop.gremlin.groovy.console.Console %*

set CLASSPATH=%OLD_CLASSPATH%
goto :eof

:script

set strg=

FOR %%X IN (%*) DO (
CALL :concat %%X %1 %2
)

set CLASSPATH=%CP%;%OLD_CLASSPATH%
java %JAVA_OPTIONS% %JAVA_ARGS% com.tinkerpop.gremlin.groovy.jsr223.ScriptExecutor %strg%
set CLASSPATH=%OLD_CLASSPATH%
goto :eof

:version

set CLASSPATH=%CP%;%OLD_CLASSPATH%
java %JAVA_OPTIONS% %JAVA_ARGS% com.tinkerpop.gremlin.Version

set CLASSPATH=%OLD_CLASSPATH%
goto :eof

:concat

if %1 == %2 goto skip

SET strg=%strg% %1

:concatsep

if "%CP%" == "" (
set CP=%1
)else (
set CP=%CP%;%1
)

:skip
