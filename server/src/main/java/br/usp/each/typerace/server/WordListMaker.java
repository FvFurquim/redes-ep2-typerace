package br.usp.each.typerace.server;

import java.util.Set;

public interface WordListMaker {

    Set<String> selectWords(int numberOfWords);
}
