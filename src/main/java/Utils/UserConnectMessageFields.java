package Utils;

public class UserConnectMessageFields extends MessageFields{
	public static final String USER_NICKNAME = "nickname";
	public static final String CONNECT_TYPE = "connectType";

	public static final class ConnectTypes {
	    // to maintain the state
		public static final String INITIAL_CONNECT = "init";
		public static final String DISCONNECT = "disconnect";
	}
}
