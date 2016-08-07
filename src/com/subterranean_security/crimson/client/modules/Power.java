package com.subterranean_security.crimson.client.modules;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.Platform.OSFAMILY;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.core.util.CUtil;

public final class Power {

	private Power() {
	}

	public static Outcome shutdown() {

		String command = "";
		switch (Platform.osFamily) {
		case BSD:
		case LIN:
		case OSX:
		case SOL:
			command = "poweroff";
			break;
		case WIN:
			command = "shutdown /s /p";

			break;
		default:
			break;
		}

		try {
			return Outcome.newBuilder().setResult(CUtil.Misc.runBackgroundCommand(command, 1)).build();
		} catch (IOException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
	}

	public static Outcome restart() {

		String command = "";
		switch (Platform.osFamily) {
		case BSD:
		case LIN:
		case OSX:
		case SOL:
			command = "restart";
			break;
		case WIN:
			command = "shutdown /r /p";
			break;
		default:
			break;
		}

		try {
			return Outcome.newBuilder().setResult(CUtil.Misc.runBackgroundCommand(command, 1)).build();
		} catch (IOException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
	}

	public static Outcome hibernate() {

		String command = "";
		switch (Platform.osFamily) {
		case BSD:
		case LIN:
		case OSX:
		case SOL:
			return Outcome.newBuilder().setResult(false).setComment("Hibernate is unsupported on this platform")
					.build();
		case WIN:
			command = "shutdown /h /p";
			break;
		default:
			break;
		}

		try {
			return Outcome.newBuilder().setResult(CUtil.Misc.runBackgroundCommand(command, 1)).build();
		} catch (IOException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
	}

	public static Outcome standby() {

		String command = "";
		switch (Platform.osFamily) {
		case BSD:
		case LIN:
		case OSX:
		case SOL:
		case WIN:
			return Outcome.newBuilder().setResult(false).setComment("Standby is unsupported on this platform").build();
		default:
			break;
		}

		try {
			return Outcome.newBuilder().setResult(CUtil.Misc.runBackgroundCommand(command, 1)).build();
		} catch (IOException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
	}

	public static Outcome uninstall() {
		String command = "";
		switch (Platform.osFamily) {
		case BSD:
			command = "rmdir /S /Q" + Client.ic.getPathBsd();
			break;
		case LIN:
			command = "rmdir /S /Q" + Client.ic.getPathLin();
			break;
		case OSX:
			command = "rmdir /S /Q" + Client.ic.getPathOsx();
			break;
		case SOL:
			command = "rmdir /S /Q" + Client.ic.getPathSol();
			break;
		case WIN:
			command = "rmdir /S /Q" + Client.ic.getPathWin();
			// TODO append reg key deletion
			// reg delete HKCU\Software\Microsoft\Windows\CurrentVersion\Run /v
			// client.jar /f
			break;
		default:
			break;
		}

		try {
			return Outcome.newBuilder().setResult(CUtil.Misc.runBackgroundCommand(command, 1)).build();
		} catch (IOException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
	}

	public static Outcome restartProcess() {
		try {
			return Outcome.newBuilder()
					.setResult(CUtil.Misc.runBackgroundCommand((Platform.osFamily == OSFAMILY.WIN) ? "javaw -jar "
							: "java -jar " + new File(
									Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
											.getAbsolutePath(),
							1))
					.build();
		} catch (IOException | URISyntaxException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
	}

}
