package Main;

import Models.User;
import Threads.ClientReceiveThread;
import Utils.MessageFactory;
import Utils.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class Client {
    private static final int port = 4444;
    private static final String hostname = "PRAKHAR-N-LAPTOP";
    private boolean isRunning = true;
    private Socket serverSocket;
    private ClientReceiveThread printThread = null; // TODO can print user info
    private User user;
    private Scanner scan; // to read input from users
    private ArrayList<String> users; //TODO maybe chang this to list of User objects more flexible

    public static void main(String[] args) throws UnknownHostException {
      //  InetAddress myHost = InetAddress.getLocalHost();
       // System.out.println(myHost.getHostName());
        Client client = new Client();
        client.startListening();
    }
    public Client() {

        Util.println("Starting Client...");

        // Get nickname for server purposes
        scan = new Scanner(System.in);
        Util.println("Enter nickname: ");
        String nickname = scan.nextLine();
        user = new User(nickname);
        users = new ArrayList<String>();
    }
    private void startListening() {
        // make connection to Server.
        try {
            serverSocket = new Socket(hostname, port);
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            // Start thread for printing messages incomming from server.
            printThread = new ClientReceiveThread(this, in, user);
            printThread.start();

            // Tell server user information
            out.println(user.toJSONString());

			Util.println("Successfully connected to server.");
            while (isRunning) {
                if (scan.hasNext()) {
                    String command = scan.nextLine();
                    handleCommand(out, user, command);
                }
            }
            printThread.stopListening();
            serverSocket.close();
            Util.println("Unfortunatley Disconnected from server.");
        } catch (IOException e) {
            System.out.println("Connection with Server failed. Closing Program.");
        }
        catch (Exception e )
        {
            System.out.println("Something else went wrong ");
        }
    }
    private void handleCommand(PrintWriter out, User user, String command) {
        Util.println("Enterd Command is "+command);
        String[] splits = command.split(" ");
        for (String str: splits)
        {
            System.out.println("Splits are "+str);
        }
        switch (splits[0]) {
            case "/disconnect":
            case "/leave":
                out.println(MessageFactory.createUserDisconnectRequestMessage(user).toJSONString());
                // Now we exit
                isRunning = false;
                break;
            case "/whisper": // same as message you ca
            case "/message":
                if(splits.length > 2) {
                    String recipient = splits[1];
                    // Check to see of recipient is in server or not.
                    if(users.contains(recipient)) {
                        String pm = String.join(" ", Arrays.copyOfRange(splits, 2, splits.length));
                        out.println(MessageFactory.createPrivateMessage(user, recipient, pm));
                    }
                }
                break;
            case "/users":
            case "/u":
                Util.println("Connected Users: ");
                for (String nickname : users) {
                    Util.println("\t" + nickname);
                }
                break;

            case "/connect":
                Util.println("Trying to connect a new user  ");
                if(splits.length > 2) {
                    String recipient = splits[1];
                    // Check to see of recipient is in server or not.
                    if(users.contains(recipient)) {
                        String pm = String.join(" ", Arrays.copyOfRange(splits, 2, splits.length));
                        out.println(MessageFactory.createPrivateMessage(user, recipient, pm));
                    }
                }
                break;
            default:
                Util.println("youve crossed the ladder for the purpose");
                out.println(MessageFactory.createGlobalMessage(user, command).toJSONString());
                break;
        }
    }


    public void stopClient() {
        if (serverSocket != null && serverSocket.isConnected()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public void updateUsers(Collection<String> newUsers) {
        users = null;
        users = new ArrayList<>(newUsers);
    }



}
