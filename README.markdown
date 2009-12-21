jmx4r is a JavaScript bridge for JMX

It can be used to write simple JavaScripts to manage remote Java applications (e.g. [JBoss](http://www.jboss.org),
[Tomcat](http://tomcat.apache.org/)) using [JMX](http://java.sun.com/javase/technologies/core/mntr-mgmt/javamanagement/).

## Requirements

This bridge works only on Java 6 with a JavaScript engine.
This is the case by default for Sun's JVM on Linux and Windows.

For Mac OS X, there are [additional steps](http://jmesnil.net/weblog/2008/05/14/how-to-include-javascript-engine-in-apples-java-6-vm/):

* Download [JSR 223’s engines](https://scripting.dev.java.net/files/documents/4957/37593/jsr223-engines.zip)
* Copy jsr223-engines/javascript/build/js-engine.jar to /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home/lib/ext/
* Download [Mozilla Rhino 1.7.R1](ftp://ftp.mozilla.org/pub/mozilla.org/js/rhino1_7R1.zip)
* Copy rhino1_7R1/js.jar to /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home/lib/ext/

## Examples

To trigger a garbage collection on a Java application:

0. Run a java application:

    jconsole \
        -J-Dcom.sun.management.jmxremote \
        -J-Dcom.sun.management.jmxremote.port=3000 \
        -J-Dcom.sun.management.jmxremote.ssl=false \
        -J-Dcom.sun.management.jmxremote.authenticate=false

1. Create a file "memory.js".

    var memory = mbsc.getMBean('java.lang:type=Memory');

    memory.gc();
    print("after gc: " + memory.heapMemoryUsage.get("used"));

2. Run the script to trigger a GC on a remote Java application:

   java -jar build/jmx-js.jar memory.js -p 3000 example/memory.js
   
This expects the remote Java application (jconsole started at step 0) running 
on localhost to accept remote JMX connection on the port 3000.

## How to Build

1. Retrieve the project:

   git clone git://github.com/jmesnil/jmx-js.git
   cd jmx-js
   
2. Build the jar

   ant jar

## How to Write Scripts

* An object "mbsc" can be used to retrieve MBean:
  
   var mbean = mbsc.getMBean('java.lang:type=Memory');  
   
   or
   
   var mbeans = mbsc.getMBeans('java.lang:type=MemoryPool,*');
   
* An object "args" contains all the remaning arguments passed on the command line after removing the file name and the optional host and port.
For example, if the script is run with "java -jar jmx-js.jar logging.js -p 3000 FINEST", in the script, args[0] is set to "FINEST".
   