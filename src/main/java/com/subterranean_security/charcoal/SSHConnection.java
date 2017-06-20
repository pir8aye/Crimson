package com.subterranean_security.charcoal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SSHConnection {

	private JSch jsch;
	private Session connection;

	private String user;
	private String host;
	private int port;
	private String pass;

	public SSHConnection(String host, int port, String user, String password) {
		jsch = new JSch();
		// jsch.addIdentity(prvkey);

		this.user = user;
		this.pass = password;
		this.host = host;
	}

	public SSHConnection(String host, int port, String user, InputStream privateKey) throws JSchException {
		jsch = new JSch();

		try (Scanner sc = new Scanner(privateKey)) {
			jsch.addIdentity(sc.useDelimiter("\\A").next());
		}

		this.user = user;
		this.host = host;
	}

	public void connect(int timeout) throws JSchException {

		connection = jsch.getSession(user, host, port);
		if (pass != null)
			connection.setPassword(pass);

		connection.setConfig("StrictHostKeyChecking", "no");
		connection.connect(timeout);
	}

	public void upload(File src, File dest) throws FileNotFoundException, SftpException, JSchException {
		ChannelSftp channel = (ChannelSftp) connection.openChannel("sftp");
		channel.connect();
		channel.cd(dest.getParent());

		channel.put(new FileInputStream(src), dest.getName());

		channel.disconnect();
	}

	public void download(File src, File dest) throws FileNotFoundException, SftpException, JSchException {
		ChannelSftp channel = (ChannelSftp) connection.openChannel("sftp");
		channel.connect();
		channel.cd(src.getParent());

		channel.get(src.getName(), new FileOutputStream(dest));

		channel.disconnect();
	}

	public void run(String cmd, CmdHandler handler) {

		try {
			ChannelExec channel = (ChannelExec) connection.openChannel("exec");
			channel.setCommand(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			channel.connect();

			String line = null;
			while ((line = br.readLine()) != null) {
				handler.processLine(line);
			}

			handler.done();
			channel.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}

	}

	public void close() {
		connection.disconnect();
	}

	public interface CmdHandler {
		public void processLine(String line);

		public void done();
	}

}