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

import java.util.List;

import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.universal.Universal.Instance;

/**
 * @author cilki
 * @since 4.0.0
 */
public class ServerProfile extends Profile {

	private static final long serialVersionUID = 1L;

	public ServerProfile() {
		super();
	}

	// public void merge(EV_ServerProfileDelta c) {
	// super.merge(c.getPd());
	//
	//
	// if (UIStore.netMan != null && c.getListenerCount() > 0) {
	// UIStore.netMan.lp.lt.fireTableDataChanged();
	// }
	//
	// if (UIStore.userMan != null && c.getAuthMethodCount() > 0) {
	// UIStore.userMan.up.ut.fireTableDataChanged();
	// }
	//
	// }

	public List<AttributeGroup> getAuthMethods() {
		return null;
	}

	@Override
	public void merge(Object updates) {
		// TODO Auto-generated method stub

	}

	@Override
	public Instance getInstance() {
		return Instance.SERVER;
	}

}
