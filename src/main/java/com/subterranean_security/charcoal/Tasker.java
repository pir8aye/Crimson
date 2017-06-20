package com.subterranean_security.charcoal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.charcoal.config.BuildConfig;
import com.subterranean_security.charcoal.config.Config;
import com.subterranean_security.charcoal.config.DeployConfig;
import com.subterranean_security.charcoal.ui.MainFrame;
import com.subterranean_security.charcoal.ui.components.IPanel;
import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.universal.util.JarUtil;

public final class Tasker {
	private Tasker() {
	}

	public static final Logger log = LoggerFactory.getLogger(Tasker.class);

	private static Config config;
	private static IPanel logPane;
	private static Thread task;

	public static void launch(Config launchConfig) {
		if (task != null) {
			log.debug("Thread: {} will be terminated", task.getName());
			task.interrupt();
			task = null;
		}

		config = launchConfig;
		logPane = new IPanel();
		MainFrame.main.addSingle(logPane);

		task = new Thread(new Runnable() {
			public void run() {
				switch (config.getTarget()) {
				case "BUILD_CRIMSON":
				case "BUILD_VIRIDIAN":
				case "BUILD_CLOUD":
				case "BUILD_CHARCOAL":
					build();
					break;
				case "DEPLOY_SVC":
					deploy_svc();
					break;
				case "DEPLOY_CLIENT":
					deploy_client();
					break;
				case "DEPLOY_VIRIDIAN":
					deploy_viridian();
					break;
				case "DEPLOY_CLOUD":
					deploy_cloud();
					break;
				}
				task = null;
			}
		});
		task.setName(config.getName());
		task.start();
	}

	private static void build() {
		BuildConfig bc = (BuildConfig) config;

		run(new ProcessBuilder().command("ruby",
				Main.script_dir.getAbsolutePath() + "/build/" + bc.getTarget().toLowerCase() + ".rb",
				bc.getProject_dir(), Main.build_output.getAbsolutePath()));
	}

	private static void deploy_svc() {
		DeployConfig dc = (DeployConfig) config;

		if (run(new ProcessBuilder().command("ruby",
				Main.script_dir.getAbsolutePath() + "/deploy/" + dc.getTarget().toLowerCase() + "_"
						+ dc.getPlatform().toLowerCase() + ".rb",
				dc.getHost(), "" + dc.getPort(), dc.getUser(), dc.getWorking_dir(),
				Main.build_output.getAbsolutePath())) == 0) {
			MainFrame.main.addTriple(new IPanel(Instance.SERVER), new IPanel(Instance.VIEWER),
					new IPanel(Instance.CLIENT));
		}
	}

	private static void deploy_client() {
		DeployConfig dc = (DeployConfig) config;

		if (run(new ProcessBuilder().command("ruby",
				Main.script_dir.getAbsolutePath() + "/deploy/" + dc.getTarget().toLowerCase() + "_"
						+ dc.getPlatform().toLowerCase() + ".rb",
				dc.getHost(), "" + dc.getPort(), dc.getUser(), dc.getWorking_dir(),
				Main.build_output.getAbsolutePath())) == 0) {
			MainFrame.main.addSingle(new IPanel(Instance.CLIENT));
		}
	}

	private static void deploy_cloud() {
		DeployConfig dc = (DeployConfig) config;

		String key = null;
		try {
			key = new String(JarUtil.readResource("/com/subterranean_security/charcoal/res/pem/CLOUD.pem"));
		} catch (IOException e) {
			log.error("Failed to read private key");
			e.printStackTrace();
			return;
		}
		if (run(new ProcessBuilder().command("ruby",
				Main.script_dir.getAbsolutePath() + "/deploy/" + dc.getTarget().toLowerCase() + ".rb", dc.getHost(),
				"" + dc.getPort(), dc.getUser(), dc.getWorking_dir(), Main.build_output.getAbsolutePath(),
				dc.getProject_dir(), key)) == 0) {
			MainFrame.main.addSingle(new IPanel(Instance.SERVER));
		}
	}

	private static void deploy_viridian() {
		DeployConfig dc = (DeployConfig) config;

		String key = null;
		try {
			key = new String(JarUtil.readResource("/com/subterranean_security/charcoal/res/pem/VIRIDIAN.pem"));
		} catch (IOException e) {
			log.error("Failed to read private key");
			e.printStackTrace();
			return;
		}
		if (run(new ProcessBuilder().command("ruby",
				Main.script_dir.getAbsolutePath() + "/deploy/" + dc.getTarget().toLowerCase() + ".rb", dc.getHost(),
				"" + dc.getPort(), dc.getUser(), dc.getWorking_dir(), Main.build_output.getAbsolutePath(),
				dc.getProject_dir(), key)) == 0) {
			MainFrame.main.addSingle(new IPanel(Instance.VIRIDIAN));
		}
	}

	private static int run(ProcessBuilder pb) {
		pb.redirectErrorStream(true);
		// List<String> cmd = pb.command();
		// System.out.println("Launching: " + cmd.get(0));
		// for (int i = 1; i < cmd.size(); i++) {
		// System.out.println("\t" + cmd.get(i));
		// }

		try {
			Process process = pb.start();

			try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line = null;
				while ((line = input.readLine()) != null) {
					logPane.addLine(line + "\n");
				}
			}

			process.destroyForcibly();

			return process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 1;
		}
	}

}
