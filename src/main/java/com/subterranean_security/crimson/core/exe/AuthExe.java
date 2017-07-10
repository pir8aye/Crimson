package com.subterranean_security.crimson.core.exe;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.universal.Universal;

public abstract class AuthExe extends Exelet {

	public AuthExe(Connector connector, BasicExecutor parent) {
		super(connector, parent);
	}

	/**
	 * The size of the random String used in Key authentication
	 */
	public static final int MAGIC_LENGTH = 128;

	protected int authID;
	protected AuthStage currentStage = AuthStage.UNAUTHENTICATED;

	protected enum AuthStage {
		UNAUTHENTICATED, GROUP_STAGE1, GROUP_STAGE2, AUTHENTICATED;
	}

	protected void acceptClient() {
		parent.initAuth();

		currentStage = AuthStage.AUTHENTICATED;
		connector.setState(ConnectionState.AUTHENTICATED);
		connector.setInstance(Universal.Instance.CLIENT);

		// ProfileStore.getClient(receptor.getCvid()).setAuthID(authID);

		// ConnectionStore.add(receptor);
	}

	protected void rejectClient() {
		currentStage = AuthStage.UNAUTHENTICATED;
		connector.setState(ConnectionState.CONNECTED);
	}

}
