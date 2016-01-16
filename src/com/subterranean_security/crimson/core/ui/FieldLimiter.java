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
package com.subterranean_security.crimson.core.ui;

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class FieldLimiter extends PlainDocument {

	private static final long serialVersionUID = 1L;
	private int limit;
	private boolean uppercase;
	private Pattern regex;

	public FieldLimiter(int limit, boolean uppercase) {
		super();
		this.limit = limit;
		this.uppercase = uppercase;
		regex = Pattern.compile(".*");
	}

	public FieldLimiter(int limit) {
		this(limit, false);
	}

	public FieldLimiter(int limit, String regex) {
		this(limit);
		this.regex = Pattern.compile(regex);
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null)
			return;

		if ((getLength() + str.length()) <= limit && regex.matcher(str).matches()) {
			super.insertString(offset, uppercase ? str.toUpperCase() : str, attr);
		}
	}

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {

		fb.replace(offset, length, uppercase ? text.toUpperCase() : text, attrs);
	}

}