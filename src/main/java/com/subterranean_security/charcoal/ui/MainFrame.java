package com.subterranean_security.charcoal.ui;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.subterranean_security.charcoal.Main;
import com.subterranean_security.charcoal.Tasker;
import com.subterranean_security.charcoal.config.Config;
import com.subterranean_security.charcoal.ui.components.IPanel;
import com.subterranean_security.charcoal.ui.layouts.TriLayout;
import com.subterranean_security.charcoal.ui.layouts.UniLayout;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	public static MainFrame main;

	private JMenu mnCrimsonSVC;
	private JMenu mnCrimsonClient;
	private JMenu mnViridian;
	private JMenu mnCloud;

	private JMenuItem mntmBuildCrimson;
	private JMenuItem mntmBuildViridian;
	private JMenuItem mntmBuildCloud;
	private JMenuItem mntmBuildCharcoal;
	private JMenuItem mntmBuildAndroidLibrary;

	public MainFrame() {
		init();
		initValues();

		main = this;
	}

	private void initValues() {

		// Deploy
		for (Config config : Main.getByTarget("DEPLOY_SVC")) {
			JMenuItem item = new JMenuItem(config.getName());
			item.addActionListener(e -> Tasker.launch(config));
			mnCrimsonSVC.add(item);
		}
		for (Config config : Main.getByTarget("DEPLOY_CLIENT")) {
			JMenuItem item = new JMenuItem(config.getName());
			item.addActionListener(e -> Tasker.launch(config));
			mnCrimsonClient.add(item);
		}
		for (Config config : Main.getByTarget("DEPLOY_VIRIDIAN")) {
			JMenuItem item = new JMenuItem(config.getName());
			item.addActionListener(e -> Tasker.launch(config));
			mnViridian.add(item);
		}
		for (Config config : Main.getByTarget("DEPLOY_CLOUD")) {
			JMenuItem item = new JMenuItem(config.getName());
			item.addActionListener(e -> Tasker.launch(config));
			mnCloud.add(item);
		}

		// Build
		for (Config config : Main.getByTarget("BUILD_CRIMSON")) {
			if (config.getShortcut() != null)
				mntmBuildCrimson.setText("(" + config.getShortcut() + ") " + mntmBuildCrimson.getText());
			mntmBuildCrimson.setEnabled(true);
			mntmBuildCrimson.addActionListener(e -> Tasker.launch(config));
			break;
		}
		for (Config config : Main.getByTarget("BUILD_VIRIDIAN")) {
			if (config.getShortcut() != null)
				mntmBuildViridian.setText("(" + config.getShortcut() + ") " + mntmBuildViridian.getText());
			mntmBuildViridian.setEnabled(true);
			mntmBuildViridian.addActionListener(e -> Tasker.launch(config));
			break;
		}
		for (Config config : Main.getByTarget("BUILD_CLOUD")) {
			if (config.getShortcut() != null)
				mntmBuildCloud.setText("(" + config.getShortcut() + ") " + mntmBuildCloud.getText());
			mntmBuildCloud.setEnabled(true);
			mntmBuildCloud.addActionListener(e -> Tasker.launch(config));
			break;
		}
		for (Config config : Main.getByTarget("BUILD_CHARCOAL")) {
			if (config.getShortcut() != null)
				mntmBuildCharcoal.setText("(" + config.getShortcut() + ") " + mntmBuildCharcoal.getText());
			mntmBuildCharcoal.setEnabled(true);
			mntmBuildCharcoal.addActionListener(e -> Tasker.launch(config));
			break;
		}
		for (Config config : Main.getByTarget("BUILD_ANDROID_LIBRARY")) {
			if (config.getShortcut() != null)
				mntmBuildAndroidLibrary.setText("(" + config.getShortcut() + ") " + mntmBuildAndroidLibrary.getText());
			mntmBuildAndroidLibrary.setEnabled(true);
			mntmBuildAndroidLibrary.addActionListener(e -> Tasker.launch(config));
			break;
		}

		// init key binding
		for (Config config : Main.configs) {
			if (config.getShortcut() != null) {
				cards.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(config.getShortcut()),
						config.getName());
				cards.getActionMap().put(config.getName(), new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						Tasker.launch(config);
					}
				});
			}
		}
	}

	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 684, 375);
		setTitle("Charcoal SDK");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnBuild = new JMenu("Build");
		menuBar.add(mnBuild);

		mntmBuildCrimson = new JMenuItem("Crimson");
		mntmBuildCrimson.setEnabled(false);
		mnBuild.add(mntmBuildCrimson);

		mntmBuildViridian = new JMenuItem("Viridian");
		mntmBuildViridian.setEnabled(false);
		mnBuild.add(mntmBuildViridian);

		mntmBuildCloud = new JMenuItem("Cloud");
		mntmBuildCloud.setEnabled(false);
		mnBuild.add(mntmBuildCloud);

		mntmBuildCharcoal = new JMenuItem("Charcoal");
		mntmBuildCharcoal.setEnabled(false);
		mnBuild.add(mntmBuildCharcoal);

		mntmBuildAndroidLibrary = new JMenuItem("Android Library");
		mntmBuildAndroidLibrary.setEnabled(false);
		mnBuild.add(mntmBuildAndroidLibrary);

		JMenu mnDeploy = new JMenu("Deploy");
		menuBar.add(mnDeploy);

		mnCrimsonSVC = new JMenu("Crimson SVC");
		mnCrimsonSVC.setEnabled(Main.hasTarget("DEPLOY_SVC"));
		mnDeploy.add(mnCrimsonSVC);

		mnCrimsonClient = new JMenu("Crimson Client");
		mnCrimsonClient.setEnabled(Main.hasTarget("DEPLOY_CLIENT"));
		mnDeploy.add(mnCrimsonClient);

		mnViridian = new JMenu("Viridian");
		mnViridian.setEnabled(Main.hasTarget("DEPLOY_VIRIDIAN"));
		mnDeploy.add(mnViridian);

		mnCloud = new JMenu("Cloud");
		mnCloud.setEnabled(Main.hasTarget("DEPLOY_CLOUD"));
		mnDeploy.add(mnCloud);

		JMenu mnTest = new JMenu("Test");
		menuBar.add(mnTest);

		cl = new CardLayout(0, 0);

		cards = new JPanel(cl);
		cards.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(cards);

		// add intro panel
		JPanel intro = new JPanel();
		cards.add(intro, "INTRO");
	}

	private JPanel cards;
	private TriLayout tri_panel;
	private UniLayout uni_panel;
	private CardLayout cl;
	public static List<IPanel> open_panels = new ArrayList<>();

	public void addSingle(IPanel pane) {
		open_panels.clear();
		if (uni_panel != null)
			cl.removeLayoutComponent(uni_panel);
		uni_panel = new UniLayout(pane);
		cards.add(uni_panel, "UNI");
		cl.show(cards, "UNI");
	}

	public void addTriple(IPanel pane1, IPanel pane2, IPanel pane3) {
		open_panels.clear();

		open_panels.add(pane1);
		open_panels.add(pane2);
		open_panels.add(pane3);

		if (tri_panel != null)
			cl.removeLayoutComponent(tri_panel);
		tri_panel = new TriLayout(pane1, pane2, pane3);
		cards.add(tri_panel, "TRI");
		cl.show(cards, "TRI");
	}

}
