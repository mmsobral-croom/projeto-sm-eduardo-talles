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




