package com.subterranean_security.crimson.core.attribute.keys.singular;

import com.subterranean_security.crimson.core.attribute.keys.TypeIndex;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;

/**
 * OS X attribute keys
 * 
 * @author cilki
 * @since 5.0.0
 */
public enum AK_OSX implements SingularKey {
	;

	@Override
	public String toString() {
		switch (this) {
		}
		return super.toString();
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
		return TypeIndex.OSX.ordinal();
	}

	@Override
	public Object query() {
		// TODO Auto-generated method stub
		return null;
	}

}
