package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class ClientMain {

    private WebSocketClient client;

    public ClientMain(WebSocketClient client) {
        this.client = client;
    }

    public void init(String idCliente) {
        // System.out.println("Iniciando cliente: " + idCliente);
        client.connect();
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        WebSocketClient client;

        System.out.println("\nServidor [default: ws://localhost:8080]: ");
        String serverUri = input.readLine();

        if (serverUri.isEmpty())
            serverUri = "ws://localhost:8080";

        // Esse while serve para tentar pegar o nome do usuario
        // Enquanto ele nao tiver feito uma conexao com sucesso, ele permanece no loop
        while(true) {
            System.out.println("Nome de Usuario: ");
            String username = input.readLine();
            String connectionUri = serverUri;

            if (username.isEmpty()) {
                System.out.println("Nome vazio! Por favor, tente novamente!");
                continue;
            }

            connectionUri += "/playerId=" + username;

            client = new Client(new URI(connectionUri));
            ClientMain main = new ClientMain(client);

            main.init(username);

            TimeUnit.SECONDS.sleep(1);

            // Se o playerId estiver repetido, a conexao sera encerrada imediatamente, portanto, ele continua nesse loop pedindo entrada
            if(client.isOpen()) {
                break;
            }
        }

        // Esse loop vai pegar todas as tentativas de respostas do jogador e enviar ao serivdor
        while(true) {
            String in = input.readLine();
            client.send(in);
        }
    }
}
