// retrieve the Memory MBean
var memory = mbsc.getMBean('java.lang:type=Memory');

print("before gc: " + memory.heapMemoryUsage.get("used"));
memory.gc();
print("after gc:  " + memory.heapMemoryUsage.get("used"));