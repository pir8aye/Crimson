package com.subterranean_security.crimson.core.util;

import java.io.File;

import com.subterranean_security.crimson.core.Common;

public class TempUtil {
	public static final String prefix = "crimson_temp_";

	public static File getFile(String name) {
		File f = new File(Common.Directories.tmp.getAbsolutePath() + File.separator + name);
		f.deleteOnExit();
		return f;
	}

	public static File getFile() {
		File f = new File(Common.Directories.tmp.getAbsolutePath() + File.separator + prefix + RandomUtil.randString(9));
		f.deleteOnExit();
		return f;
	}

	public static File getDir() {
		File f = new File(Common.Directories.tmp.getAbsolutePath() + File.separator + prefix + RandomUtil.randString(9));
		f.mkdirs();
		f.deleteOnExit();

		return f;
	}

	public static void clear() {
		for (File f : Common.Directories.tmp.listFiles()) {
			if (f.getName().startsWith(prefix)) {
				// delete it
				FileUtil.delete(f);
			}
		}
	}
}