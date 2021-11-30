package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Server extends WebSocketServer {

    private final Map<String, WebSocket> connections;
    private TypeRacer game;

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
        broadcast(getPlayerId(conn.getResourceDescriptor()) + " saiu da sala!");
        System.out.println(getPlayerId(conn.getResourceDescriptor()) + "[" + conn.getRemoteSocketAddress().getAddress().getHostAddress() + "] saiu da sala!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
//        broadcast(message);
//        System.out.println(getPlayerId(conn.getResourceDescriptor()) + ": " + message);

        if (message.equalsIgnoreCase("Iniciar") && game == null) {

            String dir = "D:\\fvfur\\Documents\\Programas\\Github\\redes-ep2-typerace\\server\\src\\main\\java\\br\\usp\\each\\typerace\\server";
            game = new TypeRacer(connections.keySet(), dir + "\\listaDePalavras.txt", 10, 3);

            String wordList = "";
            for (String word : game.getSelectedWords()) {
                wordList += word + " ";
            }

            broadcast("Jogo iniciado! Lista de palavras:\n" + wordList);
        }
        else if (message.equalsIgnoreCase("Sair") && game == null) {
            removeConnection(conn);
        }
        else if(game != null) {
            String playerId = getPlayerId(conn.getResourceDescriptor());

            if(game.checkAnswer(playerId, message)) {
                conn.send("Resposta correta!\n");
            }
            else {
                conn.send("Resposta incorreta :(\n");
            }

            if(game.isGameFinished()) {

                String result = "\nJogo encerrado!\nPlacar [Nome - Acertos - Erros]:\n";
                int count = 0;
                for(Player player : game.getScoreBoard()) {
                    result += (++count) + "- " + player + "\n";
                }

                result += "Duracao do jogo: " + game.gameDuration() + " s\n";

                broadcast(result);
                game = null;
            }
            else {
                String remainingWords = "";
                for (String word : game.getWordsOfPlayer(playerId)) {
                    remainingWords += word + " ";
                }

                conn.send(remainingWords);
            }
        }
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
