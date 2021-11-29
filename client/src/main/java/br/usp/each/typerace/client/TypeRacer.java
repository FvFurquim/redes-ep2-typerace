package br.usp.each.typerace.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TypeRacer {

    private Map<String, Integer> scoreBoard;
    private Map<String, Set<String>> currentWords;

    public TypeRacer(Set<String> players, String filePath, int numberOfWords) throws IOException {

        this.scoreBoard = new TreeMap<>();
        this.currentWords = new HashMap<>();

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

        for(String player : players){
            scoreBoard.put(player, 0);
            currentWords.put(player, new HashSet<>(selectedWords));
        }
    }

    public boolean checkAnswer(String player, String answer) {
        if(currentWords.get(player).contains(answer)){
            scoreBoard.put(player, scoreBoard.get(player) + 1);
            currentWords.get(player).remove(answer);
            return true;
        }

        return false;
    }

    public Set<String> getWordsOfPlayer(String player) {
        return currentWords.get(player);
    }

    public Map<String, Integer> getScoreBoard() {
        return scoreBoard;
    }
}
