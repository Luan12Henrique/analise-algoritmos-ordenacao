package ordenacao;

/**
 * Resultado.java
 *
 * Registro de UMA execução de um algoritmo (uma linha do resultados.csv).
 * É criado pelo BenchmarkRunner e lido pela GUI e pelos gráficos.
 * Os atributos são final: depois de criado, o resultado não muda.
 *
 * status: "OK" (execução medida) ou "PULADO_LIMITE" (combinação não
 * executada por passar do tempo máximo; nesse caso os números não existem).
 */
public class Resultado {

    public static final String OK = "OK";
    public static final String PULADO_LIMITE = "PULADO_LIMITE";

    private final String algoritmo;
    private final int tamanho;
    private final Padrao padrao;
    private final int execucao; // número da repetição (1, 2, 3...)
    private final long tempoNs; // tempo só da ordenação, em nanossegundos
    private final long comparacoes;
    private final long trocas;
    private final boolean ordenado; // true se a saída foi verificada ordenada
    private final String status;

    /** Execução medida (status OK). */
    public Resultado(String algoritmo, int tamanho, Padrao padrao, int execucao,
            long tempoNs, long comparacoes, long trocas, boolean ordenado) {
        this(algoritmo, tamanho, padrao, execucao, tempoNs, comparacoes,
                trocas, ordenado, OK);
    }

    public Resultado(String algoritmo, int tamanho, Padrao padrao, int execucao,
            long tempoNs, long comparacoes, long trocas, boolean ordenado,
            String status) {
        this.algoritmo = algoritmo;
        this.tamanho = tamanho;
        this.padrao = padrao;
        this.execucao = execucao;
        this.tempoNs = tempoNs;
        this.comparacoes = comparacoes;
        this.trocas = trocas;
        this.ordenado = ordenado;
        this.status = status;
    }

    /** Combinação não executada (limite de tempo): não tem números medidos. */
    public static Resultado pulado(String algoritmo, int tamanho, Padrao padrao) {
        return new Resultado(algoritmo, tamanho, padrao, 0, 0, 0, 0, false,
                PULADO_LIMITE);
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

    public String getStatus() {
        return status;
    }

    public boolean isPulado() {
        return PULADO_LIMITE.equals(status);
    }
}