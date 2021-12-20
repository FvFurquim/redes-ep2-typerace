# redes-ep2-typerace
Repositório para o EP2 de Redes de Computadores, EACH-USP - 2021/2

# Integrantes
* Felipe Voigtlaender Furquim - 11208030
* Stefany Ramos Ramsdorf Nohama - 10843552
* Vitor da Silva Cairolli - 11276617

## Pré-requisitos
* JDK 11 ou maior (testado com a JDK11 OpenJDK)
* Gradle (incluso no repositório, não é necessário instalá-lo)

### Rodando
Para rodar o servidor
```sh
./gradlew server:run
```

Para rodar um cliente
```sh
./gradlew client:run
```
### Regras do Jogo
* Vence quem atingir a pontuação máxima primeiro
* Envie uma palavra por vez
* Não há ordem específica para enviar palavras
* Palavras podem ser maiúsculas ou minúsculas
* Palavras erradas não tiram ponto
* Comandos:
  * Digite "Iniciar [quantidade de palavras] [pontuacao maxima]" para começar
  * Digite "Sair" fora de uma partida para sair da sala
* Divirta-se :)
