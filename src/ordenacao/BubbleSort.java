package ordenacao;

/**
 * BubbleSort.java
 *
 * Bubble Sort implementado pela equipe (sem usar sort da biblioteca).
 *
 * Ideia: percorre o vetor comparando vizinhos e trocando os que estão fora
 * de ordem; a cada passada o maior elemento restante vai para o fim.
 *
 * Parada antecipada: se uma passada não faz nenhuma troca, o vetor já está
 * ordenado e o laço termina. Por isso o melhor caso (já ordenado) é O(n).
 *
 * Estável: a comparação é estrita (>), então elementos iguais não trocam.
 *
 * Complexidade: melhor O(n); médio e pior O(n^2) (pior: ordem reversa).
 * Memória extra: O(1).
 *
 * Contadores: comparação = cada comparação v[j] > v[j+1]; troca = cada
 * troca de duas posições.
 */
public class BubbleSort implements Ordenador {

    private long comparacoes;
    private long trocas;

    @Override
    public void ordenar(double[] v) {
        int n = v.length;
        for (int passada = 0; passada < n - 1; passada++) {
            boolean trocou = false;

            // Depois de 'passada' passadas, os maiores já estão no fim.
            for (int j = 0; j < n - 1 - passada; j++) {
                comparacoes++;
                if (v[j] > v[j + 1]) {
                    double temp = v[j];
                    v[j] = v[j + 1];
                    v[j + 1] = temp;
                    trocas++;
                    trocou = true;
                }
            }

            if (!trocou) {
                break; // nenhuma troca: já está ordenado
            }
        }
    }

    @Override
    public long getComparacoes() {
        return comparacoes;
    }

    @Override
    public long getTrocas() {
        return trocas;
    }

    @Override
    public void resetar() {
        comparacoes = 0;
        trocas = 0;
    }

    @Override
    public String nome() {
        return "BubbleSort";
    }
}