/******************************************************************************
 *                                                                            *
 *                    Copyright 2016 Subterranean Security                    *
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
package com.subterranean_security.crimson.sv.keylogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.storage.MemMap;
import com.subterranean_security.crimson.core.util.CUtil;

public class Log implements Serializable {

	private static final long serialVersionUID = 1L;

	public MemMap<Date, Page> pages = new MemMap<Date, Page>();

	public Date timestamp;

	public void optimize() {
		Date now = new Date();
		synchronized (pages) {
			for (Date d : pages.keyset()) {
				// only optimize if page is old
				if (!CUtil.Misc.isSameDay(now, d)) {
					try {
						pages.get(d).optimize();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public ArrayList<EV_KEvent> getEventsAfter(Date target) {
		ArrayList<EV_KEvent> ev = new ArrayList<EV_KEvent>();

		for (Date d : pages.keyset()) {
			// skip old dates
			if (d.getTime() + 100000 < target.getTime()) {// TODO magic number
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

		for (Date d : pages.keyset()) {
			// TODO create utility for same day comparison
			if (CUtil.Misc.isSameDay(d, new Date(evKevent.getDate()))) {
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
