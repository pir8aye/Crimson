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
package com.subterranean_security.crimson.viewer.ui;

import java.util.ArrayList;

import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.ClientCPFrame;
import com.subterranean_security.crimson.viewer.ui.screen.generator.GenDialog;
import com.subterranean_security.crimson.viewer.ui.screen.netman.NetMan;
import com.subterranean_security.crimson.viewer.ui.screen.netman.auth.CreateGroup;
import com.subterranean_security.crimson.viewer.ui.screen.netman.auth.CreatePassword;
import com.subterranean_security.crimson.viewer.ui.screen.netman.listener.AddListener;
import com.subterranean_security.crimson.viewer.ui.screen.settings.SettingsDialog;
import com.subterranean_security.crimson.viewer.ui.screen.users.UserMan;

/**
 * Provides storage for UI components that should exist only once
 */
public final class UIStore {

	private UIStore() {
	}

	public static GenDialog genDialog;
	public static NetMan netMan;
	public static UserMan userMan;
	public static AddListener EAddListener;
	public static SettingsDialog settingsDialog;

	public static CreateGroup ECreateGroup;
	public static CreatePassword ECreatePassword;

	public static ArrayList<ClientCPFrame> clientControlPanels = new ArrayList<ClientCPFrame>();

}
