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

import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.ScriptRuntime;

public class ScriptableMBeanServerConnection {
	private MBeanServerConnection mbsc;
	private Context ctx;

	public ScriptableMBeanServerConnection(MBeanServerConnection mbsc) {
		this.mbsc = mbsc;
	}

	public MBeanServerConnection getObject() {
		return mbsc;
	}

	public ScriptableMBean getMBean(String objectNameStr) throws Exception {
		ObjectName on = new ObjectName(objectNameStr);
		return new ScriptableMBean(on, mbsc);
	}

	public NativeJavaArray getMBeans(String objectNameFilterStr) throws Exception {
		ObjectName filterON = new ObjectName(objectNameFilterStr);
		Set<ObjectName> objectNames = mbsc.queryNames(filterON, null);
		ScriptableMBean[] mbeans = new ScriptableMBean[objectNames.size()];
		int i = 0;
		for (ObjectName objectName : objectNames) {
			mbeans[i++] = new ScriptableMBean(objectName, mbsc);
		}
		return NativeJavaArray.wrap(ScriptRuntime.getTopCallScope(Context.getCurrentContext()), mbeans);
	}
}
