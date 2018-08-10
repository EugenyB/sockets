package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {

    List<ClientHandler> handlers = new ArrayList<>();

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            for (;;) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(this, socket);
                handlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFree(String name) {
        return handlers.stream().noneMatch(h->name.equals(h.getName()));
    }

    public void remove(String name) {
        handlers.removeIf(h->name.equals(h.getName()));
    }

    public void remove(ClientHandler clientHandler) {
        handlers.remove(clientHandler);
    }

    public synchronized void send(String name, String line) {
        handlers.forEach(h -> h.print(name, line));
    }
}
