import esd.ListaSequencial;
import sm.Produto;

/**
 * Encapsula o resultado da cotação de uma cesta em um supermercado específico.
 * Implementa Comparable para permitir a ordenação automática pelo menor preço.
 */
public class Orcamento implements Comparable<Orcamento> {
    private String supermercado;
    private float total;
    private ListaSequencial<Produto> itens;

    public Orcamento(String supermercado, float total, ListaSequencial<Produto> itens) {
        this.supermercado = supermercado;
        this.total = total;
        this.itens = itens;
    }

    public String getSupermercado() {
        return supermercado;
    }

    public float getTotal() {
        return total;
    }

    public ListaSequencial<Produto> getItens() {
        return itens;
    }

    @Override
    public int compareTo(Orcamento outro) {
        return Float.compare(this.total, outro.total);
    }
}