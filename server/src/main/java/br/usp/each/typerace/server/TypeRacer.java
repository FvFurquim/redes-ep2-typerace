package br.usp.each.typerace.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TypeRacer {

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

        List<String> allWords = new ArrayList<>();
        selectedWords = new HashSet<>();

        try {
            BufferedReader input = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = input.readLine()) != null) {
                allWords.add(line);
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
        catch(Exception e) {
            System.out.println("Algo deu errado");
            e.printStackTrace();
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

    public List<Player> getScoreBoard() {

        List<Player> sortedScore = new LinkedList<>(scoreBoard.values());
        Collections.sort(sortedScore, (a,b) -> b.compareTo(a));

        return sortedScore;
    }

    public Set<String> getSelectedWords() {
        return selectedWords;
    }

    public boolean isGameFinished() {
        return this.isGameFinished;
    }

    public long gameDuration() {
        return (this.finalTime - this.initialTime) / 1000;
    }
}
