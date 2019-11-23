package Utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Util {
	/**
     * This is very important for race conditions
	 * Thread safe println to deal with all the threads that may or may not be running around.
	 */
	public static void println(String s) {
		synchronized (System.out) {
			System.out.println(s);
		}
	}

	public static JSONObject updateTimestamp(JSONObject json) {
		json.put(MessageFields.SEND_TIME, System.currentTimeMillis());
		return json;
	}

	public static JSONObject stringToJson(String s) throws ParseException {
		return (JSONObject) new JSONParser().parse(s);
	}
}
