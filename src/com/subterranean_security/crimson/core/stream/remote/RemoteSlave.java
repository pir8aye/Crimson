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
package com.subterranean_security.crimson.core.stream.remote;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.subterranean_security.crimson.client.ClientStore;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.CoreStore;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.DirtyRect;
import com.subterranean_security.crimson.core.proto.Stream.EV_StreamData;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.proto.Stream.ScreenData;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.util.Native;

public class RemoteSlave extends Stream {

	private Robot robot;
	private Thread poller = new Thread(new Runnable() {
		private BufferedImage last;

		public void run() {
			while (!Thread.interrupted()) {
				BufferedImage image = robot.createScreenCapture(screenRect);

				ScreenData.Builder data = ScreenData.newBuilder();
				if (last == null) {
					DirtyRect.Builder dr = DirtyRect.newBuilder().setSx(0).setSy(0).setH(image.getHeight())
							.setW(image.getWidth());
					for (int j = 0; j < image.getHeight(); j++) {
						for (int i = 0; i < image.getWidth(); i++) {
							dr.addRGBA(image.getRGB(i, j));
						}
					}
					data.addDirtyRect(dr);

				} else {
					// compare with last
					boolean[][] toggleMatrix = new boolean[image.getHeight()][image.getWidth()];
					for (int j = 0; j < image.getHeight(); j++) {
						for (int i = 0; i < image.getWidth(); i++) {
							if (!toggleMatrix[i][j] && image.getRGB(i, j) != last.getRGB(i, j)) {

								int w = 1;
								int h = 1;

								while (expandRect(image, toggleMatrix, h, w, i, j)) {
									w++;
									h++;
								}

								DirtyRect.Builder dr = DirtyRect.newBuilder().setSx(i).setSy(j).setW(w).setH(h);

								for (int jh = 0; jh < h; jh++) {
									for (int iw = 0; iw < w; iw++) {
										dr.addRGBA(image.getRGB(i + iw, j + jh));
									}

								}

								data.addDirtyRect(dr);

							}
						}
					}
				}

				last = image;
				uQueue.add(data.build());
				// TODO
				try {
					Thread.sleep(param.getPeriod());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		private boolean expandRect(BufferedImage image, boolean[][] toggleMatrix, int h, int w, int i, int j) {
			try {
				for (int k = 0; k <= h; k++) {
					int ni = i + w + 1;
					int nj = j + k;
					if (image.getRGB(ni, nj) != last.getRGB(ni, nj)) {
						toggleMatrix[ni][nj] = true;
						continue;
					} else {
						return false;
					}

				}
				for (int k = 0; k <= w; k++) {
					int ni = i + k;
					int nj = j + w + 1;
					if (image.getRGB(ni, nj) != last.getRGB(ni, nj)) {
						toggleMatrix[ni][nj] = true;
						continue;
					} else {
						return false;
					}

				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
			return true;
		}
	});

	private Rectangle screenRect;

	public RemoteSlave(Param p) {
		param = p;

		CoreStore.Remote.setSlave(this);

		try {
			for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				if (gd.getIDstring().equals(param.getRemoteParam().getMonitor())) {
					screenRect = gd.getDefaultConfiguration().getBounds();
					robot = new Robot();
					break;
				}
			}

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void received(Message m) {

		if (m.getEvStreamData().getEventData().hasKeyPressed()) {
			robot.keyPress(m.getEvStreamData().getEventData().getKeyPressed());
		} else if (m.getEvStreamData().getEventData().hasKeyReleased()) {
			robot.keyRelease(m.getEvStreamData().getEventData().getKeyReleased());
		}
	}

	@Override
	public void start() {
		switch (param.getRemoteParam().getRmethod()) {
		case NATIVE:
			Native.startRD();
			break;
		case POLL:
			poller.start();
			break;
		default:
			break;

		}

		timer.schedule(sendTask, 0, param.hasPeriod() ? param.getPeriod() : 50);

	}

	@Override
	public void stop() {
		timer.cancel();

		switch (param.getRemoteParam().getRmethod()) {
		case NATIVE:
			// TODO
			break;
		case POLL:
			poller.interrupt();
			break;
		default:
			break;

		}

	}

	private LinkedList<ScreenData> uQueue = new LinkedList<ScreenData>();

	@Override
	public void send() {

		if (uQueue.size() != 0) {
			ClientStore.Connections.route(
					Message.newBuilder().setUrgent(true).setSid(Common.cvid).setRid(param.getVID()).setEvStreamData(
							EV_StreamData.newBuilder().setStreamID(getStreamID()).setScreenData(uQueue.poll())));
		}

	}

	public void addFrame(ScreenData sd) {
		uQueue.add(sd);
	}

}
