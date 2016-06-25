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
package com.subterranean_security.crimson.viewer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public enum UICommon {
	;
	// paths
	// public static final Image appIcon =

	public static String getLicensePath(String lang) {
		return "/com/subterranean_security/crimson/viewer/ui/res/image/license-" + lang + ".png";
	}

	// colors
	public static final Color bg = new Color(60, 59, 57);// 3C 3B 39
	public static final Color menuShadow = new Color(60, 59, 57);
	public static final Color controlTitledBorder = Color.BLACK;

	// fonts
	public static final Font font_menu_button = new Font("Dialog", Font.BOLD, 10);
	public static final Font font_menu_title = new Font("Dialog", Font.BOLD, 12);
	public static final Font font_title_hmenu_help = new Font("Dialog", Font.BOLD, 11);
	public static final Font font_text_help = new Font("Dialog", Font.PLAIN, 10);

	// dimensions
	public static final Dimension dim_btn_up = new Dimension(40, 17);
	public static final Dimension dim_ccp = new Dimension(550, 300);
	public static final Dimension min_files = new Dimension(600, 350);
	public static final Dimension min_netman = new Dimension(573, 600);
	public static final Dimension dim_min_users = new Dimension(450, 400);
	public static final Dimension dim_control_button = new Dimension(100, 27);

}
