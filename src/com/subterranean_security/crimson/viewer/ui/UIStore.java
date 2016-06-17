package com.subterranean_security.crimson.viewer.ui;

import com.subterranean_security.crimson.viewer.ui.screen.netman.AddDialog;
import com.subterranean_security.crimson.viewer.ui.screen.netman.NetMan;
import com.subterranean_security.crimson.viewer.ui.screen.netman.auth.CreateGroup;
import com.subterranean_security.crimson.viewer.ui.screen.netman.auth.CreatePassword;
import com.subterranean_security.crimson.viewer.ui.screen.users.UserMan;

/**
 * Provides storage for UI components that should exist only once
 */
public enum UIStore {
	;

	public static NetMan netMan;
	public static UserMan userMan;
	public static AddDialog addDialog;

	public static CreateGroup createGroup;
	public static CreatePassword createPassword;

}
