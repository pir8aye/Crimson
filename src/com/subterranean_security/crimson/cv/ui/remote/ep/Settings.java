package com.subterranean_security.crimson.cv.ui.remote.ep;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.core.proto.Misc.GraphicsDisplay;
import com.subterranean_security.crimson.core.proto.Stream.RemoteParam;
import com.subterranean_security.crimson.core.proto.Stream.RemoteParam.RMethod;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Settings extends JPanel {

	private static final long serialVersionUID = 1L;

	private GraphicsDisplay[] displays;
	private String[] displayStrings;

	private String[] methodStrings = new String[] { "Simple Poll (slowest)", "Native Hook (fastest)" };
	private String[] colorStrings = new String[] { "ARGB", "RGB", "555 RGB", "Grayscale" };
	private String[] compStrings = new String[] { "None", "Low", "Medium", "High" };

	private RDPanel parent;

	private JComboBox monitorBox;
	private JComboBox methodBox;
	private JLabel lblCapture;

	private JComboBox colorBox;
	private JLabel lblCompression;

	private JComboBox compBox;

	public Settings(GraphicsDisplay[] displays, RDPanel parent) {
		this.displays = displays;
		this.parent = parent;
		loadSettings();
		init();
	}

	private void loadSettings() {
		displayStrings = new String[displays.length];
		for (int i = 0; i < displays.length; i++) {
			displayStrings[i] = "Monitor " + (i + 1) + " (" + displays[i].getWidth() + " x " + displays[i].getHeight()
					+ ")";
		}

		if (parent.stream != null) {
			switch (parent.stream.param.getRemoteParam().getRmethod()) {
			case NATIVE:
				methodBox.setSelectedIndex(1);
				break;
			case POLL:
				methodBox.setSelectedIndex(0);
				break;
			default:
				break;

			}
		}
	}

	public RMethod getMethod() {
		String m = (String) methodBox.getSelectedItem();
		if (m.equals(methodStrings[0])) {
			return RMethod.POLL;
		} else if (m.equals(methodStrings[1])) {
			return RMethod.NATIVE;
		} else {
			return null;
		}
	}

	public String getMonitor() {
		return getDisplay().getId();
	}

	public GraphicsDisplay getDisplay() {
		return displays[monitorBox.getSelectedIndex()];
	}

	public int getColorType() {
		String m = (String) colorBox.getSelectedItem();
		if (m.equals(colorStrings[0])) {
			return BufferedImage.TYPE_INT_ARGB;
		} else if (m.equals(methodStrings[1])) {
			return BufferedImage.TYPE_INT_RGB;
		} else if (m.equals(methodStrings[2])) {
			return BufferedImage.TYPE_USHORT_555_RGB;
		} else if (m.equals(methodStrings[3])) {
			return BufferedImage.TYPE_BYTE_GRAY;
		} else {
			return BufferedImage.TYPE_INT_ARGB;
		}
	}

	public float getCompType() {
		String m = (String) colorBox.getSelectedItem();
		if (m.equals(colorStrings[0])) {
			return -1f;
		} else if (m.equals(methodStrings[1])) {
			return 1.0f;
		} else if (m.equals(methodStrings[2])) {
			return 0.5f;
		} else if (m.equals(methodStrings[3])) {
			return 0.0f;
		} else {
			return -1f;
		}
	}

	private void resetStream() {
		if (parent.stream == null) {
			return;
		}
		StreamStore.removeStream(parent.stream.getStreamID());
		parent.running = false;
		parent.start();

	}

	private void init() {
		setLayout(null);

		monitorBox = new JComboBox();
		monitorBox.setFont(new Font("Dialog", Font.BOLD, 10));
		monitorBox.setModel(new DefaultComboBoxModel(displayStrings));
		monitorBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					setAllEnabled(false);
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							resetStream();
							return null;
						}

						protected void done() {
							setAllEnabled(true);
						};

					}.execute();

				}
			}
		});

		monitorBox.setBounds(130, 7, 164, 20);
		add(monitorBox);

		methodBox = new JComboBox();
		methodBox.setModel(new DefaultComboBoxModel(methodStrings));
		methodBox.setFont(new Font("Dialog", Font.BOLD, 10));
		methodBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					setAllEnabled(false);
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							resetStream();
							return null;
						}

						protected void done() {
							setAllEnabled(true);
						};

					}.execute();

				}
			}
		});
		methodBox.setBounds(130, 31, 164, 20);
		add(methodBox);

		JLabel lblCaptureDevice = new JLabel("Capture Device:");
		lblCaptureDevice.setIcon(UIUtil.getIcon("icons16/general/viewer.png"));
		lblCaptureDevice.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCaptureDevice.setBounds(12, 7, 117, 20);
		add(lblCaptureDevice);

		lblCapture = new JLabel("Capture Mode:");
		lblCapture.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblCapture.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCapture.setBounds(12, 31, 117, 20);
		add(lblCapture);

		JLabel lblColorQuality = new JLabel("Color Mode:");
		lblColorQuality.setIcon(UIUtil.getIcon("icons16/general/palette.png"));
		lblColorQuality.setFont(new Font("Dialog", Font.BOLD, 10));
		lblColorQuality.setBounds(12, 55, 117, 20);
		add(lblColorQuality);

		colorBox = new JComboBox();
		colorBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					setAllEnabled(false);
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							resetStream();
							return null;
						}

						protected void done() {
							setAllEnabled(true);
						};

					}.execute();

				}
			}
		});
		colorBox.setFont(new Font("Dialog", Font.BOLD, 10));
		colorBox.setModel(new DefaultComboBoxModel(colorStrings));
		colorBox.setBounds(130, 56, 164, 20);
		add(colorBox);

		lblCompression = new JLabel("Compression:");
		lblCompression.setIcon(UIUtil.getIcon("icons16/general/compress.png"));
		lblCompression.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCompression.setBounds(12, 79, 117, 20);
		add(lblCompression);

		compBox = new JComboBox();
		compBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					setAllEnabled(false);
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							resetStream();
							return null;
						}

						protected void done() {
							setAllEnabled(true);
						};

					}.execute();

				}
			}
		});
		compBox.setFont(new Font("Dialog", Font.BOLD, 10));
		compBox.setModel(new DefaultComboBoxModel(compStrings));
		compBox.setBounds(130, 79, 164, 20);
		add(compBox);

	}

	private void setAllEnabled(boolean b) {
		methodBox.setEnabled(b);
		monitorBox.setEnabled(b);
		colorBox.setEnabled(b);
		compBox.setEnabled(b);
	}
}
