site: https://sites.google.com/site/beigesoftware

A-Jetty is multi-platform Jetty 9.2 adapted for Android, it can execute precompiled JSP/JSTL.

Version 1.0.5:
*changed to new beige-logging, debug ranges #0..2 base(0..999) android(1000..1999) swing(2000..2999)
*changed to Bouncy Castle 1.61
*Beigesoft sources license changed to BSD 2-Clause.

Licenses:

The Eclipse Public License, Version 1.0
http://www.eclipse.org/legal/epl-v10.html

The Apache Software License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0.txt

BSD 2-Clause License
https://sites.google.com/site/beigesoftware/bsd2csl

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

The best ond only lawful way of using A-Jetty is use it as embedded server in standalone WEB-application with precompiled JSP.
See https://github.com/demidenko05/beige-software beige-accounting-ajetty and beige-accounting-android for example

To make your WEB-app with JSP working on non-embedded Android A-Jetty see example  https://github.com/demidenko05/ajetty-webapp-test
It's only for tests purposes!!! It doesn't comply to the latest Android policy (loading executable binaries from outside)!!!
