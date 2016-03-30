package com.subterranean_security.crimson.sv;

import java.util.ArrayList;
import java.util.Date;

public class TrackedAttribute extends Attribute {

	private static final long serialVersionUID = 1L;

	private ArrayList<String> values = new ArrayList<String>();
	private ArrayList<Date> timestamps = new ArrayList<Date>();

	@Override
	public void set(String s) {
		timestamps.add(new Date());
		values.add(s);
		current = s;
	}

}
