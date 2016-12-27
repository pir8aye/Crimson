package com.subterranean_security.crimson.viewer.ui.screen.settings;

import com.subterranean_security.crimson.core.storage.LViewerDB;

public interface SPanel {
	public void setValues(LViewerDB db);

	public void saveValues(LViewerDB db);
}
