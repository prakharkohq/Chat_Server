package Utils;

public class ServerMessageFields extends MessageFields {
	public static final String NOTIFICATION = "notification";
	public static final String TEXT = "text";
	public static final String ALL_USERS = "allUsers";

	public static class NotificationTypes {
		public static final String SERVER_SHUTDOWN = "serverOff";
		public static final String USER_CONNECTED = "userConnected";
		public static final String WARNING = "warn";
		public static final String USERS_UPDATE = "usersUpdate";
	}
}
