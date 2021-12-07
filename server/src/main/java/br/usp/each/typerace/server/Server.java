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
        // Checa se um usuario com esse ID ja existe ao conectar
        if(!playerIdExists(conn)) {
            addNewConnection(conn);
        }
    }

    // Se o motivo por encerrar a conexao nao for "nome invalido", ou seja, nome repetido...
    // ele faz o procedimento padrao: informa a todos que o jogador saiu da sala
    // Se o motivo da perda de conexao foi nome invalido, ele nao avisa ninguem e so desconecta o jogador
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

        // Essa mensagem eh apenas a mensagem de retorno de conexao com o cliente
        // Nao deve ser considerada como input do usuario
        if(message.equalsIgnoreCase("###Nova conexao feita com sucesso###")) {
            System.out.println("Nova conexao feita com sucesso");
            return;
        }

        // Comandos de iniciar e finalizar o jogo so podem ser feitos enquanto nao tem nenhuma partida executando
        // Ou seja, enquanto o jogo eh null
        if(game == null) {

            String[] inputArray = message.split(" ");

            if (inputArray[0].equalsIgnoreCase("Iniciar")) {
                try {
                    int numberOfWords = Integer.parseInt(inputArray[1]);
                    int maxScore = Integer.parseInt(inputArray[2]);

                    // Chama o metodo que cria uma partida e retorna uma string com as palavras do jogo para serem trasmitidas
                    String wordList = startGame(numberOfWords, maxScore);

                    if(wordList.equals("")) {
                        conn.send("A pontuacao maxima nao pode ser maior do que a quantidade de palavras no jogo!\nPor favor, tente novamente!\n" + line);
                    }

                    else {
                        broadcast("Jogo iniciado! Lista de palavras:\n" + wordList + "\n" + line);
                    }
                }
                // Se a pessoa digitou "iniciar" e nao colocou os complementos de forma devida, ele entra nesse catch e envia essa mensagem
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
        // Nesse else, o game nao eh null, ou seja, a partida esta rodando
        // Nesse caso, todo input de usario eh considerado uma tentativa de resposta
        else {
            String playerId = getPlayerId(conn.getResourceDescriptor());

            if(game.checkAnswer(playerId, message)) {
                conn.send("Resposta correta!\n" + line);
            }
            else {
                conn.send("Resposta incorreta :(\n" + line);
            }

            // Se chegou na pontuacao maxima, vai transmitir a mensagem de resultado do jogo para todos os jogadores
            if(game.isGameFinished()) {
                broadcastGameResult();
                game = null;
            }
            // Se o jogo nao acabou, manda para o jogador atual quais palavras faltam para ele acertar
            // (acertando a tentativa atual ou nao)
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

    // Esse metodo adiciona o jogador no Map de conexoes, manda a mensagem para todo mundo que o jogador entrou na sala e manda a mensagem de boas vindas
    private void addNewConnection(WebSocket conn) {

        String playerId = getPlayerId(conn);
        this.connections.put(playerId, conn);

        broadcast("\n" + line + "\n" + playerId + " entrou na sala!" + "\nNumero de jogadores: " + numberOfConnections() + "\n" + line);

        conn.send("Bem-vindo a Corridona de Digitacao do Balacubaco!\nRegras:\n" +
                "- Palavras podem ser maiusculas ou minusculas\n- Envie uma palavra por vez\n- Vence quem atingir a pontuacao maxima primeiro" +
                "\n- Palavras erradas nao tiram ponto\n\nComandos:\n- Digite \"Iniciar [quantidade de palavras] [pontuacao maxima]\" para comecar\n- Digite \"Sair\" fora de uma partida para sair da sala\n\n- Divirta-se :)\n" + line);

        System.out.println("Nova conexao: " + playerId + " [" + conn.getRemoteSocketAddress().getAddress().getHostAddress() + "]");
    }

    private String startGame(int numberOfWords, int maxScore) {

        // Se o numero de palavras eh menor que o numero da pontuacao maxima, retorna string vazia, que significa entrada invalida
        if(maxScore > numberOfWords) {
            return "";
        }

        // Caso a entrada seja valida, cria um jogo e retorna todas as palavras selecionadas em forma de string
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\listaDePalavras.txt";
        this.game = new TypeRacer(connections.keySet(), numberOfWords, maxScore, new WordListFromFile(filePath));

        return game.getSelectedWordsAsString();
    }

    // Esse metodo pega o placar e transmite o resultado para todos os jogadores
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
