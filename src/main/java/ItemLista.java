import esd.ListaSequencial;
import lombok.Getter;
import sm.Produto;
import java.text.Normalizer;

public class ItemLista {
    @Getter
    private String termoBusca;
    private final ListaSequencial<String> termosObrigatorios;
    private final ListaSequencial<String> marcasAceitaveis;

    // Construtor da classe ItemLista.
    public ItemLista(String termoBusca) {
        this.termoBusca = termoBusca;
        this.termosObrigatorios = new ListaSequencial<>();
        this.marcasAceitaveis = new ListaSequencial<>();
    }

    /**
     * Adiciona um termo à lista de palavras obrigatórias.
     * O termo é normalizado antes de ser armazenado para facilitar a comparação futura.
     *
     * @param termo Palavra ou expressão técnica (ex: "1kg", "desnatado").
     */
    public void adicionarObrigatorio(String termo) {
        this.termosObrigatorios.adiciona(normalizar(termo));
    }

    /**
     * Adiciona uma marca à lista de marcas aceitáveis.
     * A marca é normalizada para evitar divergências entre maiúsculas/minúsculas e acentos.
     *
     * @param marca Nome da marca desejada.
     */
    public void adicionarMarca(String marca) {
        this.marcasAceitaveis.adiciona(normalizar(marca));
    }

    /**
     * Método interno para normalizar strings.
     * Remove acentos, caracteres especiais, espaços excedentes e converte para minúsculas.
     *
     * @param texto Texto original a ser tratado.
     * @return String limpa e padronizada.
     */
    private String normalizar(String texto) {
        if (texto == null) return "";
        String limpo = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return limpo.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase().trim();
    }

    /**
     * Avalia se um produto específico atende a todos os critérios definidos neste item.
     * A validação ocorre em duas etapas:
     * 1. Verifica se todos os termos obrigatórios estão presentes no nome do produto.
     * 2. Verifica se o produto pertence a uma das marcas aceitáveis (caso existam marcas definidas).
     *
     * @param p O objeto Produto a ser validado.
     * @return "true" se o produto passar em todos os filtros; "false" caso contrário.
     */
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

        // Se o usuário não definiu marcas específicas, qualquer marca é aceita
        if (marcasAceitaveis.esta_vazia()) return true;

        // Verifica se a marca do produto ou o nome dele contém alguma das marcas aceitáveis
        for (int i = 0; i < marcasAceitaveis.comprimento(); i++) {
            String marcaDesejada = marcasAceitaveis.obtem(i);
            if (marcaNormalizada.equals(marcaDesejada) || nomeNormalizado.contains(marcaDesejada)) {
                return true;
            }
        }
        return false;
    }
}