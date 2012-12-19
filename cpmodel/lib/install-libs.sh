#!/bin/sh

mvn install:install-file -Dfile=org.eclipse.emf.common_2.5.0.v200906151043.jar -DartifactId=emf-common -DgroupId=org.eclipse.emf -Dversion=2.5.0.v200906151043 -Dpackaging=jar

mvn install:install-file -Dfile=org.eclipse.emf.ecore_2.5.0.v200906151043.jar -DartifactId=ecore -DgroupId=org.eclipse.emf -Dversion=2.5.0.v200906151043 -Dpackaging=jar

mvn install:install-file -Dfile=org.eclipse.emf.ecore.xmi_2.5.0.v200906151043.jar -DartifactId=ecore-xmi -DgroupId=org.eclipse.emf.ecore -Dversion=2.5.0.v200906151043 -Dpackaging=jar

mvn install:install-file -Dfile=org.eclipse.uml2.common_1.5.0.v200905041045.jar -DartifactId=uml2-common -DgroupId=org.eclipse.uml2 -Dversion=1.5.0.v200905041045 -Dpackaging=jar

mvn install:install-file -Dfile=org.eclipse.uml2.uml_3.0.1.v200908281330.jar -DartifactId=uml -DgroupId=org.eclipse.uml2 -Dversion=3.0.1.v200908281330 -Dpackaging=jar

mvn install:install-file -Dfile=org.eclipse.uml2.uml.resources_3.0.0.v200906011111.jar -DartifactId=uml-resources -DgroupId=org.eclipse.uml2.uml -Dversion=3.0.0.v200906011111 -Dpackaging=jar


