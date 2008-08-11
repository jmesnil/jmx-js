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

import static java.lang.Character.isLowerCase;
import static org.mozilla.javascript.Context.getUndefinedValue;
import static org.mozilla.javascript.Context.jsToJava;

import java.util.HashMap;
import java.util.Map;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptableMBean extends ScriptableObject {

	private final ObjectName on;
	private final Map<String, String> attributes = new HashMap<String, String>();
	private final MBeanServerConnection mbsc;

	public ScriptableMBean(ObjectName objectName, MBeanServerConnection conn)
			throws Exception {
		this.on = objectName;
		this.mbsc = conn;
		MBeanInfo mbeanInfo = mbsc.getMBeanInfo(on);
		for (MBeanAttributeInfo attribute : mbeanInfo.getAttributes()) {
			String attrName = attribute.getName();
			if (attrName.length() >= 1 && isLowerCase(attrName.charAt(1))) {
				String attrNameStartingWithLowerCase = attrName.substring(0, 1)
						.toLowerCase()
						+ attrName.substring(1, attrName.length());
				attributes.put(attrNameStartingWithLowerCase, attrName);
			} else {
				// the attribute starts with 2 characters in upper case, use it
				// as it is:
				attributes.put(attrName, attrName);
			}
		}

		for (final MBeanOperationInfo opInfo : mbeanInfo.getOperations()) {
			final String funcName = opInfo.getName();
			final String[] signature = new String[opInfo.getSignature().length];
			for (int i = 0; i < opInfo.getSignature().length; i++) {
				signature[i] = opInfo.getSignature()[i].getType();
			}
			FunctionObject func = new FunctionObject(opInfo.getName(), this
					.getClass().getMethod("toString", new Class[0]), this) {

				@Override
				public Object call(Context arg0, Scriptable arg1,
						Scriptable arg2, Object[] args) {
					Object[] convertedArgs = new Object[args.length];
					try {
						for (int i = 0; i < args.length; i++) {
							convertedArgs[i] = jsToJava(args[i], Object.class);
						}
						return mbsc.invoke(on, funcName, convertedArgs,
								signature);
					} catch (Exception e) {
						e.printStackTrace();
						return getUndefinedValue();
					}
				}
			};
			put(funcName, this, func);
		}
	}

	@Override
	public String getClassName() {
		return "ScriptableMBean";
	}

	public boolean has(String name, Scriptable arg1) {
		if (attributes.containsKey(name)) {
			return true;
		} else {
			return super.has(name, arg1);
		}
	}

	public Object get(String name, Scriptable arg1) {
		if (attributes.containsKey(name)) {
			try {
				Context context = Context.getCurrentContext();
				Scriptable scope = ScriptRuntime.getTopCallScope(context);
				return context.getWrapFactory().wrap(context, scope,
						mbsc.getAttribute(on, attributes.get(name)),
						Object.class);
			} catch (Exception e) {
				e.printStackTrace();
				return getUndefinedValue();
			}
		} else {
			return super.get(name, arg1);
		}
	}

	public void put(String name, Scriptable arg1, Object value) {
		if (attributes.containsKey(name)) {
			try {
				mbsc.setAttribute(on,
						new Attribute(attributes.get(name), value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			super.put(name, arg1, value);
		}
	}

	public Object getDefaultValue(Class arg0) {
		return "[object ScriptableMBean for " + on + "]";
	}
}
