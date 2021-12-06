package br.usp.each.typerace.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TypeRacer {

    private static String filePath = "";
    private static List<String> allWords;

    private Map<String, Player> scoreBoard;
    private Set<String> selectedWords;
    private int maxScore;
    private boolean isGameFinished;
    private long initialTime;
    private long finalTime;

    public TypeRacer(Set<String> players, String filePath, int numberOfWords, int maxScore) {

        this.scoreBoard = new HashMap<>();
        this.maxScore = maxScore;
        this.isGameFinished = false;
        this.initialTime = new Date().getTime();

        selectedWords = new HashSet<>();

        if (!filePath.equals(TypeRacer.filePath)) {

            TypeRacer.filePath = filePath;
            allWords = new ArrayList<>();

            try {
                BufferedReader input = new BufferedReader(new FileReader(filePath));

                String line;
                while ((line = input.readLine()) != null) {
                    allWords.add(line);
                }
            } catch (Exception e) {
                System.out.println("Algo deu errado");
                e.printStackTrace();
            }
        }

        Random rand = new Random();

        for (int i = 0; i < numberOfWords; ) {
            int randNum = rand.nextInt(allWords.size());
            String selectedWord = allWords.get(randNum);

            if (!selectedWords.contains(selectedWord)) {
                selectedWords.add(selectedWord);
                i++;
            }
        }

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
        Collections.sort(sortedScore, (a,b) -> b.compareTo(a));

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
