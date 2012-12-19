#!/bin/sh

mvn install:install-file -Dfile=depfind-1.2.0.jar -DartifactId=depfind -DgroupId=net.sourceforge.depfind -Dversion=1.2.0 -Dpackaging=jar

mvn install:install-file -Dfile=jmxtools-1.2.1.jar -DartifactId=jmxtools -DgroupId=com.sun.jdmk -Dversion=1.2.1 -Dpackaging=jar

mvn install:install-file -Dfile=jmxri-1.2.1.jar -DartifactId=jmxri -DgroupId=com.sun.jmx -Dversion=1.2.1 -Dpackaging=jar

mvn install:install-file -Dfile=javax-jms-1.1.jar -DartifactId=jms -DgroupId=javax.jms -Dversion=1.1 -Dpackaging=jar
