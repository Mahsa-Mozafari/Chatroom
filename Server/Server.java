package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static final ArrayList<BufferedWriter> clientWriters = new ArrayList<>();
    private static final ArrayList<String> chatHistory = new ArrayList<>();
    private static final Set<String> usernames = new HashSet<>();


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);

        while (true) {
            Socket clientSocket = serverSocket.accept();

            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    String username = in.readLine();

                    synchronized (usernames) {
                        if (usernames.contains(username)) {
                            out.write("This name is already taken. Connection closed. \n");
                            out.flush();
                            clientSocket.close();
                            return;
                        } else {
                            usernames.add(username);
                            out.write("Welcome, " + username+"\n");
                            out.flush();
                        }
                    }

                    synchronized (chatHistory) {
                        for (String msg : chatHistory) {
                            out.write(msg+"\n");
                            out.flush();
                        }
                    }

                    synchronized (clientWriters) {

                        clientWriters.add(out);
                    }

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        String msg = "(Message) " + username + ": " + inputLine;
                        System.out.println(msg);

                        synchronized (chatHistory) {
                            chatHistory.add(msg);
                        }

                        synchronized (clientWriters) {
                            for (BufferedWriter writer : clientWriters) {
                                writer.write(msg+"\n");
                                writer.flush();
                            }
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Connection error with a client.");
                } finally {

                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                    }
                }
            }).start();
        }
    }
}