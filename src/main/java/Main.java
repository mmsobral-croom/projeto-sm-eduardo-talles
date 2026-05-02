import esd.ListaSequencial;
import sm.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Orcamento implements Comparable<Orcamento> {
    String supermercado;
    float total;
    ListaSequencial<Produto> itens;

    public Orcamento(String supermercado, float total, ListaSequencial<Produto> itens) {
        this.supermercado = supermercado;
        this.total = total;
        this.itens = itens;
    }

    @Override
    public int compareTo(Orcamento outro) {
        // Compara os orçamentos com base no valor total (necessário para o ordena() funcionar)
        return Float.compare(this.total, outro.total);
    }
}

        void main() {

            ListaSequencial<String> ls = new ListaSequencial<>();
            File lista = new File("lista-compras.txt");
            lendoArquivo(lista, ls);

            //Usando a ListaSequencial para agrupar os supermercados
            // Isso evita a repetição manual de código para cada novo supermercado
            ListaSequencial<Supermercado> supermercados = new ListaSequencial<>();
            supermercados.adiciona(new Giassi());
            supermercados.adiciona(new Bistek());
            supermercados.adiciona(new Fort());

            //Lista para guardar os orçamentos gerados
            ListaSequencial<Orcamento> orcamentos = new ListaSequencial<>();

            //Laço de repetição dinâmico
            for (Supermercado sm : supermercados) {
                ListaSequencial<Produto> produtosEncontrados = pesquisandoProduto(ls, sm);
                float total = valorTotalCompra(produtosEncontrados);

                // Pega o nome da classe (Giassi, Bistek, Fort) para identificar o orçamento
                String nomeSm = sm.getClass().getSimpleName();

                orcamentos.adiciona(new Orcamento(nomeSm, total, produtosEncontrados));
            }

            // Ordenando tudo
            orcamentos.ordena();

            //Exibição do ranking e detalhamento da cesta mais barata
            System.out.println("=== RANKING DE PREÇOS ===");
            for (int i = 0; i < orcamentos.comprimento(); i++) {
                Orcamento orc = orcamentos.obtem(i);
                System.out.printf("%dº Lugar - %s: R$ %.2f\n", (i + 1), orc.supermercado, orc.total);
            }

            System.out.println("\n=== DETALHES DA CESTA MAIS BARATA ===");
            Orcamento vencedor = orcamentos.primeiro(); // Pega o primeiro da lista ordenada
            System.out.printf("Supermercado: %s (Total: R$ %.2f)\n", vencedor.supermercado, vencedor.total);

            for (Produto p : vencedor.itens) {
                if (p != null) {
                    System.out.printf("- %s: R$ %.2f\n", p.getNome(), p.getPreco());
                } else {
                    System.out.println("- Produto não encontrado nesta loja");
                }
            }
        }

        static void lendoArquivo(File lista, ListaSequencial<String> ls) {
            try (Scanner sc = new Scanner(lista)) {
                while (sc.hasNextLine()) {
                    String item = sc.nextLine();
                    ls.adiciona(item);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Arquivo não encontrado.");
            }
        }

        static ListaSequencial<Produto> pesquisandoProduto(ListaSequencial<String> produtos, Supermercado sp) {
            ListaSequencial<Produto> lista = new ListaSequencial<>();

            for (String produto : produtos) {
                float menorPrco = Float.MAX_VALUE;
                Produto item = null;

                Supermercado.Resultado busca = sp.busca(produto.toString());
                if (busca != null) {
                    for (Produto p : busca) {
                        if (p.getPreco() < menorPrco && p.isDisponivel()) {
                            menorPrco = p.getPreco();
                            item = p;
                        }
                    }
                }

                lista.adiciona(item);
            }
            return lista;
        }

        static float valorTotalCompra(ListaSequencial<Produto> produtos) {
            float valorTotal = 0;
            for (Produto produto : produtos) {
                if (produto != null) {
                    valorTotal += produto.getPreco();
                }
            }
            return valorTotal;
        }