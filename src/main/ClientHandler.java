package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private String name;
    private Main main;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Main main, Socket socket) {
        this.main = main;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            this.writer = writer;
            this.reader = reader;
            writer.println("Hello! What's your name?");
            String name = reader.readLine();
            if (main.isFree(name)) {
                this.name = name;
                while (true) {
                    String line = reader.readLine();
                    main.send(name, line);
                    if ("BYE".equals(line)) {
                        main.remove(name);
                        break;
                    }
                }
            } else {
                main.remove(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public String getName() {
        return name;
    }

    public void print(String name, String line) {
        writer.println(name+ "> " + line);
    }
}
