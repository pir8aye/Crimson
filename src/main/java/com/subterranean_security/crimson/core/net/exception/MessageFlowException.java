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
package com.subterranean_security.crimson.core.net.exception;

import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

/**
 * Thrown when the message flow is interrupted by an unexpected message. For
 * example, if a call to connector.writeAndGetResponse() returns an
 * EV_StreamData message when something else was expected, this exception is
 * thrown.
 */
public class MessageFlowException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * TODO: Get the type of message that was received.
	 * 
	 * @param sent
	 *            The message that was sent to cause the response
	 * @param received
	 *            The response
	 * @param expected
	 *            The expected response type
	 */
	public MessageFlowException(Class<?> sent, Message received, Class<?> expected) {
		super(String.format("After sending a %s message, a unknown message was received although a %s was expected.",
				sent.getClass().getSimpleName(), expected.getClass().getSimpleName()));
	}

	public MessageFlowException(Class<?> sent, Message received) {
		super(String.format("After sending a %s message, a unknown message was received.",
				sent.getClass().getSimpleName()));
	}

}
