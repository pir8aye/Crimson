package com.subterranean_security.crimson.test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.subterranean_security.crimson.core.storage.BasicDatabase;
import com.subterranean_security.crimson.core.util.Native;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;

public class TestUtil {
	static {
		Native.Loader.loadJDBCTemporarily(new File("lib"));
	}

	public static BasicDatabase getDatabase() {
		BasicDatabase database = new BasicDatabase(
				new File(System.getProperty("java.io.tmpdir") + "/" + RandomUtil.randString(5) + ".db"));
		try {
			database.initialize();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return database;
	}

	public static void setupDatabaseStore() {
		DatabaseStore.setFacility(getDatabase());
	}

	public static BasicDatabase saveDatabase(BasicDatabase database) {
		database.close();
		database = new BasicDatabase(database.getFile());
		try {
			database.initialize();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return database;
	}
}
