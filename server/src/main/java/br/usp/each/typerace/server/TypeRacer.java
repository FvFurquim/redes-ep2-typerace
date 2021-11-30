package br.usp.each.typerace.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TypeRacer {

    private Map<String, Player> scoreBoard;
    private int maxScore;
    private boolean isGameFinished;

    public TypeRacer(Set<String> players, String filePath, int numberOfWords, int maxScore) throws IOException {

        this.scoreBoard = new HashMap<>();
        this.maxScore = maxScore;
        this.isGameFinished = false;

        List<String> allWords = new ArrayList<>();
        Set<String> selectedWords = new HashSet<>();

        BufferedReader input = new BufferedReader(new FileReader(filePath));

        String line;
        while((line = input.readLine()) != null){
            allWords.add(line);
        }

        Random rand = new Random();

        for(int i = 0; i < numberOfWords; ){
            int randNum = rand.nextInt(allWords.size());
            String selectedWord = allWords.get(randNum);

            if(!selectedWords.contains(selectedWord)){
                selectedWords.add(selectedWord);
                i++;
            }
        }

        for(String playerId : players){
            scoreBoard.put(playerId, new Player(playerId, 0, 0, new HashSet<>(selectedWords)));
        }
    }

    public boolean checkAnswer(String playerId, String answer) {

        Player player = scoreBoard.get(playerId);

        if(player.getCurrentWords().contains(answer)){
            player.rightAnswer();
            player.getCurrentWords().remove(answer);

            if(player.getCorrect() == maxScore) {
                isGameFinished = true;
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
        Collections.sort(sortedScore, (a,b) -> a.compareTo(b));

        return sortedScore;
    }

    public boolean isGameFinished() {
        return this.isGameFinished;
    }
}
