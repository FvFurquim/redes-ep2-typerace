package br.usp.each.typerace.server;

import java.util.*;

// Essa classe faz todo o trabalho do jogo que inclui: armazenar palavras, checar acertos e armazenar a pontuacao

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
        this.selectedWords = new HashSet<>();

        // Pega um Set de palavras atraves do WordListMaker passado pelo parametro
        Set<String> tempSet = wordListMaker.selectWords(numberOfWords);

        // Para evitar problemas de case, esse loop esta deixando todas as palavras maiusculas
        for(String word : tempSet) {
            selectedWords.add(word.toUpperCase());
        }

        // Armazena uma copia do Set de palavras em cada jogador
        for (String playerId : players) {
            scoreBoard.put(playerId, new Player(playerId, 0, 0, new HashSet<>(selectedWords)));
        }
    }

    public boolean checkAnswer(String playerId, String answer) {

        Player player = scoreBoard.get(playerId);

        if(player.checkAnswer(answer)){
            if(player.getCorrect() == maxScore) {
                isGameFinished = true;
                finalTime = new Date().getTime();
            }

            return true;
        }

        return false;
    }

    public Set<String> getWordsOfPlayer(String playerId) {
        return scoreBoard.get(playerId).getCurrentWords();
    }

    public String getWordsOfPlayerAsString(String playerId) {
        return setToWordList(getWordsOfPlayer(playerId));
    }

    // Esse metodo retorna uma lista ordenada dos jogadores de acordo com sua pontuacao
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
