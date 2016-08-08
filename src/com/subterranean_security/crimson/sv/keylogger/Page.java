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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;

public class Page implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(Page.class);

	public static final int sameWindowSeparationInterval = 1000 * 60 * 60;

	public Date ref;
	public ArrayList<String> titles = new ArrayList<String>();
	public ArrayList<Event> events = new ArrayList<Event>();

	public Page(EV_KEvent evKevent) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			// convert this event time to start of day
			ref = df.parse(df.format(new Date(evKevent.getDate())));
		} catch (ParseException e) {
			log.warn("Not setting page date reference to start of day");
			ref = new Date(evKevent.getDate());
		}

		titles.add(evKevent.getTitle());
		events.add(new Event(0, 0, evKevent.getEvent()));
	}

	public void addEvent(EV_KEvent evKevent) {
		events.add(new Event((int) (evKevent.getDate() - ref.getTime()), addTitle(evKevent.getTitle()),
				evKevent.getEvent()));

	}

	private boolean optimal = false;

	public void optimize() {
		if (optimal) {
			return;
		}

		ArrayList<Paragraph> paragraphs = getParagraphs();
		events.clear();
		titles.clear();
		for (Paragraph p : paragraphs) {
			events.add(
					new Event((int) (p.getDate().getTime() - ref.getTime()), addTitle(p.getTitle()), p.getContents()));

		}

		optimal = true;

	}

	/**
	 * 
	 * 
	 * @param t
	 *            title to add
	 * @return index of title
	 */
	private int addTitle(String t) {
		int index = titles.indexOf(t);
		if (index == -1) {
			titles.add(t);
			return titles.size() - 1;
		} else {
			return index;
		}

	}

	public ArrayList<Paragraph> getParagraphs() {
		ArrayList<Paragraph> p = new ArrayList<Paragraph>();
		String lastTitle = null;
		Date lastDate = null;

		for (Event k : events) {
			String title = titles.get(k.titleOffset);
			Date date = new Date(ref.getTime() + k.timeOffset);
			if (lastTitle != null && lastTitle.equals(title)) {
				if (lastDate != null && date.getTime() - lastDate.getTime() < sameWindowSeparationInterval) {
					// old paragraph
					Paragraph pp = p.get(p.size() - 1);
					pp.setContents(pp.getContents() + k.event);
					continue;
				}

			}

			// new paragraph
			p.add(new Paragraph(title, date, k.event));

			lastTitle = title;
			lastDate = date;

		}
		return p;
	}

}
