package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Grafo com vértice genéricos implementado por uma lista encadeada de
 * adjacência, com quantidade de vertices dinâmica.
 * <p>
 * As arestas do grafo podem ser direcionadas ou não.
 * </p>
 * <p>
 * Os vertices do grafo serão mapeados por um inteiro para manter o acesso aos
 * seus adjacentes com menor custo.
 * </p>
 * 
 * @author Wallace Alves Esteves Manzano
 *
 * @param <V>
 *            Tipo do vértice
 * @see Number
 * @see Serializable
 */
public class Grafo<V> implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = -7116859553985013010L;
	private static final int DEFAULT_INITIAL_CAPACITY = 10;
	private ArrayList<V> vertices;
	private Adjacencia[] listaAdjacencia;
	private boolean direcionado;
	private int size;

	/**
	 * Cria um grafo com a capacidade inicial de vertices, e ele sendo direcionado
	 * ou não.
	 * 
	 * @param capacidadeInicial
	 *            Capacidade inicial de vertices.
	 * @param direcionado
	 *            {@code true} se o grafo for direcionado.
	 */
	public Grafo(int capacidadeInicial, boolean direcionado) {
		if (capacidadeInicial < 1)
			throw new IllegalArgumentException();
		listaAdjacencia = new Adjacencia[capacidadeInicial];
		vertices = new ArrayList<V>();
		size = 0;
		this.direcionado = direcionado;
	}

	/**
	 * Cria um grafo com a capacidade inicial padrão de vertices, e ele sendo
	 * direcionado ou não.
	 * 
	 * @param direcionado
	 *            {@code true} se o grafo for direcionado.
	 */
	public Grafo(boolean direcionado) {
		this(DEFAULT_INITIAL_CAPACITY, direcionado);
	}

	/**
	 * Cria um grafo nao direcionado.
	 */
	public Grafo() {
		this(false);
	}

	/**
	 * Adiciona um vértice ao grafo, caso ele ainda não esteja no grafo.
	 * 
	 * @param v
	 *            vértice a ser adicionado.
	 * @return {@code true} se o vértice for adicionado.
	 */
	public boolean addVertice(V v) {
		if (vertices.contains(v))
			return false;
		if (size == listaAdjacencia.length)
			grow();
		vertices.add(v);
		size++;
		return true;
	}

	public ArrayList<V> getVertices() {
		return vertices;
	}

	/**
	 * Insere uma aresta ponderada, entre o vértice de origem e destino e também uma
	 * aresta de destino a origem.
	 * 
	 * @param origem
	 *            vértice de origem.
	 * @param destino
	 *            vértice de destino.
	 * @param peso
	 *            peso da aresta.
	 * @return true se a aresta for adicionada.
	 */
	public boolean addAresta(V origem, V destino, double peso) {
		int indexO = vertices.indexOf(origem);
		int indexD = vertices.indexOf(destino);
		if (indexO < 0)
			return false;
		if (indexD < 0)
			return false;
		listaAdjacencia[indexO] = new Adjacencia(indexO, indexD, peso, listaAdjacencia[indexO]);
		if (!direcionado && !origem.equals(destino)) {
			listaAdjacencia[indexD] = new Adjacencia(indexD, indexO, peso, listaAdjacencia[indexD]);
		}
		return true;
	}

	/**
	 * Insere uma aresta ponderada e será adicionado também uma aresta de destino a
	 * origem.
	 * 
	 * @param origem
	 *            Index do vértice de origem.
	 * @param destino
	 *            Index do vértice de destino.
	 * @param peso
	 *            peso da aresta.
	 * @return
	 */
	private boolean addAresta(int origem, int destino, double peso) {
		if (origem < 0)
			return false;
		if (destino < 0)
			return false;
		listaAdjacencia[origem] = new Adjacencia(origem, destino, peso, listaAdjacencia[origem]);
		if (!direcionado && origem != destino) {
			listaAdjacencia[destino] = new Adjacencia(destino, origem, peso, listaAdjacencia[destino]);
		}
		return true;
	}

	/**
	 * Remove uma aresta, entre o vértice de origem e destino e também uma aresta de
	 * destino a origem.
	 * 
	 * @param origem
	 *            vértice de origem.
	 * @param destino
	 *            vértice de destino.
	 * @return true se a aresta for removida.
	 */
	public boolean removeAresta(V origem, V destino) {
		int indexO = vertices.indexOf(origem);
		int indexD = vertices.indexOf(destino);
		if (indexD < 0)
			return false;
		if (indexO < 0)
			return false;
		Adjacencia aux = primeiroAdjacente(origem);
		Adjacencia ant = null;
		while (aux != null) {
			if (aux.vTo == indexD) {
				if (ant == null) {
					listaAdjacencia[indexO] = aux.prox;
					if (!direcionado)
						removeAresta2(destino, origem);
					return true;
				} else {
					ant.prox = aux.prox;
					if (!direcionado)
						removeAresta2(destino, origem);
					return true;
				}
			}
			ant = aux;
			aux = proximoAdjacente(aux);
		}
		return false;
	}

	/**
	 * Remove uma aresta, entre o vértice de origem e destino e também uma aresta de
	 * destino a origem.
	 * 
	 * @param origem
	 *            Index vértice de origem.
	 * @param destino
	 *            Index vértice de destino.
	 * @return true se a aresta for removida.
	 */
	public boolean removeAresta(int origem, int destino) {
		return removeAresta(vertices.get(origem), vertices.get(destino));
	}

	/**
	 * Retorna a próxima {@linkplain Adjacencia adjacência} a partir de uma
	 * adjacência atual.
	 * <p>
	 * Usar em conjunto com o método {@linkplain Grafo#primeiroAdjacente(Object)
	 * primeiroAdjacente} para pegar o primeiro adjacente e assim percorrer a lista
	 * de adjacência.
	 * </p>
	 * 
	 * @param a
	 *            Adjacência atual.
	 * @return próxima {@linkplain Adjacencia adjacente}, ou null caso não haver
	 *         mais adjacentes.
	 * @see Adjacencia
	 * @see Grafo#primeiroAdjacente(Object)
	 */
	public Adjacencia proximoAdjacente(Adjacencia a) {
		return a.prox;
	}

	/**
	 * Retorna o primeiro adjacente de um vértice.
	 * <p>
	 * Usar em conjunto com o método {@linkplain Grafo#proximoAdjacente(Adjacencia)
	 * proximoAdjacente} para pegar o próxima adjacente e assim percorrer a lista de
	 * adjacência.
	 * </p>
	 * 
	 * @param a
	 *            Vértice
	 * @return primeiro {@linkplain Adjacencia adjacente}, ou null caso não tenha
	 *         nenhum adjacentes.
	 * @see Adjacencia
	 * @see Grafo#proximoAdjacente(Adjacencia)
	 * 
	 */
	public Adjacencia primeiroAdjacente(V a) {
		if (!vertices.contains(a))
			return null;
		return listaAdjacencia[vertices.indexOf(a)];
	}

	/**
	 * Retorna o primeiro adjacente de um vértice.
	 * <p>
	 * Usar em conjunto com o método {@linkplain Grafo#proximoAdjacente(Adjacencia)
	 * proximoAdjacente} para pegar o próximo adjacente e assim percorrer a lista de
	 * adjacência.
	 * </p>
	 * 
	 * @param a
	 *            Index do vértice
	 * @return primeiro {@linkplain Adjacencia adjacente}, ou null caso não tenha
	 *         nenhum adjacentes.
	 * @see Adjacencia
	 * @see Grafo#proximoAdjacente(Adjacencia)
	 * 
	 */
	public Adjacencia primeiroAdjacente(int a) {
		return listaAdjacencia[a];
	}

	
	public double getPesoAresta(V origem, V destino) {
		int indexO = vertices.indexOf(origem);
		int indexD = vertices.indexOf(destino);
		if (indexD < 0)
			return Double.MAX_VALUE;
		if (indexO < 0)
			return Double.MAX_VALUE;
		Adjacencia aux = primeiroAdjacente(origem);
		while (aux != null) {
			if (aux.vTo == indexD)
				return aux.peso;
			aux = proximoAdjacente(aux);
		}
		return Double.MAX_VALUE;
	}

	/**
	 * Executa o algoritmo de Prim de arvores geradoras mínimas, retornando a
	 * arvore geradora mínima referente a este grafo, representado por um grafo.
	 * 
	 * @param raiz
	 *            da arvore geradora mínima.
	 * @return {@link Grafo} com a arvore geradora mínima.
	 */
	public Grafo<V> executarPrim(int raiz) {
		Prim<V> a = new Prim<V>(this);
		a.comecarPrim(raiz);
		return a.buildGrafo();
	}

	/**
	 * Executa o algoritmo de Prim de arvores geradoras mínima, retornando a
	 * arvore geradora mínima referente a este grafo, representado por um grafo.
	 * 
	 * @param raiz
	 *            da arvore geradora mínima.
	 * @return {@link Grafo} com a arvore geradora mínima.
	 */
	public Grafo<V> executarKruskal(int raiz) {
		Kruskal<V> a = new Kruskal<V>(this, 1);
		a.comecarKruskal(raiz);
		return a.buildGrafo();
	}

	private Grafo<V> executarKruskal(int raiz, int k) {
		Kruskal<V> a = new Kruskal<V>(this, k);
		a.comecarKruskal(raiz);
		return a.buildGrafo();
	}

	/**
	 * Executa o algoritmo de geração de 1-Tree Mínima.
	 * 
	 * @param raiz
	 *            da 1-Tree.
	 * @return {@link Grafo} com a 1-Tree.
	 */
	public Grafo<V> executarOneTree(int raiz) {
		OneTree<V> ot = new OneTree<>(this);
		return ot.gerenate(raiz);
	}

	/**
	 * Método de agrupamento de dados utilizando o algoritmo de
	 * {@linkplain executarPrim Prim}
	 * 
	 * @param k
	 *            quantidade de clusters
	 * @return retorna um {@link LinkedHashMap}, que mapeia para todas os vertices
	 *         o índice de seu cluster
	 * 
	 */
	public LinkedHashMap<V, Integer> agrupamentoPrim(int k) {
		Grafo<V> a = executarPrim(0);
		PriorityQueue<Adjacencia> adjs = new PriorityQueue<Adjacencia>(Adjacencia.getMaxComparator());
		LinkedHashMap<V, Integer> toReturn = new LinkedHashMap<V, Integer>();
		Adjacencia adj;
		ArrayList<Integer> auxv = new ArrayList<Integer>();
		UnionFind uf = new UnionFind(a.size);

		for (int i = 0; i < a.size; i++) {
			adj = a.primeiroAdjacente(i);
			while (adj != null) {
				adjs.offer(adj);
				adj = a.proximoAdjacente(adj);
			}
		}

		for (int i = 2; i < k * 2; i++) {
			adj = adjs.poll();
			a.removeAresta(adj.vFrom, adj.vTo);
		}

		for (int i = 0; i < a.size; i++) {
			adj = a.primeiroAdjacente(i);
			while (adj != null) {
				uf.union(adj.vFrom, adj.vTo);
				adj = a.proximoAdjacente(adj);
			}
		}

		for (int i = 0; i < a.size; i++) {
			if (!auxv.contains(uf.find(i)))
				auxv.add(uf.find(i));
		}

		for (int i = 0; i < a.size; i++) {
			toReturn.put(a.vertices.get(i), auxv.indexOf(uf.find(i)) + 1);
		}

		return toReturn;

	}

	/**
	 * Método de agrupamento de dados utilizando o algoritmo de
	 * {@linkplain executarKruskal Kruskal}
	 * 
	 * @param k
	 *            quantidade de clusters
	 * @return retorna um {@link LinkedHashMap}, que mapeia para todas os vertices
	 *         o índice de seu cluster
	 * 
	 */
	public LinkedHashMap<V, Integer> agrupamentoKruskal(int k) {
		Grafo<V> a = executarKruskal(0, k);
		LinkedHashMap<V, Integer> toReturn = new LinkedHashMap<V, Integer>();
		UnionFind uf = new UnionFind(a.size);
		Adjacencia adj;
		ArrayList<Integer> auxv = new ArrayList<Integer>();

		for (int i = 0; i < a.size; i++) {
			adj = a.primeiroAdjacente(i);
			while (adj != null) {
				uf.union(adj.vFrom, adj.vTo);
				adj = a.proximoAdjacente(adj);
			}
		}

		for (int i = 0; i < a.size; i++) {
			if (!auxv.contains(uf.find(i)))
				auxv.add(uf.find(i));
		}

		for (int i = 0; i < a.size; i++) {
			toReturn.put(a.vertices.get(i), auxv.indexOf(uf.find(i)) + 1);
		}

		return toReturn;
	}

	/**
	 * Verifica se existe alguma aresta entre o vértice de origem e o vértice
	 * destino
	 * 
	 * @param origem
	 *            Vértice de origem
	 * @param destino
	 *            Vértice de destino
	 * @return true se existir a aresta
	 */
	public boolean existeAresta(V origem, V destino) {
		int indexO = vertices.indexOf(origem);
		int indexD = vertices.indexOf(destino);
		if (indexD < 0)
			return false;
		if (indexO < 0)
			return false;
		Adjacencia aux = primeiroAdjacente(origem);
		while (aux != null) {
			if (aux.vTo == indexD)
				return true;
			aux = proximoAdjacente(aux);
		}
		return false;
	}

	public boolean existeAdjacencia(Adjacencia a) {
		Adjacencia b = primeiroAdjacente(a.origem());
		while (b != null) {
			if (b.equals(a))
				return true;
			b = proximoAdjacente(b);
		}

		return false;
	}

	public void clear() {
		vertices.clear();
		for (int i = 0; i < size; i++)
			listaAdjacencia[i] = null;
		size = 0;
	}

	public int size() {
		return this.size;
	}

	public boolean direcionado() {
		return direcionado;
	}
	
	public void setDirecionado(boolean direcionado) {
		this.direcionado = direcionado;
	}

	public Grafo<V> clone() {
		Grafo<V> g = new Grafo<>(size, direcionado);

		for (V e : this.vertices)
			g.addVertice(e);

		for (V e : this.vertices) {
			Adjacencia a = this.primeiroAdjacente(e);
			while (a != null) {
				g.addAresta(a.origem(), a.destino(), a.peso());
				a = this.proximoAdjacente(a);
			}
		}

		return g;
	}

	public String toString() {
		StringBuffer a = new StringBuffer();

		for (int i = 0; i < size; i++) {
			Adjacencia adj = primeiroAdjacente(i);
			while (adj != null) {
				a.append("(" + vertices.get(adj.vFrom).toString() + ", " + vertices.get(adj.vTo).toString()
						+ ") -- peso = " + adj.peso + "\n");
				adj = proximoAdjacente(adj);
			}
		}
		return a.toString();
	}

	private boolean removeAresta2(V origem, V destino) {
		int indexO = vertices.indexOf(origem);
		int indexD = vertices.indexOf(destino);
		if (indexD < 0)
			return false;
		if (indexO < 0)
			return false;
		Adjacencia aux = primeiroAdjacente(origem);
		Adjacencia ant = null;
		while (aux != null) {
			if (aux.vTo == indexD) {
				if (ant == null) {
					listaAdjacencia[indexO] = aux.prox;
					if (!direcionado)
						removeAresta2(destino, origem);
					return true;
				} else {
					ant.prox = aux.prox;
					return true;
				}
			}
			ant = aux;
			aux = proximoAdjacente(aux);
		}
		return false;
	}

	private void grow() {
		int oldCapacity = listaAdjacencia.length;
		int newCapacity = oldCapacity + ((oldCapacity < 64) ? (oldCapacity + 2) : (oldCapacity >> 1));
		listaAdjacencia = Arrays.copyOf(listaAdjacencia, newCapacity);
	}

	/**
	 * Classe que representa uma adjacência, contendo o peso da aresta, o vértice
	 * destino e a próxima adjacência da lista.
	 * 
	 * @author Wallace Alves Esteves Manzano
	 * 
	 */
	public static class Adjacencia {

		private int vFrom;
		private int vTo;
		private double peso;
		private Adjacencia prox;

		public Adjacencia(int origem, int destino, double peso, Adjacencia prox) {
			this.vFrom = origem;
			this.vTo = destino;
			this.peso = peso;
			this.prox = prox;
		}

		public int origem() {
			return vFrom;
		}

		public int destino() {
			return vTo;
		}

		public double peso() {
			return peso;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Adjacencia other = (Adjacencia) obj;
			if (Double.doubleToLongBits(peso) != Double.doubleToLongBits(other.peso))
				return false;
			if (vFrom != other.vFrom)
				return false;
			if (vTo != other.vTo)
				return false;
			return true;
		}

		/**
		 * Comparador para lista de adjacência, para maior prioridade o elemento de
		 * menor peso da aresta, usando MinHeap.
		 * 
		 * @return Comparador
		 * @see ComparatorDouble
		 * @see PriorityQueue
		 */
		public static ComparatorDouble<Adjacencia> getMinComparator() {
			return new ComparatorDouble<Adjacencia>() {
				@Override
				public double compare(Adjacencia o1, Adjacencia o2) {
					return o1.peso - o2.peso;
				}

			};
		}

		/**
		 * Comparador para lista de adjacência, para maior prioridade o elemento de
		 * maior peso da aresta, usando MinHeap.
		 * 
		 * @return Comparador
		 * @see ComparatorDouble
		 * @see PriorityQueue
		 */
		public static ComparatorDouble<Adjacencia> getMaxComparator() {
			return new ComparatorDouble<Adjacencia>() {
				@Override
				public double compare(Adjacencia o1, Adjacencia o2) {
					return o2.peso - o1.peso;
				}

			};
		}

		@Override
		public String toString() {
			return "(" + vFrom + ", " + vTo + ") peso = " + peso;
		}
	}

	/**
	 * Classe utilizada para o algoritmo de Prim.
	 * 
	 * @author Wallace Alves Esteves Manzano
	 *
	 * @param <V>
	 *            Tipo do vértice do {@link Grafo}
	 */
	private static class Prim<V> {
		private Grafo<V> g;
		private int[] antecessor;
		private double[] peso;

		/**
		 * 
		 * @param g
		 *            Grafo ao qual será executado o algoritmo.
		 */
		private Prim(Grafo<V> g) {
			this.g = g;
			antecessor = new int[g.size];
			peso = new double[g.size];
		}

		/**
		 * Executa o algoritmo de Prim para arvore geradora mínima.
		 * 
		 * @param raiz
		 *            Raiz da arvore
		 */
		private void comecarPrim(int raiz) {
			int n = g.size;
			boolean[] s = new boolean[n];
			for (int i = 0; i < n; i++) {
				peso[i] = Double.MAX_VALUE;
				s[i] = true;
				antecessor[i] = -1;
			}
			peso[raiz] = 0;
			PriorityQueue<Adjacencia> fila = new PriorityQueue<Adjacencia>(Adjacencia.getMinComparator());
			Adjacencia adj = g.primeiroAdjacente(raiz);

			while (adj != null) {
				int v = adj.vTo;
				if (s[v] && (adj.peso < peso[v])) {
					antecessor[v] = raiz;
					fila.offer(adj);
					peso[v] = adj.peso;
				}
				adj = g.proximoAdjacente(adj);
			}
			s[raiz] = false;

			while (!fila.isEmpty()) {
				int u = fila.poll().vTo;
				s[u] = false;
				adj = g.primeiroAdjacente(u);
				while (adj != null) {
					int v = adj.vTo;
					if (s[v] && (adj.peso < peso[v])) {
						antecessor[v] = u;
						fila.offer(adj);
						peso[v] = adj.peso;
					}
					adj = g.proximoAdjacente(adj);
				}
			}

		}

		/**
		 * Constrói uma grafo com a arvore geradora mínima construída pelo método de
		 * {@linkplain Prim#comecarPrim(int) prim}.
		 * 
		 * @return Grafo contendo a arvore geradora mínima.
		 */
		private Grafo<V> buildGrafo() {
			Grafo<V> a = new Grafo<V>(g.size, g.direcionado);
			a.vertices = new ArrayList<V>(g.vertices);
			a.size = g.size;
			for (int i = 0; i < peso.length; i++) {
				if (antecessor[i] != -1)
					a.addAresta(a.vertices.get(antecessor[i]), a.vertices.get(i), peso[i]);
			}

			return a;
		}
	}

	/**
	 * Classe utilizada para o algoritmo de Kruskal.
	 * 
	 * @author Wallace Alves Esteves Manzano
	 *
	 * @param <V>
	 *            Tipo do vértice do {@link Grafo}
	 */
	private static class Kruskal<V> {
		private Grafo<V> g;
		private ArrayList<Adjacencia> aceitas;
		private int qtdC;

		public Kruskal(Grafo<V> g, int quantidadeClusters) {
			this.g = g;
			qtdC = quantidadeClusters;
			aceitas = new ArrayList<Adjacencia>();
		}

		/**
		 * Executa o algoritmo de Kruskal para arvore geradora mínima.
		 * 
		 * @param raiz
		 *            Raiz da arvore
		 */
		private void comecarKruskal(int raiz) {
			PriorityQueue<Adjacencia> fila = new PriorityQueue<Adjacencia>(Adjacencia.getMinComparator());
			UnionFind uf = new UnionFind(g.size);
			for (int i = 0; i < g.size; i++) {
				Adjacencia adj = g.primeiroAdjacente(i);
				while (adj != null) {
					fila.offer(adj);
					adj = g.proximoAdjacente(adj);
				}
			}

			while (!fila.isEmpty()) {
				Adjacencia adj = fila.poll();
				int cF = uf.find(adj.vFrom), cT = uf.find(adj.vTo);

				if (uf.quantidadeGrupos() == qtdC)
					return;
				if (cF != cT) {
					uf.union(cF, cT);
					aceitas.add(adj);
				}
			}

		}

		/**
		 * Constrói uma grafo com a arvore geradora mínima construída pelo método de
		 * {@linkplain Kruskal#comecarKruskal(int) kruskal}.
		 *
		 * @return Grafo contendo a arvore geradora mínima.
		 */
		private Grafo<V> buildGrafo() {
			Grafo<V> a = new Grafo<V>(g.size, false);
			a.vertices = new ArrayList<V>(g.vertices);
			a.size = g.size;

			for (Adjacencia e : aceitas)
				a.addAresta(e.vFrom, e.vTo, e.peso);

			return a;
		}
	}

	/**
	 * Classe utilizada para a geração de 1-Tree mínima.
	 * 
	 * @author Wallace Manzano
	 *
	 * @param <V>
	 */
	private static class OneTree<V> {
		Grafo<V> g;

		/**
		 * 
		 * @param g
		 *            Grafo ao qual será executado o algoritmo.
		 */
		private OneTree(Grafo<V> g) {
			this.g = g;
		}

		private Grafo<V> gerenate(int root) {
			Adjacencia a = null;
			Grafo<V> r = g.executarPrim(0);
			PriorityQueue<Adjacencia> pq = new PriorityQueue<>(Adjacencia.getMinComparator());

			for (V e : g.getVertices()) {
				a = g.primeiroAdjacente(e);
				while (a != null) {
					if (a.destino() == root && !r.existeAdjacencia(a)
							&& !r.existeAdjacencia(new Adjacencia(a.destino(), a.origem(), a.peso(), null)))
						pq.add(a);
					a = g.proximoAdjacente(a);
				}
			}

			a = pq.poll();
			if (a != null) {

				r.addAresta(g.getVertices().get(a.vFrom), g.getVertices().get(a.vTo), a.peso);
			}
			return r;
		}

	}
}
