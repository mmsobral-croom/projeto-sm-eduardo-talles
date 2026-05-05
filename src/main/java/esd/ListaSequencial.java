package esd;

import java.util.*;

public class ListaSequencial<T> implements Iterable<T> {

    private T[] area;
    private int len = 0;
    private final int defcap = 8;

    @SuppressWarnings("unchecked")
    public ListaSequencial() {
        area = (T[]) new Object[defcap];
    }

    @SuppressWarnings("unchecked")
    void expande() {
        // isto será usado quando for necessário expandir a capacidade da lista
        T[] aux = (T[]) new Object[capacidade() * 2];
        for (int i = 0; i < len; i++) {
            aux[i] = area[i];
        }
        area = aux;
    }

    public boolean esta_vazia() {
        // retorna true se lista estiver vazia, ou false caso contrário
        return len == 0;
    }

    public int capacidade() {
        // retorna um inteiro que representa a capacidade da lista
        return area.length;
    }

    public void adiciona(T elemento) {
        // adiciona um valor ao final da lista
        if (len == area.length) {
            expande();
        }
        area[len++] = elemento;
    }

    // TODO alterar o comportamento;
    public void insere(int indice, T elemento) {
        // insere "elemento" na posição "indice"
        // o valor que estava na posição "indice" deve ser movido para o final da lista
        // valores válidos de "indice" vão de 0 até comprimento da lista (inclusive)
        // se "indice" for o comprimento da lista, insere faz o mesmo que "adiciona"
        // dispara IndexOutOfBoundsException se "indice" for inválido
        if (indice < 0 || indice > len) {
            throw new IndexOutOfBoundsException("Índice invalido");
        }
        if (len == area.length) {
            expande();
        }
        for (int i = len; i > indice; i--) {
            area[i] = area[i - 1];
        }
        len++;
        area[indice] = elemento;
    }

    // TODO alterar o comportamento;
    public T remove(int indice) {
        // remove um valor da posição indicada pelo parâmetro "índice"
        // move uma posição para trás os valores das próximas posições
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        // retorna o valor que foi removido da lista

        if (indice < 0 || indice >= len) {
            throw new IndexOutOfBoundsException("Índice invalido");
        }
        T retorno = area[indice];

        for (int i = indice; i < len - 1; i++) {
            area[i] = area[i + 1];
        }
        len--;
        area[len] = null;
        return retorno;
    }

    public void remove(T valor) {
        int indice = procura(valor);

        if (indice != -1) {
            this.remove(indice);
        }
    }

    public int procura(T valor) {
        // retorna um inteiro que representa aposição onde valor foi encontrado pela primeira vez (contando do início da lista)
        // retorna -1 se não o encontrar !
        for (int i = 0; i < len; i++) {
            if (area[i].equals(valor)) {
                return i;
            }
        }
        return -1;
    }

    public T obtem(int indice) {
        // retorna o valor armazenado na posição indica pelo parâmetro "indice"
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        if (indice < 0 || indice >= len) {
            throw new IndexOutOfBoundsException("Índice invalido");
        }
        return area[indice];
    }

    public void substitui(int indice, T valor) {
        // armazena o valor na posição indicada por "indice", substituindo o valor lá armazenado atualmente
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        if (indice < 0 || indice >= len) {
            throw new IndexOutOfBoundsException("Índice invalido");
        }
        area[indice] = valor;
    }

    public int comprimento() {
        // retorna um inteiro que representa o comprimento da lista (quantos valores estão armazenados)
        return len;
    }

    public void insere_rapido(int indice, T elemento) {
        // insere um valor na posição indicada por "indice"
        // usa a abordagem de ListaSequencialSimples
        // dispara IndexOutOfBoundsException se "indice" for inválido
        if (indice < 0 || indice > len) {
            throw new IndexOutOfBoundsException("Índice invalido");
        }
        if (len == area.length) {
            expande();
        }
        if (indice < len) {
            area[len] = area[indice];
        }
        area[indice] = elemento;
        len++;

    }


    public boolean equals(ListaSequencial listaSequencial) {
        if (len != listaSequencial.comprimento()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (!area[i].equals(listaSequencial.area[i])) {
                return false;
            }
        }
        return true;
    }

