import esd.ListaSequencial;
import lombok.Getter;
import sm.Produto;

@Getter
public class Orcamento implements Comparable<Orcamento> {
    private final String supermercado;
    private final float total;
    private final boolean completo;
    private final ListaSequencial<Produto> itens;

    public Orcamento(String supermercado, float total, boolean completo, ListaSequencial<Produto> itens) {
        this.supermercado = supermercado;
        this.total = total;
        this.completo = completo;
        this.itens = itens;
    }

    @Override
    public int compareTo(Orcamento outro) {
        // Cestas completas sempre vêm antes de incompletas
        if (this.completo && !outro.completo) return -1;
        if (!this.completo && outro.completo) return 1;

        // Se ambos forem iguais no status, decide pelo menor preço
        return Float.compare(this.total, outro.total);
    }
}