/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.core.store;

import static com.subterranean_security.crimson.universal.Flags.DEV_MODE;

import java.util.ArrayList;
import java.util.List;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.misc.CVID;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedMap;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.Profile;
import com.subterranean_security.crimson.sv.profile.ServerProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;

/**
 * @author cilki
 * @since 4.0.0
 */
public abstract class ProfileStore {

	/**
	 * The listener which handles profile events
	 */
	private static ProfileListener listener;

	/**
	 * Set the ProfileListener
	 * 
	 * @param listener
	 */
	public static void setProfileListener(ProfileListener listener) {
		ProfileStore.listener = listener;
	}

	/**
	 * Maps CVIDs to ClientProfiles
	 */
	protected static CachedMap<Integer, ClientProfile> clientProfiles;

	/**
	 * Maps CVIDs to ViewerProfiles
	 */
	protected static CachedMap<Integer, ViewerProfile> viewerProfiles;

	/**
	 * Maps CVIDs to ServerProfiles
	 */
	protected static CachedMap<Integer, ServerProfile> serverProfiles;

	public static void initialize(CachedMap<Integer, ClientProfile> clientMap,
			CachedMap<Integer, ViewerProfile> viewerMap, CachedMap<Integer, ServerProfile> serverMap) {
		clientProfiles = clientMap;
		viewerProfiles = viewerMap;
		serverProfiles = serverMap;

		if (DEV_MODE) {
			// charcoal
		}
	}

	/**
	 * @return The number of ClientProfiles stored
	 */
	public static int countTotalClients() {
		return clientProfiles.size();
	}

	/**
	 * @return The number of ViewerProfiles stored
	 */
	public static int countTotalViewers() {
		return viewerProfiles.size();
	}

	/**
	 * @return The number of profiles stored
	 */
	public static int countTotalProfiles() {
		return countTotalClients() + countTotalViewers();
	}

	/**
	 * Add a client to the store
	 * 
	 * @param cp
	 */
	public static void addClient(ClientProfile cp) {
		clientProfiles.put(cp.getCvid(), cp);
	}

	/**
	 * Add a viewer to the store
	 * 
	 * @param cp
	 */
	public static void addViewer(ViewerProfile vp) {
		viewerProfiles.put(vp.getCvid(), vp);
	}

	/**
	 * Retrieve a profile from the store
	 * 
	 * @param cvid
	 *            The client/viewer ID
	 * @return The requested Profile
	 */
	public static Profile getProfile(int cvid) {
		switch (CVID.extractIID(cvid)) {
		case CLIENT:
			return getClient(cvid);
		case SERVER:
			return getServer(cvid);
		case VIEWER:
			return getViewer(cvid);
		default:
			return null;
		}
	}

	/**
	 * Retrieve a client from the store
	 * 
	 * @param cid
	 *            The client ID of the requested profile
	 * @return The requested ClientProfile
	 */
	public static ClientProfile getClient(int cid) {
		return clientProfiles.get(cid);
	}

	/**
	 * Retrieve a viewer from the store
	 * 
	 * @param vid
	 *            The viewer ID of the requested profile
	 * @return The requested ViewerProfile
	 */
	public static ViewerProfile getViewer(int vid) {
		return viewerProfiles.get(vid);
	}

	/**
	 * Retrieve a server from the store
	 * 
	 * @param sid
	 *            The server ID of the requested profile
	 * @return The requested ServerProfile
	 */
	public static ServerProfile getServer(int sid) {
		return serverProfiles.get(sid);
	}

	/**
	 * Retrieve a viewer from the store
	 * 
	 * @param user
	 *            The username of the requested profile
	 * @return The requested ViewerProfile
	 */
	public static ViewerProfile getViewer(String user) {
		for (Integer i : viewerProfiles.keySet()) {
			ViewerProfile vp = viewerProfiles.get(i);
			if (user.equals(vp.get(AK_VIEWER.USER))) {
				return vp;
			}
		}

		return null;
	}

	public static ServerProfile getServer() {
		// TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
		return serverProfiles.values().iterator().next();
	}

	/**
	 * Remove a profile from the store
	 * 
	 * @param cvid
	 *            The client/viewer ID to remove
	 * @return The removed Profile or null if not found
	 */
	public static Profile removeProfile(int cvid) {
		Profile removal = removeViewer(cvid);
		if (removal != null)
			return removal;
		removal = removeClient(cvid);
		if (removal != null)
			return removal;
		return null;
	}

	/**
	 * Remove a client from the store
	 * 
	 * @param cid
	 *            The client ID to remove
	 * @return The removed ClientProfile or null if not found
	 */
	public static ClientProfile removeClient(int cid) {
		return clientProfiles.remove(cid);
	}

	/**
	 * Remove a viewer from the store
	 * 
	 * @param vid
	 *            The viewer ID to remove
	 * @return The removed ViewerProfile or null if not found
	 */
	public static ViewerProfile removeViewer(int vid) {
		return viewerProfiles.remove(vid);
	}

	public static List<ViewerProfile> getViewers() {
		List<ViewerProfile> viewers = new ArrayList<ViewerProfile>();
		viewers.addAll(viewerProfiles.values());
		return viewers;
	}

	public static List<ClientProfile> getClients() {
		List<ClientProfile> clients = new ArrayList<ClientProfile>();
		clients.addAll(clientProfiles.values());
		return clients;
	}

	public static List<Profile> getProfiles() {
		List<Profile> all = new ArrayList<Profile>();
		all.addAll(clientProfiles.values());
		all.addAll(viewerProfiles.values());
		return all;
	}

	// TODO server and viewer too
	public static void update(EV_ProfileDelta change) {
		boolean onlineChanged = false;
		boolean firstConnection = false;

		ClientProfile cp = getClient(change.getCvid());
		if (cp != null) {

			boolean online = cp.isOnline();
			cp.merge(change);
			onlineChanged = online != cp.isOnline();

		} else {
			firstConnection = true;

			// add new profile
			cp = new ClientProfile(change.getCvid());
			cp.merge(change);

			onlineChanged = cp.isOnline();
			addClient(cp);

		}

		if (onlineChanged) {
			if (cp.isOnline()) {
				if (firstConnection) {
					listener.clientOnlineFirstTime(cp);
				} else {
					listener.clientOnline(cp);
				}
			} else {
				listener.clientOffline(cp);
			}
		}

	}

	public static Profile getLocalProfile() {
		return getProfile(LcvidStore.cvid);
	}

	public interface ProfileListener {
		public void clientOnline(ClientProfile cp);

		public void viewerOnline(ViewerProfile vp);

		public void clientOnlineFirstTime(ClientProfile cp);

		public void viewerOnlineFirstTime(ViewerProfile vp);

		public void clientOffline(ClientProfile cp);

		public void viewerOffline(ViewerProfile vp);
	}

}
