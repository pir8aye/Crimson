/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.subterranean_security.crimson.core.net.factory;

import java.lang.reflect.InvocationTargetException;

import com.subterranean_security.crimson.core.net.executor.BasicExecutor;

public class ExecutorFactory {

	private Class<? extends BasicExecutor> cls;

	public ExecutorFactory(Class<? extends BasicExecutor> cls) {
		this.cls = cls;
	}

	public ExecutorFactory(String className) throws ClassNotFoundException {
		this.cls = (Class<? extends BasicExecutor>) Class.forName(className);
	}

	public BasicExecutor build() {
		try {
			return (BasicExecutor) cls.getConstructor().newInstance(new Object[] {});
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
