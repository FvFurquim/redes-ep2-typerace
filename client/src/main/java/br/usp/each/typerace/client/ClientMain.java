package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class ClientMain {

    private WebSocketClient client;

    public ClientMain(WebSocketClient client) {
        this.client = client;
    }

    public void init(String idCliente) {
        System.out.println("Iniciando cliente: " + idCliente);
        client.connect();
    }

    public static void main(String[] args) throws IOException {

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("\nServidor [default: ws://localhost:8080]: ");
        String serverUri = input.readLine();

        if (serverUri.isEmpty())
            serverUri = "ws://localhost:8080";

        System.out.print("\nNome de Usuario [default: codigo doido]: ");
        String username = input.readLine();

        serverUri = serverUri + "/playerId=" + username;

        if (username.isEmpty())
            username = "" + UUID.randomUUID();

        try {
            WebSocketClient client = new Client(new URI(serverUri));

            ClientMain main = new ClientMain(client);

            main.init(username);

            while(true) {
                String in = input.readLine();
                client.send(in);
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
