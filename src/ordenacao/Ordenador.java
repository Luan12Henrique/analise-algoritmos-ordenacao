package ordenacao;

/**
 * Ordenador.java
 *
 * Contrato que todo algoritmo de ordenação do projeto implementa
 * (BubbleSort, SelectionSort, QuickSort, MergeSort). Assim o BenchmarkRunner
 * e a GUI tratam todos da mesma forma, sem saber qual é qual.
 *
 * Contadores (definição única para todos os algoritmos):
 *  - comparações: cada vez que dois elementos do vetor são comparados;
 *  - trocas: movimentações de elementos. Uma troca entre duas posições
 *    conta 1; no Merge Sort, cada elemento copiado conta 1.
 */
public interface Ordenador {

    /** Ordena o vetor NO LUGAR, em ordem crescente. */
    void ordenar(double[] v);

    /** Comparações feitas desde o último resetar(). */
    long getComparacoes();

    /** Trocas/movimentações feitas desde o último resetar(). */
    long getTrocas();

    /** Zera os contadores. O benchmark chama antes de CADA execução. */
    void resetar();

    /** Nome para tabelas e gráficos, ex.: "QuickSort". */
    String nome();
}