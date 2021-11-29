package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Server extends WebSocketServer {

    private final Map<String, WebSocket> connections;

    public Server(int port, Map<String, WebSocket> connections) {
        super(new InetSocketAddress(port));
        this.connections = connections;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Bem-vindo a Corridona de Digitacao do Balacubaco!\n");

        String playerId = getPlayerId(handshake.getResourceDescriptor());

        addConnection(playerId, conn);

        broadcast("Nova conexao: " + playerId + "\nNumero de jogadores: " + numberOfConnections());
        System.out.println(playerId + "[" + conn.getRemoteSocketAddress().getAddress().getHostAddress() + "] entrou na sala!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " saiu da sala!");
        System.out.println(conn + " saiu da sala!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
        System.out.println(conn + ": " + message);
        System.out.println(conn.getRemoteSocketAddress());
        System.out.println(conn.getResourceDescriptor());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            System.out.printf("Ih rapa, deu ruim");
        }
    }

    @Override
    public void onStart() {
        System.out.println("Servidor acordou!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    private void addConnection(String playerId, WebSocket conn) {
        this.connections.put(playerId, conn);
    }

    public int numberOfConnections() {
        return connections.size();
    }

    private String getPlayerId(String resourceDescriptor) {
        return resourceDescriptor.substring(resourceDescriptor.indexOf("playerId=") + 9);
    }
}
