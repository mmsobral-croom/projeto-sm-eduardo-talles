import esd.ListaSequencial;
import sm.*;

void main() {

    // cria um acessador para o Giassi
    Giassi gs = new Giassi();
    Bistek bt = new Bistek();
    Fort ft = new Fort();

    ListaSequencial<String> ls = new ListaSequencial<>();

    File lista = new File("lista-compras.txt");
    lendoArquivo(lista, ls);

    float compraGiassi = valorTotalCompra(pesquisandoProduto(ls, gs));
    float compraBistek = valorTotalCompra(pesquisandoProduto(ls, bt));
    float compraFort = valorTotalCompra(pesquisandoProduto(ls, ft));

    float menorPreco = Math.min(compraGiassi, Math.min(compraBistek, compraFort));

    if (menorPreco == compraGiassi) {
        System.out.printf("O Giassi é o mais barato! O total da compra é %f\n", compraGiassi);
    } else if (menorPreco == compraBistek) {
        System.out.printf("O Bistek é o mais barato! O total da compra é %f\n", compraBistek);
    } else {
        System.out.printf("O Fort é o mais barato! O total da compra é %f\n", compraFort);
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


