/**
 * Copyright (C) 2008 Jeff Mesnil
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.jmesnil.jmx;

import java.lang.management.ManagementFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

public class ScriptableMBeanTest {

	@Test
	public void testScriptableMBean() throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("js");
		ScriptableMBeanServerConnection mbsc = new ScriptableMBeanServerConnection(ManagementFactory.getPlatformMBeanServer());
		engine.put("mbsc", mbsc);
		String script = "var logging = mbsc.getMBean('java.util.logging:type=Logging');" +
		                "print(logging);" +
						"var loggers = logging.loggerNames;" + 
						"for (var i = 0 ; i < loggers.length; i++) {" +
						"  print(loggers[i] + ' ' + logging.getLoggerLevel(loggers[i]));" +
						"  logging.setLoggerLevel(loggers[i], \"FINEST\");" +
						"  print(loggers[i] + ' ' + logging.getLoggerLevel(loggers[i]));" +
						"}";
		engine.eval(script);
		// engine.eval("runtime.foo()");
	}
	
	@Test
	public void testgetMBeans() throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("js");
		ScriptableMBeanServerConnection mbsc = new ScriptableMBeanServerConnection(ManagementFactory.getPlatformMBeanServer());
		engine.put("mbsc", mbsc);
		String script = "var memoryMBeans = mbsc.getMBeans('java.lang:type=MemoryPool,*');" +
						"for (var i = 0 ; i < memoryMBeans.length; i++) {" +
						"  print(memoryMBeans[i].name);" +
						"  memoryMBeans[i].resetPeakUsage();" +
						"}";
		engine.eval(script);
	}
}
