package com.subterranean_security.crimson.sv.keylogger;

import java.io.Serializable;

public class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	public int timeOffset;
	public int titleOffset;
	public String event;

	public Event(int timeOffset, int titleOffset, String event) {
		this.timeOffset = timeOffset;
		this.titleOffset = titleOffset;
		this.event = event;
	}
}
