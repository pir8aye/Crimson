package com.subterranean_security.crimson.sv.profile.comparator;

import java.util.Comparator;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.sv.profile.ClientProfile;

/**
 * @author cilki
 * @since 4.0.0
 */
public class SingularAttributeComparator implements Comparator<ClientProfile> {
	private AttributeKey key;

	public SingularAttributeComparator(AttributeKey sk) {
		this.key = sk;
	}

	@Override
	public int compare(ClientProfile o1, ClientProfile o2) {
		if (o1.get(key) == null) {
			return (o2.get(key) == null) ? 0 : 1;
		}
		return o1.get(key).compareTo(o2.get(key));
	}
}