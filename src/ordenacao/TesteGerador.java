package ordenacao;

import java.util.Arrays;

/** TesteGerador.java: teste temporário do GeradorDeDados (será apagado). */
public class TesteGerador {
    public static void main(String[] args) throws Exception {
        GeradorDeDados g = new GeradorDeDados();
        g.carregarLen("data/tornado_full.csv");
        g.gerarBase(1_000_000, 42);

        for (Padrao p : Padrao.values()) {
            double[] a = g.amostra(10_000, p);
            System.out.println(p + " | tamanho " + a.length
                    + " | primeiros: " + Arrays.toString(Arrays.copyOf(a, 5))
                    + " | último: " + a[a.length - 1]);
        }

        // Repetibilidade: duas chamadas iguais devem dar os mesmos dados.
        boolean igual = Arrays.equals(g.amostra(10_000, Padrao.ALEATORIO),
                                      g.amostra(10_000, Padrao.ALEATORIO));
        System.out.println("Repetível: " + igual);
    }
}