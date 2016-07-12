package com.subterranean_security.crimson.core.util;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public class AuthenticationGroup implements Serializable, Destroyable {

	private static final long serialVersionUID = 1L;

	private String name;
	private PrivateKey privateKey;
	private PublicKey groupKey;

	@Override
	public void destroy() throws DestroyFailedException {
		privateKey.destroy();
		Destroyable.super.destroy();
	}

	public AuthenticationGroup(String name, PrivateKey privateKey, PublicKey groupKey) {
		setName(name);
		this.privateKey = privateKey;
		this.groupKey = groupKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.name = n;
	}

	public byte[] getPrivateKey() {
		return privateKey.getEncoded();
	}

	public byte[] getGroupKey() {
		return groupKey.getEncoded();
	}

}
