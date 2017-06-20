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

import javax.swing.ImageIcon;
import javax.swing.border.LineBorder;

public final class UICommon {

	private UICommon() {
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
	public static final Dimension dim_login = new Dimension(405, 320);
	public static final Dimension dim_eula = new Dimension(639, 310);
	public static final Dimension dim_about = new Dimension(630, 370);
	public static final Dimension dim_main = new Dimension(620, 310);
	public static final Dimension dim_btn_up = new Dimension(40, 17);
	public static final Dimension dim_control_panel = new Dimension(594, 300);
	public static final Dimension dim_filemanager = new Dimension(600, 350);
	public static final Dimension dim_networkmanager = new Dimension(573, 600);
	public static final Dimension dim_usermanager = new Dimension(450, 400);
	public static final Dimension dim_control_button = new Dimension(100, 26);
	public static final Dimension dim_generation_report = new Dimension(450, 300);
	public static final Dimension dim_settings = new Dimension(550, 300);

	// borders
	public static final LineBorder basic = new LineBorder(new Color(184, 207, 229));

	// icons
	public static final ImageIcon hmenu_open = UIUtil.getIcon("icons16/general/open_hmenu.png");
	public static final ImageIcon hmenu_close = UIUtil.getIcon("icons16/general/close_hmenu.png");

}
