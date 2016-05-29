package com.subterranean_security.crimson.sv.keylogger;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.storage.MemMap;

public class Log implements Serializable {

	private static final long serialVersionUID = 1L;

	public MemMap<Date, Page> pages = new MemMap<Date, Page>();

	public void addEvent(EV_KEvent evKevent) {

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

	}
}
