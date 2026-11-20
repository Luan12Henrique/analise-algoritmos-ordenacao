package ordenacao;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleConsumer;

/**
 * BenchmarkRunner.java
 *
 * Roda os experimentos de ordenação e grava os resultados em CSV.
 *
 * Modo oficial (sem GUI): java -cp bin ordenacao.BenchmarkRunner
 * grava results/resultados.csv, results/resumo.csv e results/ambiente.txt.
 * A GUI usa a classe pelo construtor, com um callback de progresso.
 *
 * Procedimento (descrito no artigo):
 * - warm-up: cada algoritmo roda 'aquecimentos' vezes em cada padrão, no
 * menor tamanho, sem gravar nada (o JIT compila antes da medição);
 * - para cada (tamanho, padrão) a entrada é gerada UMA vez e cada execução
 * recebe uma cópia: todos os algoritmos recebem os mesmos dados;
 * - só a chamada a ordenar() é cronometrada; cópia, System.gc() e
 * verificação ficam fora da medição;
 * - a cada execução a saída é comparada com a referência ordenada;
 * - limite dos quadráticos: se o tempo PROJETADO (tempo do tamanho anterior
 * x (n/anterior)^2) passar do limite, a combinação é pulada
 * (PULADO_LIMITE). Nada é extrapolado como medição.
 */
public class BenchmarkRunner {

    private final GeradorDeDados gerador;
    private final List<Ordenador> ordenadores;
    private final int[] tamanhos; // em ordem crescente
    private final int repeticoes;
    private final int aquecimentos;
    private final long limiteNs; // tempo máximo (projetado) por execução
    private final Set<String> comLimite; // nomes dos algoritmos quadráticos
    private final DoubleConsumer progresso; // recebe 0.0 a 1.0 (pode ser null)

    public BenchmarkRunner(GeradorDeDados gerador, List<Ordenador> ordenadores,
            int[] tamanhos, int repeticoes, int aquecimentos,
            long limiteNs, Set<String> comLimite,
            DoubleConsumer progresso) {
        this.gerador = gerador;
        this.ordenadores = ordenadores;
        this.tamanhos = tamanhos;
        this.repeticoes = repeticoes;
        this.aquecimentos = aquecimentos;
        this.limiteNs = limiteNs;
        this.comLimite = comLimite;
        this.progresso = progresso;
    }

    /**
     * Guarda o último resultado de (algoritmo, padrão) para projetar o próximo
     * tamanho.
     */
    private static class Ultimo {
        final int tamanho;
        final double mediaNs;
        final boolean pulado;

        Ultimo(int tamanho, double mediaNs, boolean pulado) {
            this.tamanho = tamanho;
            this.mediaNs = mediaNs;
            this.pulado = pulado;
        }
    }

    /** Executa todos os experimentos e devolve todas as linhas de resultado. */
    public List<Resultado> executar() {
        List<Resultado> resultados = new ArrayList<>();
        Map<String, Ultimo> ultimos = new HashMap<>();
        int total = tamanhos.length * Padrao.values().length * ordenadores.size();
        int feitos = 0;

        aquecer();

        for (int n : tamanhos) {
            // Mesmos elementos da entrada, já ordenados: serve para verificar.
            double[] referencia = gerador.amostra(n, Padrao.ORDENADO);

            for (Padrao padrao : Padrao.values()) {
                double[] entrada = gerador.amostra(n, padrao);

                for (Ordenador o : ordenadores) {
                    if (Thread.currentThread().isInterrupted()) {
                        return resultados; // cancelado (ex.: pela GUI)
                    }
                    String chave = o.nome() + "|" + padrao;

                    if (deveSerPulado(o, chave, n, ultimos)) {
                        resultados.add(Resultado.pulado(o.nome(), n, padrao));
                        ultimos.put(chave, new Ultimo(n, 0, true));
                    } else {
                        List<Resultado> execucoes = medir(o, n, padrao, entrada, referencia);
                        resultados.addAll(execucoes);
                        ultimos.put(chave, new Ultimo(n, mediaTempo(execucoes), false));
                    }

                    feitos++;
                    if (progresso != null) {
                        progresso.accept((double) feitos / total);
                    }
                }
            }
        }
        return resultados;
    }

