package com.subterranean_security.crimson.core.misc;

import com.subterranean_security.crimson.universal.Universal.Instance;

public class CVID {

	public static final int IID_SPACE = 3;

	public static Instance extractIID(int cvid) {
		int iid = cvid & ((int) Math.pow(2, IID_SPACE) - 1);
		return Instance.values()[iid];
	}

	public static int generate(Instance instance) {
		// TODO get last cvid
		int cvid = 0;

		return (cvid << IID_SPACE) + instance.ordinal();
	}
}
