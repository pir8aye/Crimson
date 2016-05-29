package com.subterranean_security.crimson.sv.keylogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;

public class Page implements Serializable {

	private static final long serialVersionUID = 1L;

	public Date ref;
	public ArrayList<String> titles = new ArrayList<String>();
	public ArrayList<Event> events = new ArrayList<Event>();

	public Page(EV_KEvent evKevent) {
		ref = new Date(evKevent.getDate());
		titles.add(evKevent.getTitle());
		events.add(new Event(0, 0, evKevent.getEvent()));
	}

	public void addEvent(EV_KEvent evKevent) {
		titles.add(evKevent.getTitle());
		events.add(new Event((int) (evKevent.getDate() - ref.getTime()), titles.size() - 1, evKevent.getEvent()));

	}

}
