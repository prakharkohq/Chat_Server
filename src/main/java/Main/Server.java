package Main;

import Models.User;
import Threads.ServerCommandsThread;
import Threads.ServerHandleClientThread;
import Utils.MessageFactory;
import Utils.PrivateMessageFields;
import Utils.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class Server {
    private ServerSocket serverSocket = null;
    private HashMap<String, ServerHandleClientThread> connectedThreads = new HashMap<String, ServerHandleClientThread>();
    private HashMap<String, User> connectedUsers = new HashMap<String, User>();
    private boolean isRunning = true;

    // Static stuff
    private static final int port = 4444;
    private static Server instance;

    public static void main(String[] args) {
        Server server = Server.getInstance();
        server.handleIncommingRequests();
        System.exit(0);
    }

    private Server() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not start Server socket");
            System.exit(1);
        }
        Util.println("\n Starting Server On Port "+port);
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private void handleIncommingRequests() {
        Socket clientSocket = null;
        ServerCommandsThread commandsThread = new ServerCommandsThread();
        commandsThread.start();
        // now listen for incomming requests and create a server thread if a socket is found
        while (isRunning) {
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // start thread for handling sockets.
            ServerHandleClientThread thread = new ServerHandleClientThread(clientSocket);
            thread.start();
        }
    }
// Server Users to send messages
    public synchronized Collection<User> getListOfUsers() {
        return connectedUsers.values();
    }

    public synchronized void addThread(User user, ServerHandleClientThread thread) {
        // At the same time, broadcast to all threads that there is a new user.
        connectedThreads.put(user.getNickname(), thread);
        connectedUsers.put(user.getNickname(), user);
        broadcastMessage(MessageFactory.createUserConnectedMessage(user, connectedUsers.values()));
    }

    public synchronized void removeThread(User user) {
        connectedThreads.remove(user.getNickname());
        connectedUsers.remove(user.getNickname());
    }

    public synchronized void broadcastMessage(JSONObject msg) {
        for (ServerHandleClientThread thread : connectedThreads.values()) {
            thread.sendMessageToClient(msg);
        }
    }

    public synchronized void broadcastGlobalMessage(User sender, String msg) {
        JSONObject json = MessageFactory.createGlobalMessage(sender, msg);
        for (ServerHandleClientThread thread : connectedThreads.values()) {
            thread.sendMessageToClient(json);
        }
    }

    public synchronized void sendPrivateMessage(User sender, String recipient, String msg) {
        if (connectedUsers.containsKey(recipient)) {
            JSONObject json = MessageFactory.createPrivateMessage(sender, recipient, msg);
            connectedThreads.get(recipient).sendMessageToClient(json);
            connectedThreads.get(sender.getNickname()).sendMessageToClient(json);
        }
    }
/*
    {
        "Sender": "Prakhar",
            "recipent": "nigam",
            "message": "hi  how are you"
    }*/
    public synchronized void sendPrivateMessage(JSONObject json) {
        String recipient = (String) json.get(PrivateMessageFields.RECIPIENT);
        String sender = (String) json.get(PrivateMessageFields.SENDER);
        if (connectedUsers.containsKey(recipient)) {
            connectedThreads.get(recipient).sendMessageToClient(json);
            connectedThreads.get(sender).sendMessageToClient(json);
        } else {
            // send a message back to the sender to tell them that user not found.
            connectedThreads.get(sender).sendMessageToClient(MessageFactory.createWarningMessage("User " + recipient + " not found."));
        }
    }

    public synchronized void stopServer() {
        // server is shutting down.
        Util.println("Shutting down server.");
        broadcastMessage(MessageFactory.createShutdownMessage());
        isRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
