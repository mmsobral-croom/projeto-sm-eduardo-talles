import esd.ListaSequencial;
import sm.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ListaSequencial<ItemLista> cestaDesejada = new ListaSequencial<>();
        File arquivo = new File("lista-compras.txt");
        lendoArquivo(arquivo, cestaDesejada);

        ListaSequencial<Supermercado> supermercados = new ListaSequencial<>();
        supermercados.adiciona(new Giassi());
        supermercados.adiciona(new Bistek());
        supermercados.adiciona(new Fort());

        ListaSequencial<Orcamento> orcamentos = new ListaSequencial<>();
        for (Supermercado sm : supermercados) {
            ListaSequencial<Produto> produtosEncontrados = pesquisandoProduto(cestaDesejada, sm);

            boolean cestaCompleta = true;
            float total = 0;
            for (Produto p : produtosEncontrados) {
                if (p == null) {
                    cestaCompleta = false;
                } else {
                    total += p.getPreco();
                }
            }

            orcamentos.adiciona(new Orcamento(sm.getClass().getSimpleName(), total, cestaCompleta, produtosEncontrados));
        }

        orcamentos.ordena();
        imprimirRanking(orcamentos);
        imprimirDetalhesVencedor(orcamentos.primeiro());
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Realiza a leitura do arquivo de texto e popula a lista de itens desejados.
     *
     * @param arquivo O objeto File que aponta para o arquivo .txt.
     * @param ls A lista sequencial onde os itens extraídos serão armazenados.
     */
    static void lendoArquivo(File arquivo, ListaSequencial<ItemLista> ls) {
        try (Scanner sc = new Scanner(arquivo)) {
            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                if (linha.trim().isEmpty()) continue;
                ls.adiciona(extrairItemDaLinha(linha));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo lista-compras.txt não encontrado.");
        }
    }

    /**
     * Processa uma linha do arquivo de texto e converte num objeto sm.ItemLista.
     * O formato esperado da linha é: "produto;obrigatorio;marca".
     *
     * @param linha String lida do arquivo.
     * @return Um objeto sm.ItemLista configurado com termos e restrições.
     */
    private static ItemLista extrairItemDaLinha(String linha) {
        String[] partes = linha.split(";");
        ItemLista item = new ItemLista(partes[0].trim());

        if (partes.length > 1 && !partes[1].trim().isEmpty()) {
            for (String req : partes[1].split(",")) item.adicionarObrigatorio(req);
        }
        if (partes.length > 2 && !partes[2].trim().isEmpty()) {
            for (String m : partes[2].split(",")) item.adicionarMarca(m);
        }
        return item;
    }

    /**
     * Pesquisa cada item da cesta num supermercado específico, selecionando
     * sempre o produto de menor preço que atenda aos critérios.
     *
     * @param itensDesejados Lista de itens que o usuário pretende comprar.
     * @param sp O supermercado onde a busca será realizada.
     * @return Uma lista de produtos representando a melhor combinação encontrada no estabelecimento.
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
     * Imprime no console o ranking de supermercados ordenados por preço.
     *
     * @param orcamentos Lista de orçamentos já processados e ordenados.
     */
    private static void imprimirRanking(ListaSequencial<Orcamento> orcamentos) {
        System.out.println("=== RANKING DE PREÇOS ===");
        for (int i = 0; i < orcamentos.comprimento(); i++) {
            Orcamento orc = orcamentos.obtem(i);
            String status = orc.isCompleto() ? "" : " (INCOMPLETA)";
            System.out.printf("%dº Lugar - %s%s: R$ %.2f\n", (i + 1), orc.getSupermercado(), status, orc.getTotal());
        }
    }

    /**
     * Exibe detalhadamente os itens, marcas e preços do supermercado
     * que apresentou o melhor orçamento.
     *
     * @param vencedor O orçamento com o menor valor total.
     */
    private static void imprimirDetalhesVencedor(Orcamento vencedor) {
        System.out.println("\n=== DETALHES DA MELHOR CESTA ===");
        String status = vencedor.isCompleto() ? "" : " [AVISO: ITENS FALTANTES]";
        System.out.printf("Supermercado: %s (Total: R$ %.2f)%s\n", vencedor.getSupermercado(), vencedor.getTotal(), status);
        for (Produto p : vencedor.getItens()) {
            if (p != null) System.out.printf("- %s (%s): R$ %.2f\n", p.getNome(), p.getMarca(), p.getPreco());
            else System.out.println("- [PRODUTO NÃO ENCONTRADO]");
        }
    }
}