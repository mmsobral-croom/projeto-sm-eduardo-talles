package sm;

import esd.ListaSequencial;
import lombok.Getter;

@Getter
public class Orcamento implements Comparable<Orcamento> {
    private final String supermercado;
    private final float total;
    private final boolean completo;
    private final ListaSequencial<Produto> itens;

    // Construtor da classe sm.Orcamento
    public Orcamento(String supermercado, float total, boolean completo, ListaSequencial<Produto> itens) {
        this.supermercado = supermercado;
        this.total = total;
        this.completo = completo;
        this.itens = itens;
    }

    /**
     * Define a regra de comparação para ordenação de orçamentos.
     * A lógica segue a seguinte prioridade:
     * 1. Cestas completas têm prioridade sobre cestas incompletas.
     * 2. Entre cestas com o mesmo status de completude, o critério de desempate é o menor valor total.
     *
     * @param outro O outro objeto sm.Orcamento a ser comparado.
     * @return Um inteiro negativo se este objeto for "menor",
     *         zero se forem iguais, ou um inteiro positivo se for "maior".
     */
    @Override
    public int compareTo(Orcamento outro) {
        // Cestas completas sempre vêm antes de incompletas
        if (this.completo && !outro.completo) return -1;
        if (!this.completo && outro.completo) return 1;

        // Se ambos forem iguais no status, decide pelo menor preço
        return Float.compare(this.total, outro.total);
    }
}