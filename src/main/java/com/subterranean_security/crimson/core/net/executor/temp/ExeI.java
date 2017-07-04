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

	default public void mi_auth_request(Message m) {
	}

	default public void mi_challenge_result(Message m) {
	}

	default public void mi_close_file_handle(Message m) {
	}

	default public void mi_debug_kill(Message m) {
	}

	default public void mi_report(Message m) {
	}

	default public void mi_stream_start(Message m) {
	}

	default public void mi_stream_stop(Message m) {
	}

	default public void mi_trigger_profile_delta(Message m) {
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

	default public void rq_create_auth_method(Message m) {
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

	default public void rq_group_challenge(Message m) {
	}

	default public void rq_key_update(Message m) {
	}

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
	
	default public void rq_remove_auth_method(Message m) {
	}
	
	default public void rq_remove_listener(Message m) {
	}
	
	default public void rq_server_info(Message m) {
	}

}
