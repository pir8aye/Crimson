package com.subterranean_security.crimson.viewer.ui.screen.settings;

import com.subterranean_security.crimson.core.storage.StorageFacility;

public interface SPanel {
	public void setValues(StorageFacility db);

	public void saveValues(StorageFacility db);
}
