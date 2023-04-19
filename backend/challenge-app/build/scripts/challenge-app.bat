@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  challenge-app startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and CHALLENGE_APP_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\challenge-app-1.0-SNAPSHOT.jar;%APP_HOME%\lib\challenge-core-1.0-SNAPSHOT.jar;%APP_HOME%\lib\challenge-data-1.0-SNAPSHOT.jar;%APP_HOME%\lib\kotlin-logging-1.7.8.jar;%APP_HOME%\lib\exposed-0.17.14.jar;%APP_HOME%\lib\kotlin-reflect-1.5.30.jar;%APP_HOME%\lib\kotlinx-coroutines-core-jvm-1.5.2.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.5.30.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.5.30.jar;%APP_HOME%\lib\kotlin-stdlib-1.7.10.jar;%APP_HOME%\lib\dropwizard-core-2.1.4.jar;%APP_HOME%\lib\dropwizard-health-2.1.4.jar;%APP_HOME%\lib\dropwizard-jersey-2.1.4.jar;%APP_HOME%\lib\dropwizard-jetty-2.1.4.jar;%APP_HOME%\lib\dropwizard-request-logging-2.1.4.jar;%APP_HOME%\lib\dropwizard-logging-2.1.4.jar;%APP_HOME%\lib\log4j-over-slf4j-1.7.36.jar;%APP_HOME%\lib\sqlite-jdbc-3.39.3.0.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.7.10.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\dropwizard-configuration-2.1.4.jar;%APP_HOME%\lib\dropwizard-metrics-2.1.4.jar;%APP_HOME%\lib\dropwizard-jackson-2.1.4.jar;%APP_HOME%\lib\dropwizard-validation-2.1.4.jar;%APP_HOME%\lib\dropwizard-servlets-2.1.4.jar;%APP_HOME%\lib\dropwizard-lifecycle-2.1.4.jar;%APP_HOME%\lib\metrics-jetty9-4.2.12.jar;%APP_HOME%\lib\metrics-servlets-4.2.12.jar;%APP_HOME%\lib\metrics-jvm-4.2.12.jar;%APP_HOME%\lib\metrics-jmx-4.2.12.jar;%APP_HOME%\lib\metrics-healthchecks-4.2.12.jar;%APP_HOME%\lib\metrics-logback-4.2.12.jar;%APP_HOME%\lib\metrics-jersey2-4.2.12.jar;%APP_HOME%\lib\metrics-json-4.2.12.jar;%APP_HOME%\lib\metrics-core-4.2.12.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.36.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.36.jar;%APP_HOME%\lib\slf4j-api-1.7.36.jar;%APP_HOME%\lib\dropwizard-util-2.1.4.jar;%APP_HOME%\lib\logback-classic-1.2.11.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.13.4.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.13.4.jar;%APP_HOME%\lib\jackson-module-parameter-names-2.13.4.jar;%APP_HOME%\lib\jackson-module-blackbird-2.13.4.jar;%APP_HOME%\lib\jackson-datatype-joda-2.13.4.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.13.4.jar;%APP_HOME%\lib\jackson-jaxrs-json-provider-2.13.4.jar;%APP_HOME%\lib\jackson-jaxrs-base-2.13.4.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.13.4.jar;%APP_HOME%\lib\jackson-core-2.13.4.jar;%APP_HOME%\lib\jackson-datatype-guava-2.13.4.jar;%APP_HOME%\lib\jackson-databind-2.13.4.2.jar;%APP_HOME%\lib\jackson-annotations-2.13.4.jar;%APP_HOME%\lib\guava-31.1-jre.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\jakarta.servlet-api-4.0.4.jar;%APP_HOME%\lib\jersey-bean-validation-2.37.jar;%APP_HOME%\lib\hibernate-validator-6.2.5.Final.jar;%APP_HOME%\lib\jersey-container-servlet-2.37.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.37.jar;%APP_HOME%\lib\jersey-server-2.37.jar;%APP_HOME%\lib\jakarta.validation-api-2.0.2.jar;%APP_HOME%\lib\jersey-metainf-services-2.37.jar;%APP_HOME%\lib\jersey-client-2.37.jar;%APP_HOME%\lib\jersey-hk2-2.37.jar;%APP_HOME%\lib\jersey-common-2.37.jar;%APP_HOME%\lib\jakarta.ws.rs-api-2.1.6.jar;%APP_HOME%\lib\argparse4j-0.9.0.jar;%APP_HOME%\lib\jetty-servlet-9.4.49.v20220914.jar;%APP_HOME%\lib\jetty-security-9.4.49.v20220914.jar;%APP_HOME%\lib\jetty-server-9.4.49.v20220914.jar;%APP_HOME%\lib\jetty-servlets-9.4.49.v20220914.jar;%APP_HOME%\lib\jetty-http-9.4.49.v20220914.jar;%APP_HOME%\lib\jetty-io-9.4.49.v20220914.jar;%APP_HOME%\lib\jetty-util-ajax-9.4.49.v20220914.jar;%APP_HOME%\lib\jetty-util-9.4.49.v20220914.jar;%APP_HOME%\lib\jetty-setuid-java-1.0.4.jar;%APP_HOME%\lib\jakarta.inject-api-1.0.5.jar;%APP_HOME%\lib\h2-1.4.199.jar;%APP_HOME%\lib\joda-time-2.12.0.jar;%APP_HOME%\lib\caffeine-2.9.3.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\jakarta.el-3.0.4.jar;%APP_HOME%\lib\commons-text-1.10.0.jar;%APP_HOME%\lib\logback-access-1.2.11.jar;%APP_HOME%\lib\logback-core-1.2.11.jar;%APP_HOME%\lib\logback-throttling-appender-1.1.9.jar;%APP_HOME%\lib\javassist-3.29.2-GA.jar;%APP_HOME%\lib\hk2-locator-2.6.1.jar;%APP_HOME%\lib\hk2-api-2.6.1.jar;%APP_HOME%\lib\hk2-utils-2.6.1.jar;%APP_HOME%\lib\jakarta.annotation-api-1.3.5.jar;%APP_HOME%\lib\metrics-annotation-4.2.12.jar;%APP_HOME%\lib\profiler-1.1.1.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.3.jar;%APP_HOME%\lib\jboss-logging-3.4.1.Final.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\checker-qual-3.19.0.jar;%APP_HOME%\lib\error_prone_annotations-2.11.0.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\snakeyaml-1.31.jar;%APP_HOME%\lib\commons-lang3-3.12.0.jar;%APP_HOME%\lib\aopalliance-repackaged-2.6.1.jar;%APP_HOME%\lib\jakarta.inject-2.6.1.jar;%APP_HOME%\lib\jetty-continuation-9.4.49.v20220914.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.3.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.2.jar


@rem Execute challenge-app
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %CHALLENGE_APP_OPTS%  -classpath "%CLASSPATH%" org.light.challenge.app.AppKt %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable CHALLENGE_APP_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%CHALLENGE_APP_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
