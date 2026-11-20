package ordenacao;

/**
 * Partição: esquema de Hoare. Elementos iguais ao pivô param os índices e
 * são trocados, o que divide bem as chaves repetidas (a base de 1 milhão
 * tem muitos valores iguais).
 *
 * Recursão: só na parte MENOR; a parte maior vira um laço. Isso limita a
 * profundidade da pilha a cerca de log2(n).
 * 
 * Contadores: comparação = cada comparação entre elementos do vetor;
 * troca = cada troca de duas posições.
 */
public class QuickSort implements Ordenador {

    private long comparacoes;
    private long trocas;

    @Override
    public void ordenar(double[] v) {
        if (v.length > 1) {
            quick(v, 0, v.length - 1);
        }
    }

    /** Ordena v[baixo..alto]. Recursão na parte menor, laço na maior. */
    private void quick(double[] v, int baixo, int alto) {
        while (baixo < alto) {
            int j = particionar(v, baixo, alto);
            // Partes: [baixo..j] e [j+1..alto]
            if (j - baixo < alto - j) {
                quick(v, baixo, j); // esquerda é menor: recursão nela
                baixo = j + 1; // e continua o laço na direita
            } else {
                quick(v, j + 1, alto); // direita é menor: recursão nela
                alto = j; // e continua o laço na esquerda
            }
        }
    }

    /**
     * Partição de Hoare com pivô por mediana de três.
     * Devolve j tal que tudo em [baixo..j] <= tudo em [j+1..alto].
     */
    private int particionar(double[] v, int baixo, int alto) {
        int meio = baixo + (alto - baixo) / 2;

        // Escolhe a mediana e a coloca na primeira posição do trecho.
        int indiceMediana = medianaDeTres(v, baixo, meio, alto);
        if (indiceMediana != baixo) {
            trocar(v, baixo, indiceMediana);
        }
        double pivo = v[baixo];

        int i = baixo - 1;
        int j = alto + 1;
        while (true) {
            do {
                i++;
                comparacoes++;
            } while (v[i] < pivo); // para em elemento >= pivô

            do {
                j--;
                comparacoes++;
            } while (v[j] > pivo); // para em elemento <= pivô

            if (i >= j) {
                return j; // os índices se cruzaram
            }
            trocar(v, i, j);
        }
    }

    /** Devolve o ÍNDICE (entre a, b e c) do valor que está no meio. */
    private int medianaDeTres(double[] v, int a, int b, int c) {
        comparacoes++;
        if (v[a] < v[b]) {
            comparacoes++;
            if (v[b] < v[c]) {
                return b; // a < b < c
            }
            comparacoes++;
            return (v[a] < v[c]) ? c : a; // b é o maior
        } else {
            comparacoes++;
            if (v[a] < v[c]) {
                return a; // b <= a < c
            }
            comparacoes++;
            return (v[b] < v[c]) ? c : b; // a é o maior
        }
    }

    private void trocar(double[] v, int i, int j) {
        double temp = v[i];
        v[i] = v[j];
        v[j] = temp;
        trocas++;
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
        return "QuickSort";
    }
}