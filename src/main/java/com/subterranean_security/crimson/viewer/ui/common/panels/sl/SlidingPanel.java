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
package com.subterranean_security.crimson.viewer.ui.common.panels.sl;

import aurelienribon.slidinglayout.SLPanel;

public abstract class SlidingPanel extends SLPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * The time needed for the transition
	 */
	protected float transitionTime;

	/**
	 * Whether the panel is currently engaged in a transition
	 */
	protected boolean moving;

	/**
	 * Whether the panel is currently "open". Not all panels may have a
	 * meaningful open state.
	 */
	protected boolean open;

	/**
	 * @return True if the panel is moving, false otherwise
	 */
	public boolean isMoving() {
		return moving;
	}

	/**
	 * @return True if the panel is open, false if the panel is closed or does
	 *         not define an open state
	 */
	public boolean isOpen() {
		return open;
	}

}
