package ordenacao;

/**
 * MergeSort.java
 *
 * Merge Sort implementado pela equipe (sem usar sort da biblioteca).
 *
 * Ideia: divide o vetor ao meio, ordena cada metade (recursão) e intercala
 * as duas metades ordenadas em uma só.
 *
 * Memória: usa UM vetor auxiliar do tamanho do vetor de entrada, criado uma
 * vez em ordenar() (custo extra O(n), ao contrário do Quick Sort).
 *
 * Estável: em empate, sai primeiro o elemento da metade esquerda.
 *
 * Complexidade: melhor, médio e pior caso O(n log n); memória extra O(n).
 *
 * Contadores: comparação = cada comparação entre dois elementos na
 * intercalação; troca (movimentação) = cada elemento escrito de volta no
 * vetor na intercalação. A cópia para o auxiliar NÃO conta. Esta unidade
 * não é a mesma do Quick Sort (troca de duas posições).
 */
public class MergeSort implements Ordenador {

    private long comparacoes;
    private long trocas;
    private double[] aux; // vetor auxiliar, do tamanho da entrada

    @Override
    public void ordenar(double[] v) {
        if (v.length < 2) {
            return;
        }
        aux = new double[v.length];
        dividir(v, 0, v.length - 1);
        aux = null; // libera a memória do auxiliar
    }

    /** Ordena v[baixo..alto]: ordena cada metade e depois intercala. */
    private void dividir(double[] v, int baixo, int alto) {
        if (baixo >= alto) {
            return; // um elemento só: já está ordenado
        }
        int meio = baixo + (alto - baixo) / 2;
        dividir(v, baixo, meio);
        dividir(v, meio + 1, alto);
        intercalar(v, baixo, meio, alto);
    }

    /** Junta v[baixo..meio] e v[meio+1..alto], já ordenadas, em v[baixo..alto]. */
    private void intercalar(double[] v, int baixo, int meio, int alto) {
        // Copia o trecho para o auxiliar (só cópia, não conta como troca).
        System.arraycopy(v, baixo, aux, baixo, alto - baixo + 1);

        int i = baixo; // próximo da metade esquerda
        int j = meio + 1; // próximo da metade direita

        for (int k = baixo; k <= alto; k++) {
            if (i > meio) {
                v[k] = aux[j++]; // esquerda acabou
            } else if (j > alto) {
                v[k] = aux[i++]; // direita acabou
            } else {
                comparacoes++;
                if (aux[j] < aux[i]) {
                    v[k] = aux[j++]; // direita é menor
                } else {
                    v[k] = aux[i++]; // esquerda <= direita (estável)
                }
            }
            trocas++; // um elemento escrito em v
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
        return "MergeSort";
    }
}