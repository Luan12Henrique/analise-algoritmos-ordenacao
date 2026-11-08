package ordenacao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

/**
 * GeradorDeDados.java
 *
 * Responsável por preparar os dados de entrada dos benchmarks:
 * 1. carregarLen: lê o CSV de tornados e extrai a coluna "len"
 * (comprimento do trajeto, em milhas) para um vetor de double.
 * 2. gerarBase: (próxima etapa) monta a base de 1 milhão por amostragem
 * com reposição, com seed fixa.
 * 3. amostra: (próxima etapa) devolve um vetor no padrão pedido
 * (aleatório, ordenado ou reverso).
 *
 * Decisão: os valores len = 0 são MANTIDOS; só são descartadas linhas
 * com valor vazio ou que não seja número.
 */
public class GeradorDeDados {

    /** Valores reais de "len" lidos do CSV (antes da amostragem). */
    private double[] reais;

    /**
     * Lê o CSV e guarda a coluna "len". Também devolve o vetor lido.
     *
     * @param caminhoCsv caminho do arquivo, ex.: "data/tornado_full.csv"
     */
    public double[] carregarLen(String caminhoCsv) throws IOException {
        List<Double> valores = new ArrayList<>();
        int descartados = 0;

        try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoCsv))) {
            // 1) Cabeçalho: descobre em que posição está a coluna "len".
            String cabecalho = leitor.readLine();
            if (cabecalho == null) {
                throw new IOException("Arquivo vazio: " + caminhoCsv);
            }
            String[] nomes = cabecalho.split(",", -1);
            int posicaoLen = -1;
            for (int i = 0; i < nomes.length; i++) {
                if (nomes[i].trim().equals("len")) {
                    posicaoLen = i;
                    break;
                }
            }
            if (posicaoLen == -1) {
                throw new IllegalStateException("Coluna 'len' não encontrada.");
            }

            // 2) Demais linhas: pega o valor de "len" e converte para double.
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) {
                    continue; // linha em branco não conta como registro
                }
                String[] campos = linha.split(",", -1);
                try {
                    double v = Double.parseDouble(campos[posicaoLen].trim());
                    if (Double.isNaN(v) || v < 0) {
                        descartados++;
                    } else {
                        valores.add(v); // zero é aceito
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    descartados++; // valor vazio, texto ou linha curta demais
                }
            }
        }

        // 3) Converte a lista para double[] (os algoritmos usam vetor simples).
        reais = new double[valores.size()];
        for (int i = 0; i < reais.length; i++) {
            reais[i] = valores.get(i);
        }

        System.out.println("Registros lidos: " + reais.length
                + " | descartados: " + descartados);
        return reais;
    }

    /** Base de tamanho grande (ex.: 1 milhão), sorteada com reposição. */
    private double[] base;

    /**
     * Monta a base por amostragem COM reposição (bootstrap) a partir dos
     * valores reais. Mesma seed => mesma base, sempre.
     *
     * @param tamanho quantidade de elementos da base (ex.: 1_000_000)
     * @param seed    semente do gerador aleatório (usamos 42)
     */
    public void gerarBase(int tamanho, long seed) {
        if (reais == null) {
            throw new IllegalStateException("Chame carregarLen antes de gerarBase.");
        }
        Random sorteio = new Random(seed);
        base = new double[tamanho];
        for (int i = 0; i < tamanho; i++) {
            base[i] = reais[sorteio.nextInt(reais.length)];
        }
    }

    /**
     * Devolve uma CÓPIA NOVA com os 'tamanho' primeiros elementos da base,
     * organizada no padrão pedido. Os 10 mil e os 100 mil são, portanto,
     * os primeiros elementos da mesma base de 1 milhão.
     *
     * Arrays.sort é usado só para PREPARAR os padrões ORDENADO e REVERSO,
     * fora da medição de tempo (não é um dos algoritmos avaliados).
     */
    public double[] amostra(int tamanho, Padrao padrao) {
        if (base == null) {
            throw new IllegalStateException("Chame gerarBase antes de amostra.");
        }
        if (tamanho > base.length) {
            throw new IllegalArgumentException("Tamanho maior que a base.");
        }

        // Recorta os primeiros elementos e trabalha numa cópia.
        double[] copia = Arrays.copyOf(base, tamanho);

        switch (padrao) {
            case ALEATORIO:
                break; // fica na ordem em que foi sorteado
            case ORDENADO:
                Arrays.sort(copia);
                break;
            case REVERSO:
                Arrays.sort(copia);
                inverter(copia);
                break;
        }
        return copia;
    }

    /** Inverte o vetor no lugar, trocando as pontas até o meio. */
    private static void inverter(double[] v) {
        for (int i = 0, j = v.length - 1; i < j; i++, j--) {
            double temp = v[i];
            v[i] = v[j];
            v[j] = temp;
        }
    }
}