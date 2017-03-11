package com.subterranean_security.crimson.server.store;

import java.util.ArrayList;
import java.util.Set;

import com.subterranean_security.crimson.core.misc.MemMap;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public class ProfileStore {
	private static MemMap<Integer, ClientProfile> clientProfiles;
	private static MemMap<Integer, ViewerProfile> viewerProfiles;

	static {
		try {
			clientProfiles = (MemMap<Integer, ClientProfile>) DatabaseStore.getDatabase().getObject("profiles.clients");
			clientProfiles.setDatabase(DatabaseStore.getDatabase());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			viewerProfiles = (MemMap<Integer, ViewerProfile>) DatabaseStore.getDatabase().getObject("profiles.viewers");
			viewerProfiles.setDatabase(DatabaseStore.getDatabase());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static ClientProfile getClient(int cid) {
		try {
			return clientProfiles.get(cid).initialize();
		} catch (Exception e) {
			return null;
		}
	}

	public static void addClient(ClientProfile p) {
		clientProfiles.put(p.getCid(), p);
	}

	public static ViewerProfile getViewer(int vid) {
		try {
			return viewerProfiles.get(vid);
		} catch (Exception e) {
			return null;
		}
	}

	public static ViewerProfile getViewer(String user) {
		try {
			for (Integer i : viewerProfiles.keyset()) {
				ViewerProfile vp = viewerProfiles.get(i);
				if (vp.getUser().equals(user)) {
					return vp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<ViewerProfile> getViewersWithAuthorityOnClient(int cid) {
		ArrayList<ViewerProfile> vps = new ArrayList<ViewerProfile>();
		try {
			for (Integer i : viewerProfiles.keyset()) {
				// TODO filter
				vps.add(viewerProfiles.get(i));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vps;
	}

	public static ArrayList<ClientProfile> getClientsUnderAuthority(int vid) {
		ArrayList<ClientProfile> cps = new ArrayList<ClientProfile>();
		try {
			for (Integer i : clientProfiles.keyset()) {
				// TODO filter
				cps.add(clientProfiles.get(i));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cps;
	}

	public static Set<Integer> getViewerKeyset() {
		return viewerProfiles.keyset();
	}

	public static Set<Integer> getClientKeyset() {
		return clientProfiles.keyset();
	}

	public static void addViewer(ViewerProfile p) {
		viewerProfiles.put(p.getCvid(), p);
	}

}