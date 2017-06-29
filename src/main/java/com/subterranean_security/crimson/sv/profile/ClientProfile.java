/******************************************************************************
 *                                                                            *
 *                    Copyright 2016 Subterranean Security                    *
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
package com.subterranean_security.crimson.sv.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_CLIENT;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_KEYLOGGER;
import com.subterranean_security.crimson.core.util.IDGen.Reserved;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.State;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.Trigger;
import com.subterranean_security.crimson.sv.keylogger.Log;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * A {@code ClientProfile} is a container for client data. This class consists
 * mainly of convenience methods while {@code Profile} does most of the work.
 * 
 * @author cilki
 * @since 4.0.0
 */
public class ClientProfile extends Profile {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ClientProfile.class);

	public int getAuthID() {
		return getInt(AK_CLIENT.AUTH_ID);
	}

	public void setAuthID(int authID) {
		set(AK_CLIENT.AUTH_ID, authID);
	}

	public Log getKeylog() {
		return (Log) getObject(AK_CLIENT.KEYLOG);
	}

	public ClientProfile(int cid) {
		this();

		if (cid == Reserved.SERVER)
			throw new IllegalArgumentException("A ClientProfile cannot have a reserved CVID");

		setCvid(cid);
	}

	public ClientProfile() {
		super();
	}

	public Trigger getKeyloggerTrigger() {
		return Trigger.valueOf(get(AK_KEYLOGGER.TRIGGER));
	}

	public void setKeyloggerTrigger(Trigger trigger) {
		set(AK_KEYLOGGER.TRIGGER, trigger.toString());
	}

	public int getKeyloggerTriggerValue() {
		return getInt(AK_KEYLOGGER.TRIGGER_VALUE);
	}

	public void setKeyloggerTriggerValue(int triggerValue) {
		set(AK_KEYLOGGER.TRIGGER_VALUE, triggerValue);
	}

	public State getKeyloggerState() {
		return State.valueOf(get(AK_KEYLOGGER.STATE));
	}

	public void setKeyloggerState(State keyloggerState) {
		set(AK_KEYLOGGER.STATE, keyloggerState.toString());
	}

	@Override
	public void merge(Object updates) {
		// TODO Auto-generated method stub

	}

	@Override
	public Instance getInstance() {
		return Instance.CLIENT;
	}

}
