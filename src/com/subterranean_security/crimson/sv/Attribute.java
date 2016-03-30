package com.subterranean_security.crimson.sv;

import java.io.Serializable;

public abstract class Attribute implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String current;

	public String get() {
		return current;
	}

	public abstract void set(String s);

}
