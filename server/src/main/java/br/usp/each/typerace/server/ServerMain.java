package br.usp.each.typerace.server;

import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ServerMain {

    private WebSocketServer server;

    public ServerMain(WebSocketServer server) {
        this.server = server;
    }

    public void init() {
        server.start();
        System.out.println("Servidor iniciado na porta " + server.getPort());
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        WebSocketServer server = new Server(8080, new HashMap<>());
        ServerMain main = new ServerMain(server);

        main.init();

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            String in = input.readLine();
            server.broadcast(in);

            if(in.equals("exit")){
                server.stop(1000);
                break;
            }
        }

        System.out.println("Servidor finalizado");
    }
}
