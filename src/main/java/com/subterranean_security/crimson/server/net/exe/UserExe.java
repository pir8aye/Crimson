package com.subterranean_security.crimson.server.net.exe;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.ProtoUtil.PDFactory;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Users.RQ_AddUser;
import com.subterranean_security.crimson.proto.core.net.sequences.Users.RS_AddUser;
import com.subterranean_security.crimson.proto.core.net.sequences.Users.RS_EditUser;
import com.subterranean_security.crimson.server.store.ServerDatabaseStore;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.Universal;

public class UserExe extends Exelet {

	public UserExe(Connector connector) {
		super(connector);
	}

	public static void rq_add_user(Connector connector, Message m) {
		// TODO check permissions
		connector.write(
				Message.newBuilder().setId(m.getId()).setRsAddUser(RS_AddUser.newBuilder().setResult(true)).build());

		ServerDatabaseStore.getDatabase().addLocalUser(m.getRqAddUser().getUser(), m.getRqAddUser().getPassword(),
				new ViewerPermissions(m.getRqAddUser().getPermissionsList()));

		// apprise viewers
		// TODO add permissions
		NetworkStore.broadcastTo(Universal.Instance.VIEWER,
				new PDFactory(connector.getCvid()).add(AK_VIEWER.USER, m.getRqAddUser().getUser()).buildMsg());

	}

	// TODO REWRITE!!
	public static void rq_edit_user(Connector connector, Message m) {
		// TODO check permissions
		connector.write(
				Message.newBuilder().setId(m.getId()).setRsEditUser(RS_EditUser.newBuilder().setResult(true)).build());

		RQ_AddUser rqad = m.getRqEditUser().getUser();

		ViewerProfile vp = null;

		try {
			vp = ServerProfileStore.getViewer(rqad.getUser());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// EV_ViewerProfileDelta.Builder b = EV_ViewerProfileDelta.newBuilder()
		// .setPd(EV_ProfileDelta.newBuilder().addGroup(ProtoUtil.getNewGeneralGroup()
		// .putAttribute(AKeySimple.VIEWER_USER.getWireID(), rqad.getUser())))
		// .addAllViewerPermissions(m.getRqAddUser().getPermissionsList());

		if (rqad.getPermissionsCount() != 0) {
			vp.getPermissions().add(rqad.getPermissionsList());
			// b.addAllViewerPermissions(rqad.getPermissionsList());
		}

		if (rqad.hasPassword() && ServerDatabaseStore.getDatabase().validLogin(rqad.getUser(),
				CryptoUtil.hashCrimsonPassword(m.getRqEditUser().getOldPassword(),
						ServerDatabaseStore.getDatabase().getSalt(rqad.getUser())))) {
			ServerDatabaseStore.getDatabase().changePassword(rqad.getUser(), rqad.getPassword());

		}

		// Message update = Message.newBuilder().setEvViewerProfileDelta(b).build();
		//
		// NetworkStore.broadcastTo(Universal.Instance.VIEWER, update);

	}

}
