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
package com.subterranean_security.crimson.core.net.executor;

import static com.subterranean_security.crimson.universal.Flags.LOG_NET;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.factory.ExecutorFactory;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.universal.Universal;

import io.netty.util.ReferenceCountUtil;

/**
 * @author cilki
 * @since 4.0.0
 */
public abstract class BasicExecutor {

	private static final Logger log = LoggerFactory.getLogger(BasicExecutor.class);

	protected Thread dispatchThread = new Thread(new Runnable() {
		public void run() {
			while (!Thread.interrupted()) {
				Message m;
				try {
					m = connector.msgQueue.take();
				} catch (InterruptedException e) {
					return;
				}
				pool.submit(() -> {
					if (LOG_NET)
						log.debug("INCOMING\n{}/INCOMING", m.toString());
					execute(m);
				});
			}
		}
	});

	protected ExecutorService pool;

	// TODO move to abstract type for Exe's
	protected Connector connector;

	private List<ExeI> executors;

	public BasicExecutor() {
		pool = Executors.newCachedThreadPool();

	}

	public void stop() {
		if (dispatchThread != null) {
			dispatchThread.interrupt();
			dispatchThread = null;
		}

		try {
			pool.shutdown();
			pool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		} finally {
			pool.shutdownNow();
			pool = null;
		}

	}

	public void start() {
		dispatchThread.start();
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	public static BasicExecutor getInstanceExecutor() {

		switch (Universal.instance) {
		case SERVER:
			try {
				return new ExecutorFactory("com.subterranean_security.crimson.server.net.ServerExecutor").build();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		case VIRIDIAN:
			try {
				return new ExecutorFactory("com.subterranean_security.viridian.net.ViridianExecutor").build();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		case CHARCOAL:
			try {
				return new ExecutorFactory("com.subterranean_security.charcoal.net.CharcoalExecutor").build();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		default:
			return null;

		}
	}

	public void execute(Message m) {
		switch (m.getMsgOneofCase()) {
		case EV_CHAT_MESSAGE:
			for (ExeI exe : executors)
				exe.ev_chat_message(m);
			break;
		case EV_DEBUG_LOG_EVENT:
			for (ExeI exe : executors)
				exe.ev_debug_log_event(m);
			break;
		case EV_ENDPOINT_CLOSED:
			for (ExeI exe : executors)
				exe.ev_endpoint_closed(m);
			break;
		case EV_KEVENT:
			for (ExeI exe : executors)
				exe.ev_kevent(m);
			break;
		case EV_NETWORK_DELTA:
			for (ExeI exe : executors)
				exe.ev_network_delta(m);
			break;
		case EV_PROFILE_DELTA:
			for (ExeI exe : executors)
				exe.ev_profile_delta(m);
			break;
		case EV_STREAM_DATA:
			for (ExeI exe : executors)
				exe.ev_stream_data(m);
			break;
		case MI_AUTH_REQUEST:
			for (ExeI exe : executors)
				exe.mi_auth_request(m);
			break;
		case MI_CHALLENGE_RESULT:
			for (ExeI exe : executors)
				exe.mi_challenge_result(m);
			break;
		case MI_CLOSE_FILE_HANDLE:
			for (ExeI exe : executors)
				exe.mi_close_file_handle(m);
			break;
		case MI_DEBUG_KILL:
			for (ExeI exe : executors)
				exe.mi_debug_kill(m);
			break;
		case MI_REPORT:
			for (ExeI exe : executors)
				exe.mi_report(m);
			break;
		case MI_STREAM_START:
			for (ExeI exe : executors)
				exe.mi_stream_start(m);
			break;
		case MI_STREAM_STOP:
			for (ExeI exe : executors)
				exe.mi_stream_stop(m);
			break;
		case MI_TRIGGER_PROFILE_DELTA:
			for (ExeI exe : executors)
				exe.mi_trigger_profile_delta(m);
			break;
		case RQ_ADD_LISTENER:
			for (ExeI exe : executors)
				exe.rq_add_listener(m);
			break;
		case RQ_ADD_TORRENT:
			for (ExeI exe : executors)
				exe.rq_add_torrent(m);
			break;
		case RQ_ADD_USER:
			for (ExeI exe : executors)
				exe.rq_add_user(m);
			break;
		case RQ_ADVANCED_FILE_INFO:
			for (ExeI exe : executors)
				exe.rq_advanced_file_info(m);
			break;
		case RQ_CHANGE_CLIENT_STATE:
			for (ExeI exe : executors)
				exe.rq_change_client_state(m);
			break;
		case RQ_CHANGE_SERVER_STATE:
			for (ExeI exe : executors)
				exe.rq_change_server_state(m);
			break;
		case RQ_CHANGE_SETTING:
			for (ExeI exe : executors)
				exe.rq_change_setting(m);
			break;
		case RQ_CHAT:
			for (ExeI exe : executors)
				exe.rq_chat(m);
			break;
		case RQ_CREATE_AUTH_METHOD:
			for (ExeI exe : executors)
				exe.rq_create_auth_method(m);
			break;
		case RQ_CVID:
			for (ExeI exe : executors)
				exe.rq_cvid(m);
			break;
		case RQ_DEBUG_SESSION:
			for (ExeI exe : executors)
				exe.rq_debug_session(m);
			break;
		case RQ_DELETE:
			for (ExeI exe : executors)
				exe.rq_delete(m);
			break;
		case RQ_DIRECT_CONNECTION:
			for (ExeI exe : executors)
				exe.rq_direct_connection(m);
			break;
		case RQ_EDIT_USER:
			for (ExeI exe : executors)
				exe.rq_edit_user(m);
			break;
		case RQ_FILE_HANDLE:
			for (ExeI exe : executors)
				exe.rq_file_handle(m);
			break;
		case RQ_FILE_LISTING:
			for (ExeI exe : executors)
				exe.rq_file_listing(m);
			break;
		case RQ_GENERATE:
			for (ExeI exe : executors)
				exe.rq_generate(m);
			break;
		case RQ_GET_CLIENT_CONFIG:
			for (ExeI exe : executors)
				exe.rq_get_client_config(m);
			break;
		case RQ_GROUP_CHALLENGE:
			for (ExeI exe : executors)
				exe.rq_group_challenge(m);
			break;
		case RQ_KEY_UPDATE:
			for (ExeI exe : executors)
				exe.rq_key_update(m);
			break;
		case RQ_LOGIN:
			for (ExeI exe : executors)
				exe.rq_login(m);
			break;
		case RQ_LOGIN_CHALLENGE:
			for (ExeI exe : executors)
				exe.rq_login_challenge(m);
			break;
		case RQ_LOGS:
			for (ExeI exe : executors)
				exe.rq_logs(m);
			break;
		case RQ_MAKE_DIRECT_CONNECTION:
			for (ExeI exe : executors)
				exe.rq_make_direct_connection(m);
			break;
		case RQ_PING:
			for (ExeI exe : executors)
				exe.rq_ping(m);
			break;
		case RQ_QUICK_SCREENSHOT:
			for (ExeI exe : executors)
				exe.rq_quick_screenshot(m);
			break;
		case RQ_REMOVE_AUTH_METHOD:
			for (ExeI exe : executors)
				exe.rq_remove_auth_method(m);
			break;
		case RQ_REMOVE_LISTENER:
			for (ExeI exe : executors)
				exe.rq_remove_listener(m);
			break;
		case RQ_SERVER_INFO:
			for (ExeI exe : executors)
				exe.rq_server_info(m);
			break;
		default:
			connector.addNewResponse(m);
			break;
		}

		ReferenceCountUtil.release(m);
	}

}