    public T remove_rapido(int indice) {
        // remove um valor da posição indica pelo parãmetro índice
        // move o último dado da lista para essa posição
        // dispara IndexOutOfBoundsException se indice for inválido
        // retorna o valor que ofi removido da lista
        if (indice < 0 || indice >= len) {
            throw new IndexOutOfBoundsException("Índice invalido");
        }
        T retorno = area[indice];
        len--;
        if (indice < len) {
            area[indice] = area[len];
        }
        area[len] = null;

        return retorno;
    }

    public T remove_ultimo() {
        // remove o último valor da lista
        // disparar uma exceção IndexOutOfBoundsException caso lista vazia
        // retorna o valor que foi removido da lista
        if (len == 0) {
            throw new IndexOutOfBoundsException("Lista vazia");
        }
        T item = area[len - 1];
        area[--len] = null;
        return item;
    }

    public T primeiro() {
        // retorna o valor armazenado no início da lista
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        return area[0];
    }

    public T ultimo() {
        // retorna o valor armazenado no final da lista
        // disparar uma exceção IndexOutOfBoundsException caso posição seja inválida
        return area[len - 1];
    }

    public void limpa() {
        // esvazia a lista
        Arrays.fill(area, null);
        len = 0;
    }

    public void insere_ordenado(Comparable valor) {
        // insere o valor na lista, preservando seu ordenamento
        int posicao = 0;
        for (; posicao < len; posicao++) {
            int cmp = valor.compareTo(area[posicao]);
            if (cmp <= 0) break;
        }
        insere(posicao, (T) valor);
    }

    public int busca_binaria(Comparable valor) {
        // procura o valor dentro da lista usando busca binária
        // retorna a posição onde se encontra, ou -1 caso não exista
        int inicio = 0;
        int fim = len - 1;

        while (inicio <= fim) {
            int meio = inicio + (fim - inicio) / 2;

            int cmp = valor.compareTo(area[meio]);

            if (cmp == 0) {
                return meio; // Encontrou o valor
            } else if (cmp < 0) {
                fim = meio - 1; // O valor está na metade da esquerda
            } else {
                inicio = meio + 1; // O valor está na metade da direita
            }
        }

        return -1; // Valor não encontrado após esgotar o espaço de busca
    }

    public boolean esta_ordenada() {
        boolean ok = true;
        if (len > 1) {
            for (int pos = 1; ok && pos < len; pos++) {
                Comparable val = (Comparable) area[pos - 1];
                ok = val.compareTo(area[pos]) <= 0;
            }
        }
        return ok;
    }

    public void embaralha() {
        if (len > 1) {
            Random random = new Random();
            for (int j = len; j > len; j--) {
                int pos = random.nextInt(0, j + 1);
                swap(j, pos);
            }
        }
    }

    public void ordena() {
        this.ordenaMescla(0, len);
    }

    public void ordenaMescla(int pos1, int pos2) {
        if ((pos2 - pos1) > 1) {
            int meio = pos1 + (pos2 - pos1) / 2;
            ordenaMescla(pos1, meio);
            ordenaMescla(meio, pos2);

            mescla(pos1, meio, pos2);
        }
    }

    @SuppressWarnings("unchecked")
    private void mescla(int pos1, int meio, int pos2) {
        T[] aux = (T[]) new Object[pos2 - pos1];
        int i = pos1;
        int j = meio;
        int k = 0;

        while ((i < meio) && (j < pos2)) {
            Comparable valorI = (Comparable) area[i];
            Comparable valorJ = (Comparable) area[j];
            if (valorI.compareTo(valorJ) <= 0) {
                aux[k] = area[i];
                i = i + 1;
            } else {
                aux[k] = area[j];
                j = j + 1;
            }
            k++;
        }

        while (i < meio ) {
            aux[k] = area[i];
            i++;
            k++;
        }

        while (j < pos2) {
            aux[k] = area[j];
            j++;
            k++;
        }

        for (int l = pos1; l < pos2; l++) {
            area[l] = aux[l - pos1];
        }
    }

    private void swap(int pos1, int pos2) {
        if (pos1 != pos2) {
            T val = area[pos1];
            area[pos1] = area[pos2];
            area[pos2] = val;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ListaSimplesIterator(this);
    }

    public class ListaSimplesIterator implements Iterator<T> {

        ListaSequencial<T> l;
        int pos;

        public ListaSimplesIterator(ListaSequencial<T> l) {
            this.l = l;
            this.pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < l.comprimento();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("fim da iteração");
            }
            return l.obtem(pos++);
        }
    }
}
