package sm;

import esd.ListaSequencial;
import esd.TabHash;

import java.io.*;

/**
 * Classe responsável por gerenciar o cache local de forma otimizada.
 * Utiliza uma arquitetura de Double Hash (Normalização) para máxima eficiência de memória:
 * 1. cacheBuscas: Mapeia o Termo Pesquisado (String) para uma Lista de IDs de Produtos.
 * 2. cacheProdutos: Mapeia o ID do Produto (String) para o Objeto Produto completo.
 */

public class CacheSupermercado {
    private TabHash<String, ListaSequencial<String>> cacheBuscas;
    private TabHash<String, Produto> cacheProdutos;
    private String nomeArquivo;

    // Construtor da classe CacheSupermercado
    public CacheSupermercado(String nomeArquivo) {
        this.cacheBuscas = new TabHash<>();
        this.cacheProdutos = new TabHash<>();
        this.nomeArquivo = "cache_" + nomeArquivo + ".txt";
        carregarCache();
    }

    /**
     * Busca os produtos no cache utilizando a arquitetura de Duplo Hash.
     */
    public ListaSequencial<Produto> buscarPorNome(String nome) {
        ListaSequencial<String> ids = cacheBuscas.obtem_ou_default(nome, null);

        // Se a lista de IDs é nula, a palavra nunca foi pesquisada
        if (ids == null) return null;

        ListaSequencial<Produto> produtos = new ListaSequencial<>();

        // Se achou IDs, recupera os objetos Produto completos no tempo O(1)
        for (String id : ids) {
            Produto p = cacheProdutos.obtem_ou_default(id, null);
            if (p != null) produtos.adiciona(p);
        }
        return produtos;
    }

    /**
     * Verifica se existe alguma informação carregada no cache.
     * @return true se o cache não estiver vazio.
     */
    public boolean temCache() {
        return !cacheBuscas.esta_vazia();
    }

    /**
     * Insere ou atualiza um termo e um produto no cache.
     * Evita duplicação salvando o produto apenas no hash de produtos.
     */
    public void adiciona(String termo, Produto prod) {
        if (termo == null) return;

        // 1. Garante que o termo exista no cache de buscas
        ListaSequencial<String> ids = cacheBuscas.obtem_ou_default(termo, null);
        if (ids == null) {
            ids = new ListaSequencial<>();
            cacheBuscas.adiciona(termo, ids);
        }

        // Se for 'Negative Cache' (termo pesquisado, mas API não retornou nada), encerramos aqui
        if (prod == null) return;

        // 2. Salva ou atualiza o produto no cache de produtos
        cacheProdutos.adiciona(prod.getId(), prod);

        // 3. Associa o ID do produto ao termo, sem duplicar IDs na lista
        boolean idJaExiste = false;
        for (String id : ids) {
            if (id.equals(prod.getId())) {
                idJaExiste = true;
                break;
            }
        }

        if (!idJaExiste) {
            ids.adiciona(prod.getId());
        }
    }

    /**
     * Salva o estado atual da memória nos arquivos de texto (.txt).
     * Divide os registros entre TERMO (mapeamento do que foi pesquisado) e PROD (dados reais de cada produto).
     */
    public void salvaCache() {
        try (PrintWriter out = new PrintWriter(new FileWriter(nomeArquivo))) {

            // Grava os termos e seus respectivos IDs
            ListaSequencial<String> termos = cacheBuscas.chaves();
            for (String termo : termos) {
                ListaSequencial<String> ids = cacheBuscas.obtem(termo);

                if (ids.esta_vazia()) {
                    out.printf("TERMO;%s;VAZIO%n", termo);
                } else {
                    StringBuilder idsStr = new StringBuilder();
                    for (int i = 0; i < ids.comprimento(); i++) {
                        idsStr.append(ids.obtem(i));
                        if (i < ids.comprimento() - 1) idsStr.append(",");
                    }
                    out.printf("TERMO;%s;%s%n", termo, idsStr.toString());
                }
            }

            // Grava os produtos consolidados (sem duplicação)
            ListaSequencial<Produto> produtosUnicos = cacheProdutos.valores();
            for (Produto p : produtosUnicos) {
                out.printf("PROD;%s;%s;%s;%.2f;%s;%b%n",
                        p.getId(), p.getNome(), p.getMarca(), p.getPreco(), p.getEan(), p.isDisponivel());
            }

        } catch (IOException e) {
            System.out.println("Erro ao salvar cache: " + e.getMessage());
        }
    }

    /**
     * Carrega as informações persistidas no arquivo .txt para a memória RAM.
     */
    public void carregarCache() {
        File file = new File(nomeArquivo);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] colunas = linha.split(";");

                // Se for um registro de TERMO, popula o cacheBuscas
                if (colunas[0].equals("TERMO")) {
                    String termo = colunas[1];
                    ListaSequencial<String> ids = new ListaSequencial<>();

                    if (!colunas[2].equals("VAZIO")) {
                        String[] idArray = colunas[2].split(",");
                        for (String id : idArray) {
                            ids.adiciona(id);
                        }
                    }
                    cacheBuscas.adiciona(termo, ids);
                }
                // Se for um registro de PROD, popula o cacheProdutos
                else if (colunas[0].equals("PROD")) {
                    if (colunas.length < 7) continue;

                    Produto produto = Produto.builder()
                            .id(colunas[1])
                            .nome(colunas[2])
                            .marca(colunas[3])
                            .preco((Float.parseFloat(colunas[4].replace(",", "."))))
                            .ean(colunas[5])
                            .disponivel(Boolean.parseBoolean(colunas[6]))
                            .build();

                    cacheProdutos.adiciona(produto.getId(), produto);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar cache: " + e.getMessage());
        }
    }
}