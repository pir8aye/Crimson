package com.subterranean_security.charcoal;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.subterranean_security.charcoal.config.BuildConfig;
import com.subterranean_security.charcoal.config.Config;
import com.subterranean_security.charcoal.config.DeployConfig;
import com.subterranean_security.charcoal.ui.MainFrame;
import com.subterranean_security.crimson.core.util.LogUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.ListenerConfig;
import com.subterranean_security.crimson.server.ShutdownHook;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.universal.Universal;

public class Main {

	public static List<Config> configs;

	public static final File build_output = new File(System.getProperty("user.home") + "/.charcoal_output");
	public static final File script_dir = new File(Universal.jar.getParent() + "/modules");

	public static void main(String[] argv) {
		LogUtil.configure();

		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		try {
			readConfig();
		} catch (FileNotFoundException | YamlException e1) {
			e1.printStackTrace();
			return;
		}

		// create output directory
		build_output.mkdirs();

		// copy scripts
		ZipUtil.unpack(Universal.jar, script_dir, new NameMapper() {
			public String map(String name) {
				return name.startsWith("com/subterranean_security/charcoal/modules")
						? name.substring(name.indexOf("modules/") + 8) : null;
			}
		});

		// start listeners
		ListenerStore.load();
		ListenerStore.add(ListenerConfig.newBuilder().setPort(10100).setId(0).setOwner("").build());
		ListenerStore.startAll();

		// start interface
		EventQueue.invokeLater(() -> {
			try {
				MainFrame frame = new MainFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static void readConfig() throws FileNotFoundException, YamlException {
		configs = new ArrayList<>();

		YamlReader yml = new YamlReader(new FileReader("/home/cilki/config.yaml"));
		yml.getConfig().setClassTag("DeployConfig", DeployConfig.class);
		yml.getConfig().setClassTag("BuildConfig", BuildConfig.class);
		configs = (List<Config>) ((Map) yml.read()).get("config");
	}

	public static boolean hasTarget(String target) {
		for (Config c : configs)
			if (c.getTarget().equals(target))
				return true;
		return false;
	}

	public static List<Config> getByTarget(String target) {
		List<Config> a = new ArrayList<>();
		for (Config c : configs)
			if (c.getTarget().equals(target))
				a.add(c);
		return a;
	}

	public static Config getByTargetSingle(String target) {
		return getByTarget(target).get(0);
	}

	public static Config getByName(String name) {
		for (Config c : configs)
			if (c.getName().equals(name))
				return c;
		return null;
	}

	public static Config getByShortcut(char shortcut) {
		for (Config c : configs)
			if (c.getShortcut() != null && c.getShortcut().toCharArray()[0] == shortcut)
				return c;
		return null;
	}

}
