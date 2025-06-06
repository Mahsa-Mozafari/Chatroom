package Client;

import Model.User;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client extends Thread {
    private final User user;
    private final Map<String, Long> sentMessages = new HashMap<>();

    public Client(String name) {
        this.user = new User(name);
    }

    public void run() {
        try {
            Socket socket = new Socket("127.0.0.1", 6666);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            out.write(user.getName() + "\n");
            out.flush();

            new Thread(() -> {
                String serverMsg;
                try {
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);

                        String prefix = "(Message) " + user.getName() + ": ";
                        if (serverMsg.startsWith(prefix)) {
                            String content = serverMsg.substring(prefix.length());

                            synchronized (sentMessages) {
                                if (sentMessages.containsKey(content)) {
                                    long ping = System.currentTimeMillis() - sentMessages.get(content);
                                    System.out.println("Ping: " + ping + " ms");
                                    sentMessages.remove(content);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            }).start();

            String msg;
            long time=System.currentTimeMillis();
            while ((msg = userInput.readLine()) != null) {
                synchronized (sentMessages) {
                    sentMessages.put(msg, time);
                }
                out.write(msg + "\n");
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("Cannot connect to server.");
        }
    }
}