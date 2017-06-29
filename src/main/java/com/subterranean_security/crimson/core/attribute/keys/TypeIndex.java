package com.subterranean_security.crimson.core.attribute.keys;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.subterranean_security.crimson.core.attribute.keys.plural.AK_AUTH;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_CPU;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_DISP;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_GPU;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_LISTENER;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_NIC;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_TORRENT;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_CLIENT;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_JVM;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_KEYLOGGER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LIN;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LOC;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_META;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_MOBO;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OS;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OSX;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_RAM;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_SERVER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_USER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_WIN;

public enum TypeIndex {
	// Type ID is encoded in one byte
	CPU, GPU, NIC, TORRENT, CLIENT, NET, RAM, USER, WIN, KEYLOGGER, LIN, LOC, VIEWER, MOBO, OSX, SERVER,

	// Type ID is encoded in two bytes
	DISP, META, JVM, OS, LISTENER, AUTH;

	public int getTypeID() {
		return ordinal();
	}

	public static Class<?> getType(int typeID) {
		switch (values()[typeID]) {
		case AUTH:
			return AK_AUTH.class;
		case CLIENT:
			return AK_CLIENT.class;
		case CPU:
			return AK_CPU.class;
		case DISP:
			return AK_DISP.class;
		case GPU:
			return AK_GPU.class;
		case JVM:
			return AK_JVM.class;
		case KEYLOGGER:
			return AK_KEYLOGGER.class;
		case LIN:
			return AK_LIN.class;
		case LISTENER:
			return AK_LISTENER.class;
		case LOC:
			return AK_LOC.class;
		case META:
			return AK_META.class;
		case MOBO:
			return AK_MOBO.class;
		case NET:
			return AK_NET.class;
		case NIC:
			return AK_NIC.class;
		case OS:
			return AK_OS.class;
		case OSX:
			return AK_OSX.class;
		case RAM:
			return AK_RAM.class;
		case SERVER:
			return AK_SERVER.class;
		case TORRENT:
			return AK_TORRENT.class;
		case USER:
			return AK_USER.class;
		case VIEWER:
			return AK_VIEWER.class;
		case WIN:
			return AK_WIN.class;
		default:
			return null;
		}
	}

	// TODO
	@SuppressWarnings("unchecked")
	public static final Class<SingularKey>[] types = new Class[] {
			// TypeID is one byte
			AK_CLIENT.class, AK_JVM.class, AK_KEYLOGGER.class, AK_LIN.class, AK_LOC.class, AK_META.class, AK_MOBO.class,
			AK_NET.class, AK_OS.class, AK_OSX.class, AK_RAM.class, AK_SERVER.class, AK_USER.class, AK_VIEWER.class,
			AK_WIN.class, AK_CPU.class,

			// TypeID is two bytes
			AK_DISP.class, AK_GPU.class, AK_NIC.class, AK_TORRENT.class };

	public static final List<SingularKey> keys = getAllKeys();

	public static List<SingularKey> getAllKeys() {
		List<SingularKey> l = new LinkedList<>();
		for (Class<SingularKey> k : types) {
			l.addAll(Arrays.asList(k.getEnumConstants()));
		}
		return l;
	}
}
