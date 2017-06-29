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
	 * Respond to a request for a cvid. If the server is not familiar with one of
	 * the supplied long-cvids, respond with a new lcvid-cvid pair.
	 * 
	 * @param m
	 */
	default public void rq_cvid(Message m) {
	}
}
