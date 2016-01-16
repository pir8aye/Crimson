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
package com.subterranean_security.crimson.viewer.ui.utility;

import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public enum UUtil {
	;

	public static int getPixelLength(String s, Font font) {
		return (int) (font.getStringBounds(s, new FontRenderContext(new AffineTransform(), true, true)).getWidth());
	}

	public static ArrayList<Image> getIconList() {
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit()
				.getImage(UUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-128.png")));
		icons.add(Toolkit.getDefaultToolkit()
				.getImage(UUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-64.png")));
		icons.add(Toolkit.getDefaultToolkit()
				.getImage(UUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-32.png")));
		icons.add(Toolkit.getDefaultToolkit()
				.getImage(UUtil.class.getResource("/com/subterranean_security/crimson/viewer/ui/res/image/c-16.png")));
		return icons;
	}

}
