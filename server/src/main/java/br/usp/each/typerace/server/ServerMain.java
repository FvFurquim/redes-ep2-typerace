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
        System.out.println("Iniciando servidor...");
        server.start();
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Porta do servidor [default: 8080]: ");
        String portStr = input.readLine();

        int port;

        if (portStr.isEmpty())
            port = 8080;
        else
            port = Integer.parseInt(portStr);

        WebSocketServer server = new Server(port, new HashMap<>());
        ServerMain main = new ServerMain(server);

        main.init();

        while(true) {
            String in = input.readLine();

            if(in != null && in.equals("encerrar")){
                server.stop(1000);
                break;
            }
        }

        System.out.println("\nServidor finalizado");
    }
}
