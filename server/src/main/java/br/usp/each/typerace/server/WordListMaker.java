package br.usp.each.typerace.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class WordListMaker {

    private static String filePath = "";
    private static List<String> allWords;

    public WordListMaker(String filePath) {

        if (!filePath.equals(WordListMaker.filePath)) {
            WordListMaker.filePath = filePath;
            allWords = new ArrayList<>();

            try {
                BufferedReader input = new BufferedReader(new FileReader(filePath));

                String line;
                while ((line = input.readLine()) != null) {
                    allWords.add(line);
                }
            } catch (Exception e) {
                System.out.println("Erro ao ler arquivo");
                e.printStackTrace();
            }
        }
    }

    public Set<String> selectWords(int numberOfWords) {

        Set<String> selectedWords = new HashSet<>();
        Random rand = new Random();

        for (int i = 0; i < numberOfWords; ) {
            int randNum = rand.nextInt(allWords.size());
            String selectedWord = allWords.get(randNum);

            if (!selectedWords.contains(selectedWord)) {
                selectedWords.add(selectedWord);
                i++;
            }
        }

        return selectedWords;
    }
}
