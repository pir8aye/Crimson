package com.subterranean_security.crimson.sv;

public class UntrackedAttribute extends Attribute {

	private static final long serialVersionUID = 1L;

	@Override
	public void set(String s) {
		current = s;
	}

}
