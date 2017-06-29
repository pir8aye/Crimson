package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;
import com.subterranean_security.crimson.universal.Universal;

/**
 * Meta attribute keys
 * 
 * @author cilki
 * @since 4.0.0
 */
public enum AK_META implements SingularKey {
	CVID, ONLINE, FIRST_CONTACT, VERSION;

	@Override
	public String toString() {
		switch (this) {
		case CVID:
			return "Client/Viewer ID";
		case FIRST_CONTACT:
			return "First Contact";
		case ONLINE:
			return "Online";
		case VERSION:
			return "Version";
		default:
			return super.toString();
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

	@Override
	public int getTypeID() {
		return TypeIndex.META.ordinal();
	}

	@Override
	public Object query() {
		switch (this) {
		case CVID:
			return LcvidStore.cvid;
		case FIRST_CONTACT:
			return null;
		case ONLINE:
			return ConnectionStore.connectedDirectly(Reserved.SERVER);
		case VERSION:
			return Universal.version;
		default:
			throw new UnsupportedOperationException("Cannot query: " + this);
		}
	}

}
