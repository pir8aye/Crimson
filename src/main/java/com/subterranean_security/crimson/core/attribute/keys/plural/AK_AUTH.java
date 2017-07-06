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
package com.subterranean_security.crimson.core.attribute.keys.plural;

import com.subterranean_security.crimson.core.attribute.keys.PluralKey;

public enum AK_AUTH implements PluralKey {
	ID, NAME, TYPE, CREATION_DATE,

	// Key authentication
	KEY_SEED,

	// Password authentication
	PASSWORD;

	@Override
	public Object query() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSuperString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGroupID(int groupID) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getGroupID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTypeID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getConstID() {
		// TODO Auto-generated method stub
		return 0;
	}
}
