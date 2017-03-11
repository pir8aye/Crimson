package com.subterranean_security.crimson.client.store;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

public final class TorrentStore {
	private TorrentStore() {
	}

	private static List<Client> torrents = new ArrayList<Client>();

	public static void addTorrent(File torrent, File destination) throws IOException {
		try {
			Client client = new Client(InetAddress.getLocalHost(), SharedTorrent.fromFile(torrent, destination));
			client.share();
			torrents.add(client);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
