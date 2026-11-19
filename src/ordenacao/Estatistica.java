package ordenacao;

/**
 * Estatistica.java
 *
 * Média e desvio-padrão dos tempos das repetições de um experimento.
 * Usado pelo BenchmarkRunner para gerar o resumo.csv, e pode ser usado
 * pela GUI.
 *
 * Desvio-padrão AMOSTRAL (divide por n-1): as repetições são uma amostra
 * das execuções possíveis. Com uma única execução, o desvio é 0.
 */
public final class Estatistica {

    private Estatistica() {
    } // classe só com métodos estáticos

    public static double media(double[] x) {
        double soma = 0;
        for (double valor : x) {
            soma += valor;
        }
        return soma / x.length;
    }

    public static double desvioPadrao(double[] x) {
        if (x.length < 2) {
            return 0;
        }
        double m = media(x);
        double somaQuadrados = 0;
        for (double valor : x) {
            somaQuadrados += (valor - m) * (valor - m);
        }
        return Math.sqrt(somaQuadrados / (x.length - 1));
    }
}