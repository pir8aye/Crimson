package com.subterranean_security.crimson.sv;

import java.util.ArrayList;
import java.util.Date;

public class TrackedAttribute extends Attribute {

	private static final long serialVersionUID = 1L;

	private ArrayList<String> values = new ArrayList<String>();
	private ArrayList<Date> timestamps = new ArrayList<Date>();

	@Override
	public void set(String s) {
		set(s, new Date());
	}

	public void set(String s, Date d) {
		timestamps.add(d);
		values.add(s);
		current = s;
	}

	public int size() {
		return values.size();
	}

	public String getValue(int i) {
		return values.get(i);
	}

	public Date getTime(int i) {
		return timestamps.get(i);
	}

}
