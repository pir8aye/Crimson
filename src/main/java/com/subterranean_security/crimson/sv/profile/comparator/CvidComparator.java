package com.subterranean_security.crimson.sv.profile.comparator;

import java.util.Comparator;

import com.subterranean_security.crimson.sv.profile.ClientProfile;

public class CvidComparator implements Comparator<ClientProfile> {
	@Override
	public int compare(ClientProfile o1, ClientProfile o2) {
		return o1.getCvid() - o2.getCvid();
	}
}