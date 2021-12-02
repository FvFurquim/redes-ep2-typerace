package br.usp.each.typerace.server;

import java.util.Objects;
import java.util.Set;

public class Player {

    private String playerId;
    private int correct;
    private int wrong;
    private Set<String> currentWords;

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

    public int compareTo(Player p) {
        if(this.correct == p.correct)
            return p.wrong - this.wrong;

        return this.correct - p.correct;
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
