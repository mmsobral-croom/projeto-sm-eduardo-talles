import esd.ListaSequencial;
import lombok.Getter;
import sm.Produto;
import java.text.Normalizer;

/**
 * Representa um item desejado na cesta de compras, permitindo filtros avançados
 * como termos obrigatórios (ex: peso, volume) e marcas aceitáveis.
 */
public class ItemLista {
    @Getter
    private String termoBusca;
    private ListaSequencial<String> termosObrigatorios;
    private ListaSequencial<String> marcasAceitaveis;

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

    /**
     * Remove acentos e converte para minúsculo para garantir comparações exatas.
     */
    private String normalizar(String texto) {
        if (texto == null) return "";
        String limpo = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return limpo.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase().trim();
    }

    /**
     * Valida se o produto retornado pela API atende a todos os requisitos do usuário
     * (termos obrigatórios presentes no nome e correspondência de marca aceitável).
     */
    public boolean aceitaProduto(Produto p) {
        String nomeNormalizado = normalizar(p.getNome());
        String marcaNormalizada = normalizar(p.getMarca());

        // Verifica a presença de termos obrigatórios no nome
        for (int i = 0; i < termosObrigatorios.comprimento(); i++) {
            if (!nomeNormalizado.contains(termosObrigatorios.obtem(i))) {
                return false;
            }
        }

        // Valida as marcas (aceita qualquer uma se a lista estiver vazia)
        if (marcasAceitaveis.esta_vazia()) {
            return true;
        }

        for (int i = 0; i < marcasAceitaveis.comprimento(); i++) {
            String marcaDesejada = marcasAceitaveis.obtem(i);
            if (marcaNormalizada.equals(marcaDesejada) || nomeNormalizado.contains(marcaDesejada)) {
                return true;
            }
        }

        return false;
    }
}