    /**
     * Warm-up: roda cada algoritmo em cada padrão, no menor tamanho, sem gravar.
     */
    private void aquecer() {
        int n = tamanhos[0];
        for (Ordenador o : ordenadores) {
            for (Padrao padrao : Padrao.values()) {
                double[] modelo = gerador.amostra(n, padrao);
                for (int i = 0; i < aquecimentos; i++) {
                    o.ordenar(modelo.clone());
                }
            }
            o.resetar();
        }
    }

    /** Decide se a combinação passa do limite (só para algoritmos quadráticos). */
    private boolean deveSerPulado(Ordenador o, String chave, int n,
            Map<String, Ultimo> ultimos) {
        if (!comLimite.contains(o.nome())) {
            return false;
        }
        Ultimo u = ultimos.get(chave);
        if (u == null) {
            return false; // menor tamanho: sempre executa
        }
        if (u.pulado) {
            return true; // tamanho menor já foi pulado
        }
        double razao = (double) n / u.tamanho;
        double projetado = u.mediaNs * razao * razao; // crescimento quadrático
        return projetado > limiteNs;
    }

    /**
     * Roda as repetições de um (algoritmo, tamanho, padrão) e verifica cada uma.
     */
    private List<Resultado> medir(Ordenador o, int n, Padrao padrao,
            double[] entrada, double[] referencia) {
        List<Resultado> lista = new ArrayList<>();
        for (int r = 1; r <= repeticoes; r++) {
            double[] copia = entrada.clone(); // fora da medição
            o.resetar();
            System.gc(); // fora da medição

            long inicio = System.nanoTime();
            o.ordenar(copia); // única coisa cronometrada
            long tempo = System.nanoTime() - inicio;

            boolean correto = Arrays.equals(copia, referencia);
            if (!correto) {
                System.err.println("ERRO: " + o.nome() + " nao ordenou corretamente ("
                        + n + ", " + padrao + ", execucao " + r + ")");
            }
            lista.add(new Resultado(o.nome(), n, padrao, r, tempo,
                    o.getComparacoes(), o.getTrocas(), correto));
        }
        return lista;
    }

    private static double mediaTempo(List<Resultado> execucoes) {
        double[] tempos = new double[execucoes.size()];
        for (int i = 0; i < tempos.length; i++) {
            tempos[i] = execucoes.get(i).getTempoNs();
        }
        return Estatistica.media(tempos);
    }

    // ------------------------------------------------------------------
    // Gravação dos arquivos
    // ------------------------------------------------------------------

    /** Grava resultados.csv, resumo.csv e ambiente.txt na pasta indicada. */
    public static void gravar(List<Resultado> resultados, Path pasta) throws IOException {
        Files.createDirectories(pasta);
        gravarResultados(resultados, pasta.resolve("resultados.csv"));
        gravarResumo(resultados, pasta.resolve("resumo.csv"));
        Files.writeString(pasta.resolve("ambiente.txt"), descreverAmbiente());
    }

