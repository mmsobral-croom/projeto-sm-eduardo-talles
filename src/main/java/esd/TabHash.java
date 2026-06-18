package esd;

import java.lang.reflect.Array;

public class TabHash <K, V> {
    public class Par {
        K chave;
        V valor;

        Par(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
        }

        public K obtemChave() {
            return chave;
        }

        public V obtemValor() {
            return valor;
        }

        @Override
        public boolean equals(Object outro) {
            Par _outro = (Par)outro;
            return chave.equals(_outro.chave);
        }
    }

    double maxFatorCarga = 1;
    ListaSequencial<Par>[] tab;
    int len = 0;
    final int defcap = 31;

    public TabHash() {
        tab = inicia_tabela(defcap);
    }

    @SuppressWarnings("unchecked")
    ListaSequencial<Par>[] inicia_tabela(int linhas) {
        return (ListaSequencial<Par>[]) Array.newInstance(ListaSequencial.class, linhas);
    }

    public double factor_carga() {
        double f = len;
        return f / tab.length;
    }

    public void setMaxFatorCarga(double fator) {
        if (fator <= 0) throw new IllegalArgumentException("O fator de carga deve ser maior que zero");
        this.maxFatorCarga = fator;
    }

    private void expande() {
        var old = tab;
        tab = inicia_tabela(2 * tab.length);
        len = 0;

        for (var lp: old) {
            if (lp != null) {
                for (int pos = 0; pos < lp.comprimento(); pos++) {
                    Par p = lp.obtem(pos);
                    adiciona(p.chave, p.valor);
                }
            }
        }
    }

    public void adiciona(K chave, V valor) throws IndexOutOfBoundsException {
        if (factor_carga() > maxFatorCarga) {
            expande();
        }

        int linha = Math.abs(chave.hashCode()) % tab.length;
        if (tab[linha] == null) {
            tab[linha] = new ListaSequencial<Par>();
        }

        ListaSequencial<Par> pares = tab[linha];
        for (int pos = 0; pos < pares.comprimento(); pos++) {
            Par p = pares.obtem(pos);
            if (chave.equals(p.chave)) {
                p.valor = valor;
                return;
            }
        }

        pares.adiciona(new Par(chave, valor));
        len++;
    }

    public V obtem(K chave) {
        int linha = Math.abs(chave.hashCode()) % tab.length;
        ListaSequencial<Par> pares = tab[linha];
        if (pares == null) {
            throw new IndexOutOfBoundsException("chave inexistente");
        }
        for (int pos = 0; pos < pares.comprimento(); pos++) {
            Par p = pares.obtem(pos);
            if (chave.equals(p.chave)) {
                return p.valor;
            }
        }
        throw new IndexOutOfBoundsException("chave inexistente");
    }

    public void remove(K chave) {
        int linha = Math.abs(chave.hashCode()) % tab.length;
        ListaSequencial<Par> pares = tab[linha];
        if (pares != null) {
            for (int pos = 0; pos < pares.comprimento(); pos++) {
                Par p = pares.obtem(pos);
                if (chave.equals(p.chave)) {
                    pares.remove(pos);
                    len--;
                    return;
                }
            }
        }
        throw new IndexOutOfBoundsException("chave inexistente");
    }

    public boolean contem(K chave) {
        int linha = Math.abs(chave.hashCode()) % tab.length;
        ListaSequencial<Par> pares = tab[linha];
        if (pares != null) {
            for (int pos = 0; pos < pares.comprimento(); pos++) {
                Par p = pares.obtem(pos);
                if (chave.equals(p.chave)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean esta_vazia() {
        return len == 0;
    }

    public V obtem_ou_default(K chave, V defval) {
        try {
            return obtem(chave);
        } catch (IndexOutOfBoundsException e) {
            return defval;
        }
    }

    public ListaSequencial<K> chaves() {
        ListaSequencial<K> lk = new ListaSequencial<>();

        for (ListaSequencial<Par> list : tab) {
            if (list != null) {
                for (int pos = 0; pos < list.comprimento(); pos++) {
                    lk.adiciona(list.obtem(pos).chave);
                }
            }
        }
        return lk;
    }

    public ListaSequencial<V> valores() {
        ListaSequencial<V> lv = new ListaSequencial<>();

        for (ListaSequencial<Par> pars : tab) {
            if (pars != null) {
                for (int pos = 0; pos < pars.comprimento(); pos++) {
                    lv.adiciona(pars.obtem(pos).valor);
                }
            }
        }
        return lv;
    }

    public ListaSequencial<Par> items() {
        ListaSequencial<Par> lp = new ListaSequencial<>();

        for (ListaSequencial<Par> pars : tab) {
            if (pars != null) {
                for (int pos = 0; pos < pars.comprimento(); pos++) {
                    lp.adiciona(pars.obtem(pos));
                }
            }
        }
        return lp;
    }

    public int comprimento() {
        return len;
    }

    public void limpa() {
        for (int i = 0; i < tab.length; i++) {
            tab[i] = null;
        }
        len = 0;
    }
}
