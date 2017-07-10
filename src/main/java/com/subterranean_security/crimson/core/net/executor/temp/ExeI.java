/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.core.net.executor.temp;

import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

/**
 * @author cilki
 * @since 5.0.0
 */
public interface ExeI {
	default public void ev_chat_message(Message m) {
	}

	default public void ev_debug_log_event(Message m) {
	}

	default public void ev_endpoint_closed(Message m) {
	}

	default public void ev_kevent(Message m) {
	}

	default public void ev_network_delta(Message m) {
	}

	default public void ev_profile_delta(Message m) {
	}

	default public void ev_stream_data(Message m) {
	}

	/**
	 * <pre>
	 * {@code
	 * 
	 * KEY AUTHENTICATION
	 * [Server]                                                        [Client]
	 * |  <-  M1_AuthAttempt      [group name]                                |
	 * |  ->  RQ_KeyChallenge     [nonce]                                     |
	 * |  <-  RS_KeyChallenge     [signed nonce with public key]              |
	 * |  ->  M1_ChallengeResult                                              |
	 * |  <-  RQ_KeyChallenge     [nonce]                                     |
	 * |  ->  RS_KeyChallenge     [signed nonce with private key]             |
	 * |  <-  M1_ChallengeResult                                              |
	 * 
	 * }
	 * </pre>
	 * 
	 * @param m
	 */
	default public void m1_auth_attempt(Message m) {
	}

	default public void m1_challenge_result(Message m) {
	}

	default public void m1_close_file_handle(Message m) {
	}

	default public void m1_debug_kill(Message m) {
	}

	default public void m1_report(Message m) {
	}

	default public void m1_stream_start(Message m) {
	}

	default public void m1_stream_stop(Message m) {
	}

	default public void m1_trigger_profile_delta(Message m) {
	}

	default public void rq_add_listener(Message m) {
	}

	default public void rq_add_torrent(Message m) {
	}

	default public void rq_add_user(Message m) {
	}

	default public void rq_advanced_file_info(Message m) {
	}

	default public void rq_change_client_state(Message m) {
	}

	default public void rq_change_server_state(Message m) {
	}

	default public void rq_change_setting(Message m) {
	}

	default public void rq_chat(Message m) {
	}

	default public void rq_create_auth_group(Message m) {
	}

	/**
	 * Respond to a request for a cvid. If the server is not familiar with one of
	 * the supplied long-cvids, respond with a new lcvid-cvid pair.
	 * 
	 * @param m
	 */
	default public void rq_cvid(Message m) {
	}

	default public void rq_debug_session(Message m) {
	}

	default public void rq_delete(Message m) {
	}

	default public void rq_direct_connection(Message m) {
	}

	default public void rq_edit_user(Message m) {
	}

	default public void rq_file_handle(Message m) {
	}

	default public void rq_file_listing(Message m) {
	}

	default public void rq_generate(Message m) {
	}

	default public void rq_get_client_config(Message m) {
	}

	default public void rq_key_challenge(Message m) {
	}

	default public void rq_key_update(Message m) {
	}

	/**
	 * <pre>
	 * {@code
	 * [Server]                                                        [Viewer]
	 * |  <-  RQ_Login           [username]                                   |
	 * |  ->  RQ_LoginChallenge  [salt]                                       |
	 * |  <-  RS_LoginChallenge  [password + salt hash]                       |
	 * |  ->  RS_Login           [result, profile updates]                    |
	 * 
	 * }
	 * </pre>
	 * 
	 * @param m
	 */
	default public void rq_login(Message m) {
	}

	default public void rq_login_challenge(Message m) {
	}

	default public void rq_logs(Message m) {
	}

	default public void rq_make_direct_connection(Message m) {
	}

	default public void rq_ping(Message m) {
	}

	default public void rq_quick_screenshot(Message m) {
	}

	default public void rq_remove_auth_group(Message m) {
	}

	default public void rq_remove_listener(Message m) {
	}

	default public void rq_server_info(Message m) {
	}

}
