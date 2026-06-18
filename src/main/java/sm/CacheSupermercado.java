package sm;

import esd.ListaSequencial;
import esd.TabHash;

import java.io.*;

public class CacheSupermercado {
    private TabHash<String, Produto> hash;
    String nomeArquivo;

    public CacheSupermercado(String nomeArquivo) {
        this.hash = new TabHash<>();
        this.nomeArquivo = "cache_" + nomeArquivo + ".txt";
        carregarCache();
    }

    public Produto buscarPorEan(String ean) {
        return hash.obtem(ean);
    }

    public void salvaCache() {
        try (PrintWriter out = new PrintWriter(new FileWriter(nomeArquivo))) {
            ListaSequencial<Produto> produtos = hash.valores();
            for (Produto p : produtos) {
                out.printf("%s;%s;%s;%.2f;%s;%b%n",
                        p.getId(), p.getNome(), p.getMarca(), p.getPreco(), p.getEan(), p.isDisponivel());
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
                String[] linhas = linha.split(";");
                Produto produto = Produto.builder()
                        .id(linhas[0])
                        .nome(linhas[1])
                        .marca(linhas[2])
                        .preco(Float.parseFloat(linhas[3]))
                        .ean(linhas[4])
                        .disponivel(Boolean.parseBoolean(linhas[5]))
                        .build();
                this.adicona(produto);
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar cache: " + e.getMessage());
        }
    }

    public void adicona(Produto prod) {
        if (prod == null) return;
        if (prod.getEan() != null) {
            this.hash.adiciona(prod.getEan(), prod);
        }
    }
}
