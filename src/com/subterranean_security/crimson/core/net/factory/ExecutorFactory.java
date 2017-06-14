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
