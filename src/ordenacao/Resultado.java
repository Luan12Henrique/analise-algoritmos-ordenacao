package ordenacao;

/**
 * Resultado.java
 *
 * Registro de UMA execução de um algoritmo (uma linha do resultados.csv).
 * É criado pelo BenchmarkRunner e lido pela GUI e pelos gráficos.
 * Os atributos são final: depois de criado, o resultado não muda.
 */
public class Resultado {

    private final String algoritmo;
    private final int tamanho;
    private final Padrao padrao;
    private final int execucao; // número da repetição (1, 2, 3...)
    private final long tempoNs; // tempo só da ordenação, em nanossegundos
    private final long comparacoes;
    private final long trocas;
    private final boolean ordenado; // true se a saída foi verificada ordenada

    public Resultado(String algoritmo, int tamanho, Padrao padrao, int execucao,
            long tempoNs, long comparacoes, long trocas, boolean ordenado) {
        this.algoritmo = algoritmo;
        this.tamanho = tamanho;
        this.padrao = padrao;
        this.execucao = execucao;
        this.tempoNs = tempoNs;
        this.comparacoes = comparacoes;
        this.trocas = trocas;
        this.ordenado = ordenado;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public int getTamanho() {
        return tamanho;
    }

    public Padrao getPadrao() {
        return padrao;
    }

    public int getExecucao() {
        return execucao;
    }

    public long getTempoNs() {
        return tempoNs;
    }

    public long getComparacoes() {
        return comparacoes;
    }

    public long getTrocas() {
        return trocas;
    }

    public boolean isOrdenado() {
        return ordenado;
    }
}