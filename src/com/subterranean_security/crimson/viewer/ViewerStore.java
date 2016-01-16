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
package com.subterranean_security.crimson.viewer;

import java.util.ArrayList;
import java.util.Iterator;

import com.subterranean_security.crimson.core.Logger;
import com.subterranean_security.crimson.core.proto.msg.Delta.ProfileDelta_EV;
import com.subterranean_security.crimson.sv.Profile;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public enum ViewerStore {

	;

	public static class Profiles {
		public static ArrayList<Profile> profiles = new ArrayList<Profile>();

		public static void add(Profile p) {
			profiles.add(p);
		}

		public static void remove(Integer id) {
			Iterator<Profile> pp = profiles.iterator();
			while (pp.hasNext()) {
				if (pp.next().getClientid() == id) {
					pp.remove();
					return;
				}
			}
		}

		public static void update(ProfileDelta_EV change) {
			boolean flag = true;
			for (Profile p : profiles) {
				if (p.getClientid() == change.getClientid()) {
					Logger.debug("Found existing client and amalgamating");
					flag = false;
					amalgamate(p, change);

					break;
				}
			}

			if (flag) {
				Logger.debug("Adding new profile");
				Profile np = new Profile(change.getClientid());
				amalgamate(np, change);
				profiles.add(np);
			}

			if (MainFrame.main.panel.listLoaded) {
				MainFrame.main.panel.list.refreshTM();
			}
			if (MainFrame.main.panel.graphLoaded) {
				// TODO refresh graph
			}

		}

		private static void amalgamate(Profile p, ProfileDelta_EV c) {
			if (c.hasNetHostname()) {
				Logger.debug("Updating hostname with new value");
				p.setHostname(c.getNetHostname());
			}
			if (c.hasUserName()) {
				Logger.debug("Updating username with new value");
				p.setUsername(c.getUserName());
			}
		}

	}

}
