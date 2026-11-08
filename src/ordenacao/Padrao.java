package ordenacao;

/**
 * Padrao.java
 * 
 * Descreve os três padrões de entrada usados nos benchmarks do Trabalho 2.
 * Cada padrão muda a ORDEM inicial dos dados, e é isso que altera o desempenho
 * dos algoritmos de ordenação (ex.: Quick Sort com pivô ruim).
 * 
 * Quem usa: GeradorDeDados (monta o vetor no padrão pedido) e BenchmarkRunner
 * (percorre os três padrões em cada experimento).
 */
public enum Padrao {

    /** Dados na ordem em que foram sorteados da base (sem ordem nenhuma). */
    ALEATORIO,

    /** Dados já em ordem crescente (melhor caso de alguns algoritmos). */
    ORDENADO,

    /** Dados em ordem decrescente (pior caso clássico de vários algoritmos). */
    REVERSO
}