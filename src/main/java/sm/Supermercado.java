package sm;

import esd.ListaSequencial;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Supermercado {

    HttpClient cliente;
    String url;
    final Pattern re_resources = Pattern.compile("(\\d+)-(\\d+)/(\\d+)", Pattern.CASE_INSENSITIVE);
    final int query_len = 40;
    CacheSupermercado cache;
    private boolean houveAtualizacaoDeCache = false;

    public boolean houveAtualizacaoDeCache() {
        return houveAtualizacaoDeCache;
    }

    /**
     * Reseta a flag de atualização de cache.
     * Deve ser chamado antes do início da iteração de uma nova lista de compras
     * para garantir que alertas de cache incompleto funcionem corretamente por mercado.
     */
    public void resetarStatusCache() {
        this.houveAtualizacaoDeCache = false;
    }

    public class Resultado implements Iterable<Produto> {
        String produto;
        int total;
        ListaSequencial<Produto> produtos;
        Supermercado sm;

        Resultado(Supermercado sm, String produto, ListaSequencial<Produto> produtos, int total) {
            this.produto = produto;
            this.produtos = produtos;
            this.total = total;
            this.sm = sm;
        }

        @Override
        public Iterador iterator() {
            return new Iterador(produtos, total);
        }

        public Stream<Produto> stream() {
            return StreamSupport.stream(new ResultIterator(this), false);
        }

        class ResultIterator implements Spliterator<Produto> {
            Iterador it;

            ResultIterator(Resultado res) {
                this.it = res.iterator();
            }

            public void forEachRemaining(Consumer<? super Produto> action) {
                while (it.hasNext()) {
                    action.accept(it.next());
                }
            }

            public boolean tryAdvance(Consumer<? super Produto> action) {
                if (it.hasNext()) {
                    action.accept(it.next());
                    return true;
                } else return false;
            }

            public Spliterator<Produto> trySplit() {
                return null;
            }

            public long estimateSize() {
                return (long) (it.total - it.inicio);
            }

            public int characteristics() {
                return ORDERED | SIZED | IMMUTABLE | SUBSIZED;
            }
        }

        class Iterador implements Iterator<Produto> {
            int total;
            int inicio = 0;
            ListaSequencial<Produto> produtos;

            Iterador(ListaSequencial<Produto> produtos, int total) {
                this.produtos = produtos;
                this.total = total;
            }

            @Override
            public boolean hasNext() {
                return total > inicio;
            }

            @Override
            public Produto next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("fim da iteração");
                }
                Produto prod = produtos.obtem(inicio++);
                if (inicio >= produtos.comprimento()) {
                    if (inicio < total) {
                        var mais_produtos = sm.busca_proximo(produto, inicio);
                        if (produtos != null && mais_produtos != null) {
                            for (int j = 0; j < mais_produtos.comprimento(); j++) {
                                Produto p = mais_produtos.obtem(j);
                                produtos.adiciona(p);
                                sm.adiciona(produto, p);
                            }
                        }
                    }
                }
                return prod;

            }
        }
    }

    public Supermercado(String url) {
        cliente = HttpClient.newHttpClient();
        this.url = url + "/api/catalog_system/pub/products/search/";
        this.cache = new CacheSupermercado(this.getClass().getSimpleName());
    }

    public void encerra() {
        this.cache.salvaCache();
    }

    public void adiciona(String termo, Produto prod) {
        this.cache.adiciona(termo, prod);
    }

    public boolean temCache() {
        return this.cache.temCache();
    }

    String make_url(String produto, int inicio) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.url);
        sb.append("?ft=");
        sb.append(URLEncoder.encode(produto, StandardCharsets.UTF_8));
        sb.append("&_from=");
        sb.append(Integer.toString(inicio));
        sb.append("&_to=");
        sb.append(Integer.toString(inicio + query_len - 1));

        return sb.toString();
    }

    String make_get_url(String... ids) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.url);
        sb.append("?");
        boolean naoPrimeiro = false;
        for (var produtoId : ids) {
            if (naoPrimeiro) sb.append("&");
            else naoPrimeiro = true;
            sb.append("fq=productId:");
            sb.append(produtoId);
        }

        return sb.toString();
    }

    HttpResponse<String> envia(String url) {
        HttpResponse<String> response = null;

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .setHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64; rv:140.0) Gecko/20100101 Firefox/140.0")
                    .build();

            try {
                response = cliente.send(req, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {

            }
        } catch (URISyntaxException e) {

        }

        return response;
    }

    ListaSequencial<Produto> extrai_produtos(HttpResponse<String> response) {
        ListaSequencial<Produto> r = new ListaSequencial<>();
        var headers = response.headers().map();
        boolean isJson = headers.get("content-type").stream().anyMatch(x -> x.startsWith("application/json"));
        if (isJson) {
            JSONArray jo = new JSONArray(response.body());
            for (var o : jo) {
                JSONObject obj = (JSONObject) o;
                r.adiciona(Produto.fromJson(obj));
            }
        }
        return r;
    }

    ListaSequencial<Produto> busca_proximo(String produto, int inicio) {
        HttpResponse<String> response = envia(make_url(produto, inicio));
        if (response != null) {
            int status = response.statusCode();
            if (status == 200 || status == 206) {
                return extrai_produtos(response);
            }
        }

        return null;
    }

    int[] obtem_info_paginas(HttpResponse<String> response) {
        var headers = response.headers().map();

        String faixa = headers.get("resources").getFirst();
        var m = re_resources.matcher(faixa);
        int paginas[] = {0, 9, 10};
        if (m.find()) {
            paginas[0] = Integer.parseInt(m.group(1));
            paginas[1] = Integer.parseInt(m.group(2));
            paginas[2] = Integer.parseInt(m.group(3));
            paginas[1] = Math.min(paginas[1], paginas[2]);
        }
        return paginas;
    }

    /**
     * Efetua a busca por produtos. Prioriza a verificação no cache local.
     * Caso o termo já tenha sido pesquisado, obtém preços atualizados pela API via ID.
     */
    public Resultado busca(String produto, ItemLista itemDesejado) {
        ListaSequencial<Produto> produtosCache = this.cache.buscarPorNome(produto);

        if (produtosCache != null) {
            if (produtosCache.esta_vazia()) return new Resultado(this, produto, produtosCache, 0);

            // Filtro local baseado no termo de  busca para ignorar produtos irrelevantes do cache
            ListaSequencial<String> ids = new ListaSequencial<>();
            String termoNormalizado = produto.toLowerCase().trim();

            for (int i = 0; i < produtosCache.comprimento(); i++) {
                Produto p = produtosCache.obtem(i);
                String nomeProd = p.getNome().toLowerCase();

                // Só atualiza se o nome do produto contiver o termo original da busca e o itemDesejado aceitar o produto
                if (nomeProd.contains(termoNormalizado) && itemDesejado.aceitaProduto(p)) {
                    ids.adiciona(p.getId());
                }
            }

            // Se o filtro reduziu a zero por preciosismo, usa a lista do cache original
            if (ids.esta_vazia()) {
                for (int i = 0; i < produtosCache.comprimento(); i++) {
                    ids.adiciona(produtosCache.obtem(i).getId());
                }
            }

            // Busca na API por ID apenas dos produtos que passaram na verificação
            ListaSequencial<Produto> res = this.obtem(ids);

            if (res != null && !res.esta_vazia()) {
                for (int i = 0; i < res.comprimento(); i++) {
                    this.cache.adiciona(produto, res.obtem(i));
                }
                return new Resultado(this, produto, res, res.comprimento());
            }

            return new Resultado(this, produto, produtosCache, produtosCache.comprimento());
        }

        // O termo NÃO EXISTE no cache
        HttpResponse<String> response = envia(make_url(produto, 0));
        if (response != null) {
            int status = response.statusCode();
            if (status == 200 || status == 206) {
                ListaSequencial<Produto> produtosBrutos = extrai_produtos(response);

                if (produtosBrutos.esta_vazia()) {
                    this.cache.adiciona(produto, null); // Salva cache negativo se não achar nada
                } else {
                    this.houveAtualizacaoDeCache = true;

                    for (int i = 0; i < produtosBrutos.comprimento(); i++) {
                        this.cache.adiciona(produto, produtosBrutos.obtem(i));
                    }

                    int[] faixa = obtem_info_paginas(response);
                    int total = faixa[2];

                    while (produtosBrutos.comprimento() < total) {
                        var maisProdutos = busca_proximo(produto, produtosBrutos.comprimento());
                        if (maisProdutos == null || maisProdutos.esta_vazia()) break;

                        for (int j = 0; j < maisProdutos.comprimento(); j++) {
                            Produto p = maisProdutos.obtem(j);
                            produtosBrutos.adiciona(p);
                            this.cache.adiciona(produto, p);
                        }
                    }
                }

                // Filtro local de segurança para o retorno da API baseado no termo
                ListaSequencial<Produto> produtosFiltrados = new ListaSequencial<>();
                String termoNormalizado = produto.toLowerCase().trim();
                for (int i = 0; i < produtosBrutos.comprimento(); i++) {
                    Produto p = produtosBrutos.obtem(i);
                    if (p.getNome().toLowerCase().contains(termoNormalizado) && itemDesejado.aceitaProduto(p)) {
                        produtosFiltrados.adiciona(p);
                    }
                }

                if (produtosFiltrados.esta_vazia()) {
                    return new Resultado(this, produto, produtosBrutos, produtosBrutos.comprimento());
                }

                return new Resultado(this, produto, produtosFiltrados, produtosFiltrados.comprimento());
            }
        }
        return null;
    }

    private static final int MAX_QUERY_LEN = 40;

    /**
     * Fatiador de lotes para API. Resolve os limites de itens por requisição da VTEX.
     */
    public ListaSequencial<Produto> obtem(String... ids) {
        ListaSequencial<Produto> res = new ListaSequencial<>();
        for (int j = 0; j < ids.length; j += Supermercado.MAX_QUERY_LEN) {
            var args = Arrays.copyOfRange(ids, j, Math.min(j + Supermercado.MAX_QUERY_LEN, ids.length));
            HttpResponse<String> response = envia(make_get_url(args));
            if (response != null) {
                int status = response.statusCode();
                if (status == 200 || status == 206) {
                    var prods = extrai_produtos(response);
                    for (int k = 0; k < prods.comprimento(); k++) {
                        res.adiciona(prods.obtem(k));
                    }
                }
            }
        }
        return res;
    }

    public ListaSequencial<Produto> obtem(ListaSequencial<String> ids) {
        String[] args = new String[ids.comprimento()];
        for (int j = 0; j < ids.comprimento(); j++) {
            args[j] = ids.obtem(j);
        }
        return obtem(args);
    }

    public Produto obtem(String produto_id) {
        Produto prod = null;

        HttpResponse<String> response = envia(make_get_url(produto_id));
        if (response != null) {
            int status = response.statusCode();
            if (status == 200) {
                var headers = response.headers().map();
                boolean isJson = headers.get("content-type").stream().anyMatch(x -> x.startsWith("application/json"));
                if (isJson) {
                    JSONArray jo = new JSONArray(response.body());
                    prod = Produto.fromJson(jo.getJSONObject(0));
                }
            }
        }

        return prod;
    }
}