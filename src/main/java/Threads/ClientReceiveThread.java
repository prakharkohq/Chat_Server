package Threads;

import Main.Client;
import Models.User;
import Utils.GlobalMessageFields;
import Utils.MessageFields;
import Utils.MessageTypes;
import Utils.PrivateMessageFields;
import Utils.ServerMessageFields;
import Utils.Util;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientReceiveThread extends Thread {
    private BufferedReader in;
    private boolean isRunning = true;
    private User currUser;
    Client client;

    /**
     * synchronized(this.lock) {
     *             this.ensureOpenConnection();
     *             if (this.skipLF) {
     *                 if (this.nextChar >= this.nChars && this.in.ready()) {
     *                     this.fill();
     *                 }
     *         }
     *
     * */

    public  ClientReceiveThread(Client client, BufferedReader in, User user) {
        this.in = in;
        currUser = user;
        this.client = client;
    }

    public void run() {
        // Keep checking for new data from server. If you get something, handle it appropriately
        while (isRunning) {
            try {
                if (in.ready()) {
                    String rawString = in.readLine();
                    JSONObject msg = (JSONObject) new JSONParser().parse(rawString);
                    handleMessage(msg);
                }
            } catch (IOException | ParseException e) {
                // maybe force close the client here?
                /**
                 * Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
                 *     try {
                 *         socket.close();
                 *         System.out.println("The server is shut down!");
                 *     } catch (IOException e) {
                 *}
                 });


                 OR

                 isRunning = false;
                 * */

                e.printStackTrace();
            }
            // to inform server i am done
            yield();
        }
    }

    /**
     * Handle the 4 different message types that can come in from the server
     */
    private void handleMessage(JSONObject msg) {
        String key = (String) msg.get(MessageFields.TYPE);
        String rawMsg, nickname, recipient;
        switch (key) {
            case MessageTypes.GLOBAL_CHAT_MSG:
                rawMsg = (String) msg.get(GlobalMessageFields.TEXT);
                nickname = (String) msg.get(GlobalMessageFields.SENDER);
                Util.println(nickname + ": " + rawMsg);
                break;
            case MessageTypes.USER_CONNECT:
                // TODO ???
                break;
            case MessageTypes.PRIVATE_CHAT_MSG:
                rawMsg = (String) msg.get(PrivateMessageFields.TEXT);
                nickname = (String) msg.get(PrivateMessageFields.SENDER);
                recipient = (String) msg.get(PrivateMessageFields.RECIPIENT);
                if (currUser.getNickname().equals(recipient) || currUser.getNickname().equals(nickname)) {
                    Util.println(nickname + " to " + recipient + ": " + rawMsg);
                }
                break;
            case MessageTypes.SERVER_MSG:
                handleServerMessages(msg);
                break;
        }
    }

    /**
     * Shutdown socket thread that listens for text from server.
     */
    public void stopListening() {
        isRunning = false;
    }

    /**
     * Handle the different types of Server Messages that the server can send.
        */
    private void handleServerMessages(JSONObject json) {
        String key = (String) json.get(ServerMessageFields.NOTIFICATION);
        String rawMsg;
        switch (key) {
            case ServerMessageFields.NotificationTypes.SERVER_SHUTDOWN:
                rawMsg = (String) json.get(ServerMessageFields.TEXT);
                Util.println(rawMsg);
                client.stopClient();
                break;
            case ServerMessageFields.NotificationTypes.USER_CONNECTED:
                // print the message which should have new name of person then update users list
            case ServerMessageFields.NotificationTypes.USERS_UPDATE:
                rawMsg = (String) json.get(ServerMessageFields.TEXT);
                Util.println(rawMsg);
                // update users list
                JSONArray ja = (JSONArray) json.get(ServerMessageFields.ALL_USERS);
                client.updateUsers(ja);
                break;
            case ServerMessageFields.NotificationTypes.WARNING:
                // just display the warning
                rawMsg = (String) json.get(ServerMessageFields.TEXT);
                Util.println("SERVER WARNING: " + rawMsg);
                break;
        }
    }
}
