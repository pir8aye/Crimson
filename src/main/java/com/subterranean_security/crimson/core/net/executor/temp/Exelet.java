package com.subterranean_security.crimson.core.net.executor.temp;

import com.subterranean_security.crimson.core.net.Connector;

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

	public Exelet(Connector connector) {
		this.connector = connector;
	}

}
