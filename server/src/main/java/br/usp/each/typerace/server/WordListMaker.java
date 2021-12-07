package br.usp.each.typerace.server;

import java.util.Set;

//Com essa inteface, eh possivel mudar a forma em que o servidor seleciona as palavras para o jogo
//Pode-se utilizar um arquivo, uma API, um banco de dados ou outras formas, dependendo da implementação

public interface WordListMaker {

    Set<String> selectWords(int numberOfWords);
}
