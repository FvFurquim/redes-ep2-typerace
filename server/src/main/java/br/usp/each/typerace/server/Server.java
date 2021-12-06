package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;

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
        if(!playerIdExists(conn)) {
            addNewConnection(conn);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if(!reason.equalsIgnoreCase("invalidName")) {
            connections.remove(getPlayerId(conn));
            broadcast(getPlayerId(conn.getResourceDescriptor()) + " saiu da sala!\n" + line);
            System.out.println("Conexao perdida: " + getPlayerId(conn.getResourceDescriptor()) + " [" + conn.getRemoteSocketAddress().getAddress().getHostAddress() + "]");
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        if(game == null) {

            String[] inputArray = message.split(" ");

            if (inputArray[0].equalsIgnoreCase("Iniciar")) {
                try {
                    int numberOfWords = Integer.parseInt(inputArray[1]);
                    int maxScore = Integer.parseInt(inputArray[2]);

                    String wordList = startGame(numberOfWords, maxScore);

                    if(wordList.equals("")) {
                        conn.send("A pontuacao maxima nao pode ser maior do que a quantidade de palavras no jogo!\nPor favor, tente novamente!\n" + line);
                    }

                    else {
                        broadcast("Jogo iniciado! Lista de palavras:\n" + wordList + "\n" + line);
                    }
                }
                catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
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
                broadcastGameResult();
                game = null;
            }
            else {
                String remainingWords = game.getWordsOfPlayerAsString(playerId) + "\n" + line;
                conn.send(remainingWords);
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            System.out.println("Deu erro e conn nao eh null");
        }
    }

    @Override
    public void onStart() {
        System.out.println("Servidor iniciado na porta " + getPort());
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public int numberOfConnections() {
        return connections.size();
    }

    private String getPlayerId(String resourceDescriptor) {
        return resourceDescriptor.substring(resourceDescriptor.indexOf("playerId=") + 9);
    }

    private String getPlayerId(WebSocket conn) {
        return getPlayerId(conn.getResourceDescriptor());
    }

    private boolean playerIdExists(WebSocket conn) {
        if(connections.containsKey(getPlayerId(conn))) {
            conn.send("Nome de usuario ja existe!\nPor favor, insira um nome diferente\n" + line);
            conn.close(1000, "invalidName");
            return true;
        }

        return false;
    }

    private void addNewConnection(WebSocket conn) {

        String playerId = getPlayerId(conn);
        this.connections.put(playerId, conn);

        broadcast("\n" + line + "\n" + playerId + " entrou na sala!" + "\nNumero de jogadores: " + numberOfConnections() + "\n" + line);

        conn.send("Bem-vindo a Corridona de Digitacao do Balacubaco!\nRegras:\n" +
                "- Palavras podem ser maiusculas ou minusculas\n- Envie uma palavra por vez\n- Vence quem atingir a pontuacao maxima primeiro" +
                "\n- Palavras erradas nao tiram ponto\n- Digite \"Sair\" fora de uma partida para sair da sala\n- Digite \"Iniciar [quantidade de palavras] [pontuacao maxima]\" para comecar\n\n- Divirta-se :)\n" + line);

        System.out.println("Nova conexao: " + playerId + " [" + conn.getRemoteSocketAddress().getAddress().getHostAddress() + "]");
    }

    private String startGame(int numberOfWords, int maxScore) {

        if(maxScore > numberOfWords) {
            return "";
        }

        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\listaDePalavras.txt";
        this.game = new TypeRacer(connections.keySet(), numberOfWords, maxScore, new WordListFromFile(filePath));

        return game.getSelectedWordsAsString();
    }

    private void broadcastGameResult() {

        String result = "Jogo encerrado!\nPlacar [Nome - Acertos - Erros]:\n";
        int count = 0;
        for(Player player : game.getScoreBoard()) {
            result += (++count) + "- " + player + "\n";
        }

        result += "\nDuracao do jogo: " + game.gameDuration() + " s\n" + line;
        broadcast(result);
    }
}
