package sm;

import esd.ListaSequencial;
import esd.TabHash;

import java.io.*;

public class CacheSupermercado {
    private TabHash<String, ListaSequencial<Produto>> hash;
    String nomeArquivo;

    public CacheSupermercado(String nomeArquivo) {
        this.hash = new TabHash<>();
        this.nomeArquivo = "cache_" + nomeArquivo + ".txt";
        carregarCache();
    }

    public ListaSequencial<Produto> buscarPorNome(String nome) {
        return hash.obtem_ou_default(nome, null);
    }

    public void adicona(String termo, Produto prod) {
        if (prod == null || termo == null) return;

        ListaSequencial<Produto> produtos = hash.obtem_ou_default(termo, null);
        if (produtos == null) {
            produtos = new ListaSequencial<>();
            hash.adiciona(termo, produtos);
        }

        boolean jaExiste = false;
        for (Produto produto : produtos) {
            if (produto.getId().equals(prod.getId())) {
                jaExiste = true;
                break;
            }
        }

        if (!jaExiste) {
            produtos.adiciona(prod);
        }
    }

    public void salvaCache() {
        try (PrintWriter out = new PrintWriter(new FileWriter(nomeArquivo))) {
            ListaSequencial<String> termos = hash.chaves();
            for (String termo : termos) {
                ListaSequencial<Produto> produtos = hash.obtem(termo);

                for (Produto p : produtos) {
                    out.printf("%s;%s;%s;%s;%.2f;%s;%b%n",
                            termo, p.getId(), p.getNome(), p.getMarca(), p.getPreco(), p.getEan(), p.isDisponivel());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar cache: " + e.getMessage());
        }
    }

    public void carregarCache() {
        File file = new File(nomeArquivo);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] colunas = linha.split(";");
                if (colunas.length < 7) continue;

                Produto produto = Produto.builder()
                        .id(colunas[1])
                        .nome(colunas[2])
                        .marca(colunas[3])
                        .preco((Float.parseFloat(colunas[4].replace(",", "."))))
                        .ean(colunas[5])
                        .disponivel(Boolean.parseBoolean(colunas[6]))
                        .build();

                this.adicona(colunas[0], produto);
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar cache: " + e.getMessage());
        }
    }
}
