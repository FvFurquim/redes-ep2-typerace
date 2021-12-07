package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class Client extends WebSocketClient {

    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("###Nova conexao feita com sucesso###");
        // System.out.println("\n----------------------------------------------------------\nVoce entrou na sala!");
    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(!reason.equalsIgnoreCase("invalidName")) {
            System.out.println("Voce saiu da sala!");
            System.exit(0);
        }
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
