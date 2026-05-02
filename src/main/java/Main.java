import esd.ListaSequencial;
import sm.*;

void main() {

    // cria um acessador para o Giassi
    Giassi sm = new Giassi();
    Bistek bt = new Bistek();
    Fort ft = new Fort();

    ListaSequencial<String> ls = new ListaSequencial<>();

    File lista = new File("lista-compras.txt");
    lendoArquivo(lista, ls);

    pesquisandoProduto(ls, sm);
    pesquisandoProduto(ls, bt);
    pesquisandoProduto(ls, ft);


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




