package Utils;

import Models.User;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MessageFactory {


    /**
     * USER MESSAGE TYPES
     *
     * WARNING
     * CONNECT
     * DISCONNECT
     * */
	public static JSONObject createUserInitConnectRequestMessage(String nickname) {
		JSONObject s = new JSONObject();
		s.put(UserConnectMessageFields.TYPE, MessageTypes.USER_CONNECT);
		s.put(UserConnectMessageFields.USER_NICKNAME, nickname);
		s.put(UserConnectMessageFields.SEND_TIME, System.currentTimeMillis());
		s.put(UserConnectMessageFields.CONNECT_TYPE, UserConnectMessageFields.ConnectTypes.INITIAL_CONNECT);
        System.out.println("\n Created users init request with name "+nickname+" Connect type INITIAL CONNECT");
		return s;
	}

	public static JSONObject createUserDisconnectRequestMessage(User user) {
		JSONObject s = new JSONObject();
		s.put(UserConnectMessageFields.TYPE, MessageTypes.USER_CONNECT);
		s.put(UserConnectMessageFields.USER_NICKNAME, user.getNickname());
		s.put(UserConnectMessageFields.SEND_TIME, System.currentTimeMillis());
		s.put(UserConnectMessageFields.CONNECT_TYPE, UserConnectMessageFields.ConnectTypes.DISCONNECT);
		return s;
	}

	public static JSONObject createGlobalMessage(User sender, String msg) {
		JSONObject s = new JSONObject();
		s.put(GlobalMessageFields.TYPE, MessageTypes.GLOBAL_CHAT_MSG);
		s.put(GlobalMessageFields.SENDER, sender.getNickname());
		s.put(GlobalMessageFields.TEXT, msg);
		s.put(GlobalMessageFields.SEND_TIME, System.currentTimeMillis());
		return s;
	}

	public static JSONObject createPrivateMessage(User sender, String recipient, String msg) {
		JSONObject s = new JSONObject();
		s.put(PrivateMessageFields.TYPE, MessageTypes.PRIVATE_CHAT_MSG);
		s.put(PrivateMessageFields.SENDER, sender.getNickname());
		s.put(PrivateMessageFields.RECIPIENT, recipient);
		s.put(PrivateMessageFields.TEXT, msg);
		s.put(PrivateMessageFields.SEND_TIME, System.currentTimeMillis());
		return s;
	}

	public static JSONObject createShutdownMessage() {
		JSONObject s = new JSONObject();
		s.put(ServerMessageFields.TYPE, MessageTypes.SERVER_MSG);
		s.put(ServerMessageFields.NOTIFICATION, ServerMessageFields.NotificationTypes.SERVER_SHUTDOWN);
		s.put(ServerMessageFields.TEXT, "Server is shutting down.");
		return s;
	}

	public static JSONObject createUserConnectedMessage(User newUser, Collection<User> totalUsers) {
		JSONObject s = new JSONObject();
		s.put(ServerMessageFields.TYPE, MessageTypes.SERVER_MSG);
		s.put(ServerMessageFields.NOTIFICATION, ServerMessageFields.NotificationTypes.USER_CONNECTED);
		s.put(ServerMessageFields.TEXT, "User " + newUser.getNickname() + " connected.");
		JSONArray ja = new JSONArray();
		for(User user : totalUsers) {
			ja.add(user.getNickname());
		}
		if(!ja.contains(newUser.getNickname())) {
			ja.add(newUser.getNickname());
		}
		// here JSON ARRAY can have more then one fields
		s.put(ServerMessageFields.ALL_USERS,ja);
		return s;
	}

	public static JSONObject createUserDisconnectedMessage(User goneUser, Collection<User> totalUsers) {
		JSONObject s = new JSONObject();
		s.put(ServerMessageFields.TYPE, MessageTypes.SERVER_MSG);
		s.put(ServerMessageFields.NOTIFICATION, ServerMessageFields.NotificationTypes.USERS_UPDATE);
		s.put(ServerMessageFields.TEXT, "User " + goneUser.getNickname() + " disconnected.");
		JSONArray ja = new JSONArray();
		for(User user : totalUsers) {
			ja.add(user.getNickname());
		}
		if(ja.contains(goneUser.getNickname())) {
			ja.remove(goneUser.getNickname());
		}
		s.put(ServerMessageFields.ALL_USERS,ja);
		return s;
	}

	public static JSONObject createWarningMessage(String text) {
		JSONObject s = new JSONObject();
		s.put(ServerMessageFields.TYPE, MessageTypes.SERVER_MSG);
		s.put(ServerMessageFields.NOTIFICATION, ServerMessageFields.NotificationTypes.WARNING);
		s.put(ServerMessageFields.TEXT, text);
		return s;
	}
}
