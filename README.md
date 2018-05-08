# Projeto-1-IA
## Requisitos para rodar o experimento
  Para executar o experimento precisa-se do Java Development Kit (JDK) e do Java SE Runtime Environment e (JRE), abos na verso 8.
  
  Link JDK 8: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
  
  Link JRE 8: http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
  
  Além disso o programa deve ser preferencialmente executado usando terminal. 
  
  É recomendavel também usar o programa make para a execução do arquivo Makefile.
  
  Link Make Windows: http://gnuwin32.sourceforge.net/packages/make.htm
  
  Make, em linux, faz parte por default.
  
  O procedimento abaixo foi testado em Ubuntu 16.04 LTS e em Windows 10.

## Execução do experimento
  Primeiramente deve-se baixar o projeto via git usando um dos dois comandos abaixo:
  
  Usando HTTPS:
  ```
  git clone https://github.com/WallaceManzano/Projeto-1-IA.git
  ```
  Usando SSH:
  ```
  git clone git@github.com:SO-I-2017-2/cadeocafe.git
  ```
### Compilação
  Primeiramente deve-se mudar o working directory para **Projeto-1-IA**
  
  No diretório **Projeto-1-IA**, para compilar usando o programa make deve-se usar o seguinte comando:
  ```
  make compile
  ```
  Sem o programa make, também no diretório **Projeto-1-IA**, deve-se usar os seguintes comandos (**nessa ordem**):
  ```
  mkdir -p bin
  javac -d bin -cp bin/ src/utils/ComparatorDouble.java
  javac -d bin -cp bin/ src/utils/PriorityQueue.java
  javac -d bin -cp bin/ src/utils/UnionFind.java
  javac -d bin -cp bin/ src/utils/Grafo.java
  javac -d bin -cp bin/ src/aestrela/Estado.java
  javac -d bin -cp bin/ src/aestrela/PCVAEstrela.java
  javac -d bin -cp bin/ src/branch_and_bound/PCVBranchAndBound.java
  javac -d bin -cp bin/ src/experimento/Experimento.java
  ```
### Execução
  Antes de executar o experimento deve-se compilar o código.
  Para executar o experimento deve-se, no diretório **Projeto-1-IA**, usar o seguinte comando:
  ```
  make experimento
  ```
  Ou caso não tenha o programa make, também no diretório **Projeto-1-IA**, deve-se usar o seguinte comando:
  ```
  java -cp bin experimento/Experimento
  ```
  
  O programa do experimento irá gerar arquivos no pasta **Projeto-1-IA** Resultados[i].txt, onde [i] é o número de cidades.


