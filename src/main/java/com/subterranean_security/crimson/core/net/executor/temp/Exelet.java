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
package com.subterranean_security.crimson.core.net.executor.temp;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;

/**
 * An {@code Exelet} is a component of an Executor which handles incoming
 * messages. Executors are divided into {@code Exelet}s to keep file sizes down.
 * Exelets are not {@code static} to allow state information to be stored
 * directly in the relevant class. Every Exelet should also implement the Exelet
 * interface.
 * 
 * @author cilki
 * @since 5.0.0
 */
public abstract class Exelet {

	/**
	 * The {@code Connector} this {@code Exelet} is handling.
	 */
	protected Connector connector;

	/**
	 * The parent of this {@code Exelet}.
	 */
	protected BasicExecutor parent;

	public Exelet(Connector connector) {
		this.connector = connector;
	}

	public Exelet(Connector connector, BasicExecutor parent) {
		this.connector = connector;
		this.parent = parent;
	}

	/*
	 * Exelets of the same type are equal because there should never be more than
	 * one Exelet of a type defined for an Executor.
	 */
	@Override
	public boolean equals(Object obj) {
		return this.getClass().getName().equals(obj.getClass().getName());
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

}
