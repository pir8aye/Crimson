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
package com.subterranean_security.crimson.client.net;

import static com.subterranean_security.crimson.universal.Flags.LOG_NET;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import javax.imageio.ImageIO;
import javax.security.auth.DestroyFailedException;

import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.client.modules.Keylogger;
import com.subterranean_security.crimson.client.modules.Power;
import com.subterranean_security.crimson.client.modules.QuickScreenshot;
import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.misc.HCP;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.Config;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.executor.BasicExecutor;
import com.subterranean_security.crimson.core.platform.LocalFS;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.store.FileManagerStore;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;
import com.subterranean_security.crimson.core.stream.remote.RemoteSlave;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.TempUtil;
import com.subterranean_security.crimson.proto.core.Generator.ClientConfig;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.MI_GroupChallengeResult;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientControl.RS_ChangeSetting;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_Delete;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileHandle;
import com.subterranean_security.crimson.proto.core.net.sequences.FileManager.RS_FileListing;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.State;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogFile;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.RS_Logs;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Network.RQ_MakeDirectConnection;
import com.subterranean_security.crimson.proto.core.net.sequences.Screenshot.RS_QuickScreenshot;
import com.subterranean_security.crimson.proto.core.net.sequences.State.RS_ChangeClientState;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;
import com.subterranean_security.crimson.proto.core.net.sequences.Update.RS_GetClientConfig;
import com.subterranean_security.crimson.sc.Logsystem;

import io.netty.util.ReferenceCountUtil;

public class ClientExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ClientExecutor.class);

	public ClientExecutor() {
		super();

	}

}
