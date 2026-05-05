import esd.ListaSequencial;
import lombok.Getter;
import sm.Produto;
import java.text.Normalizer;

public class ItemLista {
    @Getter
    private String termoBusca;
    private final ListaSequencial<String> termosObrigatorios;
    private final ListaSequencial<String> marcasAceitaveis;

    public ItemLista(String termoBusca) {
        this.termoBusca = termoBusca;
        this.termosObrigatorios = new ListaSequencial<>();
        this.marcasAceitaveis = new ListaSequencial<>();
    }

    public void adicionarObrigatorio(String termo) {
        this.termosObrigatorios.adiciona(normalizar(termo));
    }

    public void adicionarMarca(String marca) {
        this.marcasAceitaveis.adiciona(normalizar(marca));
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String limpo = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return limpo.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase().trim();
    }

    public boolean aceitaProduto(Produto p) {
        String nomeNormalizado = normalizar(p.getNome());
        String marcaNormalizada = normalizar(p.getMarca());

        // Verifica termos obrigatórios ignorando espaços (ex: "1kg" == "1 kg")
        for (int i = 0; i < termosObrigatorios.comprimento(); i++) {
            String termoSemEspacos = termosObrigatorios.obtem(i).replaceAll("\\s+", "");
            String nomeSemEspacos = nomeNormalizado.replaceAll("\\s+", "");

            if (!nomeSemEspacos.contains(termoSemEspacos)) {
                return false;
            }
        }

        if (marcasAceitaveis.esta_vazia()) return true;

        for (int i = 0; i < marcasAceitaveis.comprimento(); i++) {
            String marcaDesejada = marcasAceitaveis.obtem(i);
            if (marcaNormalizada.equals(marcaDesejada) || nomeNormalizado.contains(marcaDesejada)) {
                return true;
            }
        }
        return false;
    }
}