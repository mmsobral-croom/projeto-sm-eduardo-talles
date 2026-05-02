import esd.ListaSequencial;
import sm.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // 1. Carrega a cesta desejada a partir do arquivo de texto
        ListaSequencial<ItemLista> cestaDesejada = new ListaSequencial<>();
        File arquivo = new File("lista-compras.txt");
        lendoArquivo(arquivo, cestaDesejada);

        // 2. Prepara a lista de supermercados que serão consultados
        ListaSequencial<Supermercado> supermercados = new ListaSequencial<>();
        supermercados.adiciona(new Giassi());
        supermercados.adiciona(new Bistek());
        supermercados.adiciona(new Fort());

        // 3. Consulta os produtos e gera os orçamentos para cada supermercado
        ListaSequencial<Orcamento> orcamentos = new ListaSequencial<>();
        for (Supermercado sm : supermercados) {
            ListaSequencial<Produto> produtosEncontrados = pesquisandoProduto(cestaDesejada, sm);
            float total = valorTotalCompra(produtosEncontrados);
            String nomeSm = sm.getClass().getSimpleName();

            orcamentos.adiciona(new Orcamento(nomeSm, total, produtosEncontrados));
        }

        // 4. Ordena os orçamentos do menor para o maior preço
        orcamentos.ordena();

        // 5. Exibe o ranking final de preços
        System.out.println("=== RANKING DE PREÇOS ===");
        for (int i = 0; i < orcamentos.comprimento(); i++) {
            Orcamento orc = orcamentos.obtem(i);
            System.out.printf("%dº Lugar - %s: R$ %.2f\n", (i + 1), orc.getSupermercado(), orc.getTotal());
        }

        // 6. Detalha os itens da cesta mais barata
        System.out.println("\n=== DETALHES DA MELHOR CESTA ===");
        Orcamento vencedor = orcamentos.primeiro();
        System.out.printf("Supermercado: %s (Total: R$ %.2f)\n", vencedor.getSupermercado(), vencedor.getTotal());

        for (Produto p : vencedor.getItens()) {
            if (p != null) {
                System.out.printf("- %s (%s): R$ %.2f\n", p.getNome(), p.getMarca(), p.getPreco());
            } else {
                System.out.println("- [ITEM NÃO ENCONTRADO/DISPONÍVEL NOS CRITÉRIOS]");
            }
        }
    }

    /**
     * Lê o arquivo de entrada e delega a criação dos itens para conversão da linha.
     * Ignora linhas em branco.
     */
    static void lendoArquivo(File arquivo, ListaSequencial<ItemLista> ls) {
        try (Scanner sc = new Scanner(arquivo)) {
            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                if (linha.trim().isEmpty()) continue;

                ItemLista item = extrairItemDaLinha(linha);
                ls.adiciona(item);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo lista-compras.txt não encontrado.");
        }
    }

    /**
     * Converte uma linha de texto no formato "busca ; obrigatorios ; marcas"
     * em um objeto ItemLista devidamente configurado para os filtros da busca.
     */
    private static ItemLista extrairItemDaLinha(String linha) {
        String[] partes = linha.split(";");
        ItemLista item = new ItemLista(partes[0].trim());

        if (partes.length > 1 && !partes[1].trim().isEmpty()) {
            String[] obrigatorios = partes[1].split(",");
            for (String req : obrigatorios) {
                item.adicionarObrigatorio(req);
            }
        }

        if (partes.length > 2 && !partes[2].trim().isEmpty()) {
            String[] marcas = partes[2].split(",");
            for (String m : marcas) {
                item.adicionarMarca(m);
            }
        }

        return item;
    }

    /**
     * Busca os itens na API do supermercado e seleciona a opção mais barata
     * que atenda simultaneamente a todos os critérios exigidos pelo usuário.
     */
    static ListaSequencial<Produto> pesquisandoProduto(ListaSequencial<ItemLista> itensDesejados, Supermercado sp) {
        ListaSequencial<Produto> resultadoCesta = new ListaSequencial<>();

        for (ItemLista itemDesejado : itensDesejados) {
            float menorPreco = Float.MAX_VALUE;
            Produto melhorOpcao = null;

            Supermercado.Resultado busca = sp.busca(itemDesejado.getTermoBusca());
            if (busca != null) {
                for (Produto p : busca) {
                    if (p.isDisponivel() && itemDesejado.aceitaProduto(p)) {
                        if (p.getPreco() < menorPreco) {
                            menorPreco = p.getPreco();
                            melhorOpcao = p;
                        }
                    }
                }
            }
            resultadoCesta.adiciona(melhorOpcao);
        }
        return resultadoCesta;
    }

    /**
     * Retorna o valor total da cesta somando o preço de cada produto encontrado.
     */
    static float valorTotalCompra(ListaSequencial<Produto> produtos) {
        float valorTotal = 0;
        for (Produto produto : produtos) {
            if (produto != null) {
                valorTotal += produto.getPreco();
            }
        }
        return valorTotal;
    }
} 