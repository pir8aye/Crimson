package com.subterranean_security.crimson.universal.misc;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.universal.stores.PrefStore.PTag;

public class PreferenceWrapper {
	private Preferences prefs;

	public PreferenceWrapper(Instance instance) {
		switch (instance) {
		case CLIENT:
			prefs = Preferences.userRoot().node("/com/subterranean_security/crimson/server");
			break;
		case SERVER:
			prefs = Preferences.userRoot().node("/com/subterranean_security/crimson/server");
			break;
		case VIEWER:
			prefs = Preferences.userRoot().node("/com/subterranean_security/crimson/server");
			break;
		default:
			break;

		}
	}

	public boolean isLocked() {
		// debug
		prefs.putInt("LOCK", 0);
		return prefs.getInt("LOCK", 0) == 1;
	}

	public void lock() {
		prefs.putInt("LOCK", 1);
	}

	public String getString(PTag tag) {
		return prefs.get(tag.name(), tag.defaultString());
	}

	public void putString(PTag tag, String str) {
		prefs.put(tag.name(), str);
	}

	public boolean getBoolean(PTag tag) {
		return prefs.getBoolean(tag.name(), tag.defaultBoolean());
	}

	public void putBoolean(PTag tag, boolean bool) {
		prefs.putBoolean(tag.name(), bool);
	}

	public int getInt(PTag tag) {
		return prefs.getInt(tag.name(), tag.defaultInt());
	}

	public void putInt(PTag tag, int i) {
		prefs.putInt(tag.name(), i);
	}

	public void flush() throws BackingStoreException {
		prefs.flush();
	}

	public void close() throws IOException {
		prefs.putInt("LOCK", 0);
		try {
			flush();
		} catch (BackingStoreException e) {
			throw new IOException();
		}
	}
}