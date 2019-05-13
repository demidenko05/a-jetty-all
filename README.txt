site: https://sites.google.com/site/beigesoftware

A-Jetty is multiplatform Jetty 9.2 adapted for Android, it can execute precompiled JSP/JSTL.

Version 1.0.5:
*changed to new beige-log, debug ranges #0..2 base(0..999) android(1000..1999) swing(2000..2999)
*changed to Bouncy Castle 1.61

Licenses:

The Eclipse Public License, Version 1.0
http://www.eclipse.org/legal/epl-v10.html

The Apache Software License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0.txt

GNU General Public License version 2
http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html

3-d party:

Oracle JEE (Servlet API...)
CDDL + GPLv2 with classpath exception
https://javaee.github.io/glassfish/LICENSE

https://github.com/demidenko05/a-tomcat-all - part of Apache Tomcat/JSTL by Apache Software Foundation, adapted for Android to precompile and run JSP/JSTL:
The Apache Software License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0.txt

https://github.com/demidenko05/a-javabeans8 - adapted OpenJDK8 javabeans for Android:
GNU General Public License, version 2, with the Classpath Exception
http://openjdk.java.net/legal/gplv2+ce.html

Bouncy Castle Crypto APIs by the Legion of the Bouncy Castle Inc:
Bouncy Castle License (actually MIT)
http://www.bouncycastle.org/licence.html

SLF4J by QOS.ch:
MIT License
http://www.opensource.org/licenses/mit-license.php

Features:

It runs as well on standard Java 7+ as on Android.
It can performs precompiled by A-Tomcat JSP/JSTL.

The best way of using A-Jetty is use it as embedded server in standalone WEB-application with precompiled JSP.
See https://github.com/demidenko05/beige-software beige-accounting-ajetty and beige-accounting-android for example

To make your WEB-app with JSP working on non-embedded Android A-Jetty (see example  https://github.com/demidenko05/ajetty-webapp-test) you should:
1. Install A-Tomcat, Apache Ant, Maven ... see https://github.com/demidenko05/a-tomcat-all
2. Unpack WAR into [your-webapp-dir] and copy "ajetty-webapp-test/build.xml" at parent folder
3. Precompile JSP to Java Servlets with Ant and A-Tomcat by run outside [your-webapp-dir]:
  $ANT_HOME/bin/ant -Dtomcat.home=$TOMCATA_HOME -Dwebapp.path=[your-webapp-dir]/
4. Compile generated Java files and copy servlets configuration from generated_web.xml into web.xml
5. make DEX file from classes:
  * Unpack all jars into WEB-INF/classes folder in proper order to remove duplicates
  * inside WEB-INF run:
     java -jar $ANDROID_HOME/build-tools/[tools-version]/lib/dx.jar --dex --output=dex-classes.jar classes/
  * move dex-classes.jar into WEB-INF/lib
  * remove unneeded classes form WEB-INF/classes, old jars and JSP files
6. place your unpacked web-app into [Android ext.files dir]/A-Jetty/webapps
7. start A-Jetty on Android, your web-app will be listed.

Many things from standard Java don't work on Android.
Use BeigeORM to create truly cross-platform RDBMS applications (JDBC and Android).
If you use Beige-Logging in WEB-app on non-embedded A-Jetty, then remove those (beige-logging) classes
from DEX file cause they are already loaded.