    /** Uma linha por execução. Combinações puladas ficam com campos vazios. */
    private static void gravarResultados(List<Resultado> resultados, Path arquivo)
            throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(arquivo))) {
            out.println("algoritmo,tamanho,padrao,execucao,tempo_ns,comparacoes,trocas,ordenado,status");
            for (Resultado r : resultados) {
                if (r.isPulado()) {
                    out.println(String.join(",", r.getAlgoritmo(),
                            String.valueOf(r.getTamanho()), r.getPadrao().name(),
                            "", "", "", "", "", r.getStatus()));
                } else {
                    out.println(String.join(",", r.getAlgoritmo(),
                            String.valueOf(r.getTamanho()), r.getPadrao().name(),
                            String.valueOf(r.getExecucao()),
                            String.valueOf(r.getTempoNs()),
                            String.valueOf(r.getComparacoes()),
                            String.valueOf(r.getTrocas()),
                            String.valueOf(r.isOrdenado()), r.getStatus()));
                }
            }
        }
    }

    /** Uma linha por (algoritmo, tamanho, padrão): média e desvio dos tempos. */
    private static void gravarResumo(List<Resultado> resultados, Path arquivo)
            throws IOException {
        Map<String, List<Resultado>> grupos = new LinkedHashMap<>();
        for (Resultado r : resultados) {
            String chave = r.getAlgoritmo() + "," + r.getTamanho() + "," + r.getPadrao();
            grupos.computeIfAbsent(chave, k -> new ArrayList<>()).add(r);
        }

        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(arquivo))) {
            out.println(
                    "algoritmo,tamanho,padrao,execucoes,media_ns,desvio_ns,comparacoes,trocas,todas_corretas,status");
            for (Map.Entry<String, List<Resultado>> e : grupos.entrySet()) {
                List<Resultado> grupo = e.getValue();
                if (grupo.get(0).isPulado()) {
                    out.println(e.getKey() + ",0,,,,,," + Resultado.PULADO_LIMITE);
                    continue;
                }
                double[] tempos = new double[grupo.size()];
                boolean todasCorretas = true;
                for (int i = 0; i < tempos.length; i++) {
                    tempos[i] = grupo.get(i).getTempoNs();
                    todasCorretas &= grupo.get(i).isOrdenado();
                }
                // Comparações e trocas são iguais em todas as repetições
                // (mesmos dados, algoritmo determinístico): usa a última.
                Resultado ultima = grupo.get(grupo.size() - 1);
                out.println(e.getKey() + "," + grupo.size()
                        + "," + String.format(Locale.US, "%.1f", Estatistica.media(tempos))
                        + "," + String.format(Locale.US, "%.1f", Estatistica.desvioPadrao(tempos))
                        + "," + ultima.getComparacoes()
                        + "," + ultima.getTrocas()
                        + "," + todasCorretas
                        + "," + Resultado.OK);
            }
        }
    }

    /**
     * Descrição do ambiente de testes (o modelo do processador e a RAM total são
     * anotados à mão).
     */
    public static String descreverAmbiente() {
        Runtime rt = Runtime.getRuntime();
        return "Sistema operacional: " + System.getProperty("os.name") + " "
                + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")\n"
                + "Java: " + System.getProperty("java.version") + " ("
                + System.getProperty("java.vm.name") + ")\n"
                + "Processadores logicos: " + rt.availableProcessors() + "\n"
                + "Memoria maxima da JVM: " + rt.maxMemory() / (1024 * 1024) + " MB\n"
                + "(Modelo do processador e RAM total: anotar manualmente)\n";
    }

    // ------------------------------------------------------------------
    // Modo oficial, por linha de comando (sem GUI)
    // ------------------------------------------------------------------

    /**
     * Sem argumentos: experimento completo, grava em results/.
     * Com o argumento "rapido": tamanhos pequenos e 2 repetições, grava em
     * results/rapido/ (só para testar; não entra nos números do artigo).
     */
    public static void main(String[] args) throws Exception {
        boolean rapido = args.length > 0 && args[0].equals("rapido");

        GeradorDeDados gerador = new GeradorDeDados();
        gerador.carregarLen("data/tornado_full.csv");
        gerador.gerarBase(1_000_000, 42);

        List<Ordenador> ordenadores = new ArrayList<>();
        ordenadores.add(new QuickSort());
        ordenadores.add(new MergeSort());
        // Quando a Aninha entregar, acrescentar:
        // ordenadores.add(new BubbleSort());
        // ordenadores.add(new SelectionSort());

        int[] tamanhos = rapido ? new int[] { 1_000, 10_000 }
                : new int[] { 10_000, 100_000, 1_000_000 };
        int repeticoes = rapido ? 2 : 5;
        long limiteNs = 60_000_000_000L; // 60 s por execução (projetado)
        Set<String> comLimite = Set.of("BubbleSort", "SelectionSort");

        System.out.println(descreverAmbiente());

        BenchmarkRunner runner = new BenchmarkRunner(gerador, ordenadores, tamanhos,
                repeticoes, 3, limiteNs, comLimite,
                p -> System.out.printf("Progresso: %.0f%%%n", p * 100));
        List<Resultado> resultados = runner.executar();

        Path pasta = rapido ? Paths.get("results", "rapido") : Paths.get("results");
        gravar(resultados, pasta);
        System.out.println("Gravado em " + pasta.toAbsolutePath());
    }
}