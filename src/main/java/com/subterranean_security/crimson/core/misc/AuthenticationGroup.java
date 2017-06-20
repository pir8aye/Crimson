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
package com.subterranean_security.crimson.core.misc;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

/**
 * 
 * The group key-pair authentication mechanism is the strongest out of the
 * supported authentication schemes, utilizing DSA keys to verify client and
 * server identities. Upon installer generation, the public key is embedded in
 * the installer.<br>
 * <br>
 * <div align="center"><img src="../../../../../files/client_auth.png" /></div>
 * 
 * @author Tyler Cook
 *
 */
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
