@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

REM Installs some necessary libs in the local Maven repo

call mvn install:install-file -Dfile=depfind-1.2.0.jar -DartifactId=depfind -DgroupId=net.sourceforge.depfind -Dversion=1.2.0 -Dpackaging=jar

call mvn install:install-file -Dfile=jmxtools-1.2.1.jar -DartifactId=jmxtools -DgroupId=com.sun.jdmk -Dversion=1.2.1 -Dpackaging=jar

call mvn install:install-file -Dfile=jmxri-1.2.1.jar -DartifactId=jmxri -DgroupId=com.sun.jmx -Dversion=1.2.1 -Dpackaging=jar

call mvn install:install-file -Dfile=javax-jms-1.1.jar -DartifactId=jms -DgroupId=javax.jms -Dversion=1.1 -Dpackaging=jar
