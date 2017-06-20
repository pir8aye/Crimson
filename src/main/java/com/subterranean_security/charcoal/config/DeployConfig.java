package com.subterranean_security.charcoal.config;

public class DeployConfig extends Config {
	private String host;
	private int port;
	private String user;
	private String working_dir;
	private String platform;

	public String getWorking_dir() {
		return working_dir;
	}

	public void setWorking_dir(String working_dir) {
		this.working_dir = working_dir;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

}
