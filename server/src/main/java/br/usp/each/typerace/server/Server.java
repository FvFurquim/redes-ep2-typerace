package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Server extends WebSocketServer {

    private final Map<String, WebSocket> connections;
    private TypeRacer game;
    private final String line = "----------------------------------------------------------";

    public Server(int port, Map<String, WebSocket> connections) {
        super(new InetSocketAddress(port));
        this.connections = connections;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        String playerId = getPlayerId(handshake.getResourceDescriptor());

        if(connections.containsKey(playerId)) {
            conn.send("Nome de usuario ja existe!\nPor favor, insira um nome diferente\n" + line);
            conn.close(1000, "invalidName");
            return;
        }

        addConnection(playerId, conn);

        broadcast("\n" + line + "\n" + playerId + " entrou na sala!" + "\nNumero de jogadores: " + numberOfConnections() + "\n" + line);

        conn.send("Bem-vindo a Corridona de Digitacao do Balacubaco!\nRegras:\n" +
                "- Palavras podem ser maiusculas ou minusculas\n- Envie uma palavra por vez\n- Vence quem atingir a pontuacao maxima primeiro" +
                "\n- Palavras erradas nao tiram ponto\n- Digite \"Sair\" fora de uma partida para sair da sala\n- Digite \"Iniciar [quantidade de palavras] [pontuacao maxima]\" para comecar\n\n- Divirta-se :)\n" + line);

        System.out.println("Nova conexao: " + playerId + " [" + conn.getRemoteSocketAddress().getAddress().getHostAddress() + "]");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(getPlayerId(conn.getResourceDescriptor()) + " saiu da sala!\n" + line);
        System.out.println("Conexao perdida: " + getPlayerId(conn.getResourceDescriptor()) + " [" + conn.getRemoteSocketAddress().getAddress().getHostAddress() + "]");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        if(game == null) {

            String[] inputArray = message.split(" ");

            if (inputArray[0].equalsIgnoreCase("Iniciar")) {
                try {
                    int numberOfWords = Integer.parseInt(inputArray[1]);
                    int maxScore = Integer.parseInt(inputArray[2]);

                    if(maxScore > numberOfWords) {
                        conn.send("A pontuacao maxima nao pode ser maior do que a quantidade de palavras no jogo!\nPor favor, tente novamente!\n" + line);
                    }
                    else {
                        String dir = System.getProperty("user.dir") + "\\src\\main\\resources";;
                        game = new TypeRacer(connections.keySet(), dir + "\\listaDePalavras.txt", numberOfWords, maxScore);

                        String wordList = setToWordList(game.getSelectedWords());
                        broadcast("Jogo iniciado! Lista de palavras:\n" + wordList + "\n" + line);
                    }
                }
                catch (Exception e) {
                    conn.send("Comando invalido!\nPor favor, tente novamente!\nLembre-se de digitar: Iniciar [quantidade de palavras] [pontuacao maxima]\n" + line);
                }
            }
            else if (message.equalsIgnoreCase("Sair")) {
                conn.close();
            }
            else {
                conn.send("Comando invalido!\nPor favor, tente novamente!\n" + line + "\n");
            }
        }
        else {
            String playerId = getPlayerId(conn.getResourceDescriptor());

            if(game.checkAnswer(playerId, message)) {
                conn.send("Resposta correta!\n" + line);
            }
            else {
                conn.send("Resposta incorreta :(\n" + line);
            }

            if(game.isGameFinished()) {
                String result = "Jogo encerrado!\nPlacar [Nome - Acertos - Erros]:\n";
                int count = 0;
                for(Player player : game.getScoreBoard()) {
                    result += (++count) + "- " + player + "\n";
                }

                result += "\nDuracao do jogo: " + game.gameDuration() + " s\n" + line;
                broadcast(result);

                game = null;
            }
            else {
                String remainingWords = setToWordList(game.getWordsOfPlayer(playerId)) + "\n" + line;
                conn.send(remainingWords);
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            System.out.println("Ih rapa, deu ruim");
        }
    }

    @Override
    public void onStart() {
        System.out.println("Servidor iniciado na porta " + getPort());
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

    private String setToWordList(Set<String> set) {

        String wordList = "| ";
        for (String word : set) {
            wordList += word + " | ";
        }

        return wordList;
    }
}
