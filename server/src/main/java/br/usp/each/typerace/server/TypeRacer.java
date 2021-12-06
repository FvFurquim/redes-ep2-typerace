package br.usp.each.typerace.server;

import java.util.*;

public class TypeRacer {

    private final Map<String, Player> scoreBoard;
    private final Set<String> selectedWords;
    private final int maxScore;
    private boolean isGameFinished;
    private long initialTime;
    private long finalTime;

    public TypeRacer(Set<String> players, int numberOfWords, int maxScore, WordListMaker wordListMaker) {

        this.scoreBoard = new HashMap<>();
        this.maxScore = maxScore;
        this.isGameFinished = false;
        this.initialTime = new Date().getTime();

        selectedWords = wordListMaker.selectWords(numberOfWords);

        for (String playerId : players) {
            scoreBoard.put(playerId, new Player(playerId, 0, 0, new HashSet<>(selectedWords)));
        }
    }

    public boolean checkAnswer(String playerId, String answer) {

        Player player = scoreBoard.get(playerId);

        if(player.getCurrentWords().contains(answer.toUpperCase())){
            player.rightAnswer();
            player.getCurrentWords().remove(answer.toUpperCase());

            if(player.getCorrect() == maxScore) {
                isGameFinished = true;
                finalTime = new Date().getTime();
            }

            return true;
        }

        player.wrongAnswer();
        return false;
    }

    public Set<String> getWordsOfPlayer(String playerId) {
        return scoreBoard.get(playerId).getCurrentWords();
    }

    public String getWordsOfPlayerAsString(String playerId) {
        return setToWordList(getWordsOfPlayer(playerId));
    }

    public List<Player> getScoreBoard() {

        List<Player> sortedScore = new LinkedList<>(scoreBoard.values());
        sortedScore.sort((a,b) -> b.compareTo(a));

        return sortedScore;
    }

    public Set<String> getSelectedWords() {
        return selectedWords;
    }

    public String getSelectedWordsAsString() {
        return setToWordList(getSelectedWords());
    }

    public boolean isGameFinished() {
        return this.isGameFinished;
    }

    public long gameDuration() {
        return (this.finalTime - this.initialTime) / 1000;
    }

    public String setToWordList(Set<String> set) {

        String wordList = "| ";
        for (String word : set) {
            wordList += word + " | ";
        }

        return wordList;
    }
}
