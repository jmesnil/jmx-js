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

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Main {

	public static void main(String[] args) throws Exception {
		String file = args[0];
		System.err.println("executing script from " + file);
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("js");
		String host = "localhost";
		int port = 3000;
		List<String> argsList = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (i == 0) {
				continue;
			} else if ("-h".equals(arg)) {
				host = args[i + 1];
				i++;
			} else if ("-p".equals(arg)) {
				port = Integer.parseInt(args[i + 1]);
				i++;
			} else {
				argsList.add(arg);
			}
		}
		String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port
				+ "/jmxrmi";
		System.err.println("connecting to " + url);
		JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(
				url));

		ScriptableMBeanServerConnection mbsc = new ScriptableMBeanServerConnection(
				connector.getMBeanServerConnection());
		engine.put("mbsc", mbsc);
		engine.put("args", (String[]) argsList.toArray(new String[argsList
				.size()]));
		Reader fileReader = new FileReader(file);
		engine.eval(fileReader);
		fileReader.close();
	}
}
