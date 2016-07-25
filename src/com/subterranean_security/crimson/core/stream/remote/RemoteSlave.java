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
import java.util.Date;
import java.util.LinkedList;

import com.subterranean_security.crimson.client.ClientStore;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.CoreStore;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.DirtyRect;
import com.subterranean_security.crimson.core.proto.Stream.EV_StreamData;
import com.subterranean_security.crimson.core.proto.Stream.EventData;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.proto.Stream.RemoteParam.RMethod;
import com.subterranean_security.crimson.core.proto.Stream.ScreenData;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.util.Native;

public class RemoteSlave extends Stream {

	private static final int blockSize = 16;

	private Robot robot;
	private Thread poller = new Thread(new Runnable() {
		private BufferedImage last;

		public void run() {

			Date iterationStart;
			while (!Thread.interrupted()) {
				iterationStart = new Date();
				BufferedImage image = robot.createScreenCapture(screenRect);
				Date d2 = new Date();

				ScreenData.Builder data = ScreenData.newBuilder();
				if (last == null || param.getRemoteParam().getRmethod() == RMethod.POLL) {
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
							if (!toggleMatrix[j][i] && image.getRGB(i, j) != last.getRGB(i, j)) {

								int w = (i + blockSize > image.getWidth()) ? image.getWidth() - i : blockSize;
								int h = (j + blockSize > image.getHeight()) ? image.getHeight() - j : blockSize;

								DirtyRect.Builder dr = DirtyRect.newBuilder().setSx(i).setSy(j).setW(w).setH(h);

								for (int jh = 0; jh < h; jh++) {
									for (int iw = 0; iw < w; iw++) {
										toggleMatrix[j + jh][i + iw] = true;
										dr.addRGBA(image.getRGB(i + iw, j + jh));
									}

								}

								data.addDirtyRect(dr);

							}
						}
					}
				}
				Date d3 = new Date();
				System.out.println("Screenshot taken in: " + (d2.getTime() - iterationStart.getTime())
						+ " ms. Processed in: " + (d3.getTime() - d2.getTime()) + " ms");

				last = image;
				uQueue.add(data.build());

				try {
					int iterationDuration = (int) (new Date().getTime() - iterationStart.getTime());
					Thread.sleep(iterationDuration > param.getPeriod() ? 0 : param.getPeriod() - iterationDuration);
				} catch (InterruptedException e) {
					return;
				}
			}

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
					robot = new Robot(gd);
					System.out.println(
							"Initialized robot on screen: " + screenRect.getWidth() + "X" + screenRect.getHeight());
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
		EventData ev = m.getEvStreamData().getEventData();
		if (ev.hasMouseMovedX()) {
			robot.mouseMove(ev.getMouseMovedX(), ev.getMouseMovedY());
		} else if (ev.hasKeyPressed()) {
			robot.keyPress(m.getEvStreamData().getEventData().getKeyPressed());
		} else if (ev.hasKeyReleased()) {
			robot.keyRelease(m.getEvStreamData().getEventData().getKeyReleased());
		} else if (ev.hasMouseReleased()) {
			robot.mouseRelease(ev.getMouseReleased());
		} else if (ev.hasMousePressed()) {
			robot.mouseMove(ev.getMouseMovedX(), ev.getMouseMovedY());
			robot.mousePress(ev.getMousePressed());
		}
	}

	@Override
	public void start() {
		switch (param.getRemoteParam().getRmethod()) {
		case NATIVE:
			Native.startRD();
			break;
		case POLL:
		case POLL_DELTA:
			poller.start();
			break;
		default:
			break;

		}

		timer.schedule(sendTask, 0, 50);

	}

	@Override
	public void stop() {
		timer.cancel();

		switch (param.getRemoteParam().getRmethod()) {
		case NATIVE:
			// TODO
			break;
		case POLL:
		case POLL_DELTA:
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
