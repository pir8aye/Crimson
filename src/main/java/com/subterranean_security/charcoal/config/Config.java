package com.subterranean_security.charcoal.config;

public abstract class Config {
	protected String name;
	protected String target;
	protected String shortcut;
	private String project_dir;

	public String getProject_dir() {
		return project_dir;
	}

	public void setProject_dir(String project_dir) {
		this.project_dir = project_dir;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getShortcut() {
		return shortcut;
	}

	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}

}
