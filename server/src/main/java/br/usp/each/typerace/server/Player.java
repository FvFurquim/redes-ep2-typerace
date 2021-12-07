package br.usp.each.typerace.server;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

// Essa classe possui todos os atributos necessarios de informacoes dos jogadores
// Como seu ID, quantidade de acertos e erros e um Set com todas as palavras que esse jogador ainda tem que acertar

public class Player {

    private final String playerId;
    private int correct;
    private int wrong;
    private final Set<String> currentWords;

    public Player(String playerId, int correct, int wrong, Set<String> currentWords) {
        this.playerId = playerId;
        this.correct = correct;
        this.wrong = wrong;
        this.currentWords = currentWords;
    }

    public void rightAnswer() {
        this.correct++;
    }

    public void wrongAnswer() {
        this.wrong++;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getCorrect() {
        return correct;
    }

    public int getWrong() {
        return wrong;
    }

    public Set<String> getCurrentWords() {
        return currentWords;
    }

    // O compareTo sera utilizado para comparar o placar de dois jogadores, apenas considerando acertos
    // Fracassos sao apenas criterio de desempate
    public int compareTo(Player p) {
        if(this.correct == p.correct)
            return p.wrong - this.wrong;

        return this.correct - p.correct;
    }

    public boolean checkAnswer(String answer) {

        if(getCurrentWords().contains(answer.toUpperCase())){
            rightAnswer();
            getCurrentWords().remove(answer.toUpperCase());

            return true;
        }

        wrongAnswer();
        return false;
    }

    public String toString() {
        return this.playerId + " - " + this.correct + " - " + this.wrong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId.equals(player.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, correct, wrong);
    }
}
