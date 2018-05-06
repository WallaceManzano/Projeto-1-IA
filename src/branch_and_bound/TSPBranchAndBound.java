package branch_and_bound;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import utils.Grafo;
import utils.Grafo.Adjacencia;
import utils.UnionFind;

/**
 * Classe que realiza o algoritmo Branch and Bound em grafos a fim de encontrar
 * um circuito com o menor custo possível.
 * <p>
 * Para usar esta classe deve-se passar em seu construtor um {@linkplain Grafo
 * grafo} que represente o modelo do problema do caixeiro viajante a ser
 * resolvido onde os vertices representam os pontos que terão que ser visitados
 * e as {@linkplain Adjacencia arestas} são ligações entre os pontos e o peso da
 * aresta o custo.
 * </p>
 * <p>
 * Para executar o algoritmo deve-se invocar o método {@link solve}, cujo o qual
 * retornará o grafo com o circuito mínimo
 * </p>
 * 
 * Exemplo:
 * 
 * <pre>
 * Grafo<Integer> g = new Grafo<>(true);
 * for (int i = 1; i <= 8; i++) {
 * 	g.addVertice(i);
 * }
 * g.addAresta(1, 2, 2);
 * g.addAresta(1, 3, 4);
 * g.addAresta(1, 4, 5);
 * 
 * g.addAresta(2, 1, 2);
 * g.addAresta(2, 3, 4);
 * g.addAresta(2, 6, 7);
 * g.addAresta(2, 7, 5);
 * 
 * g.addAresta(3, 1, 4);
 * g.addAresta(3, 2, 4);
 * g.addAresta(3, 4, 1);
 * g.addAresta(3, 5, 7);
 * g.addAresta(3, 6, 4);
 * 
 * g.addAresta(4, 1, 5);
 * g.addAresta(4, 3, 1);
 * g.addAresta(4, 5, 10);
 * 
 * g.addAresta(5, 3, 7);
 * g.addAresta(5, 4, 10);
 * g.addAresta(5, 6, 1);
 * g.addAresta(5, 8, 4);
 * 
 * g.addAresta(6, 2, 7);
 * g.addAresta(6, 3, 4);
 * g.addAresta(6, 5, 1);
 * g.addAresta(6, 7, 3);
 * g.addAresta(6, 8, 5);
 * 
 * g.addAresta(7, 2, 5);
 * g.addAresta(7, 6, 3);
 * g.addAresta(7, 8, 2);
 * 
 * g.addAresta(8, 5, 4);
 * g.addAresta(8, 6, 5);
 * g.addAresta(8, 7, 2);
 * 
 * TSPBranchAndBound<Integer> t = new TSPBranchAndBound<>(g);
 * Grafo<Integer> r = t.solve();
 * </pre>
 * 
 * Retornando o grafo:
 * 
 * <pre>
 * (1, 2) -- peso = 2.0
 * (2, 7) -- peso = 5.0
 * (3, 4) -- peso = 1.0
 * (4, 1) -- peso = 5.0
 * (5, 6) -- peso = 1.0
 * (6, 3) -- peso = 4.0
 * (7, 8) -- peso = 2.0
 * (8, 5) -- peso = 4.0
 * </pre>
 * 
 * @author Wallace Manzano
 *
 * @param <T>
 *            Tipo da aresta do grafo.
 * @see Grafo
 */
public class TSPBranchAndBound<T> {
	private Grafo<T> graph;
	private double bestSolution = Double.MAX_VALUE;
	private Grafo<T> bestSolutionGraph = null;

	/**
	 * Construtor da classe TSPBranchAndBound.
	 * 
	 * @param g
	 *            {@link Grafo} representando o modelo do problema do caixeiro
	 *            viajante a ser resolvido.
	 */
	public TSPBranchAndBound(Grafo<T> g) {
		this.graph = g;
	}

	/**
	 * Executa o algoritmo Branch and Bound no grafo que foi passado pelo
	 * construtor. A busca é feita por profundidade simples e a poda dos ramos da
	 * arvore de busca é feita por infactibilidade, qualidade e otimalidade.
	 * 
	 * @return o {@linkplain Grafo grafo} com o circuito de menor custo.
	 */
	public Grafo<T> solve() {
		T v = graph.getVertices().get(0);
		Node n = new Node(v, graph);

		/**
		 * Execution Stack tem como função fazer possível a busca em profundidade e
		 * evitando eventuais problemas de overflow na stack da Java Virtual Machine
		 * (JVM).
		 */
		Stack<Node> executionStack = new Stack<>();

		executionStack.push(n);

		while (!executionStack.empty()) {
			n = executionStack.pop();
			v = n.vortex;

			// somente para teste
			/*
			 * System.out.println("Visited graph with cost " + n.calculateEstimatedCost());
			 * System.out.println(n.visited.toString());
			 * System.out.println(n.reducedGraph.toString());
			 */

			// poda por infactibilidade (não consegue fechar circuito)
			if (!n.checkFeasibility()) {
				continue;
			}

			// poda por qualidade
			if (n.calculateEstimatedCost() > bestSolution) {
				continue;
			}

			// todas as cidades visitadas
			if (n.level == graph.getVertices().size() - 1) {
				boolean aux = false;

				// ver se o circuito fecha
				Adjacencia a = n.reducedGraph.primeiroAdjacente(v);
				while (a != null) {
					if (a.destino() == 0) {
						aux = true;
					} else {
						n.reducedGraph.removeAresta(a.origem(), a.destino());
					}
					a = n.reducedGraph.proximoAdjacente(a);
				}

				// se o cricuito fechar, atualiza a melhor solução.
				if (aux & (n.calculateEstimatedCost() < bestSolution)) {
					bestSolution = n.calculateEstimatedCost();
					bestSolutionGraph = n.reducedGraph;
				}
				continue;
			}
			Adjacencia a = graph.primeiroAdjacente(v);
			while (a != null) {
				T vChild = graph.getVertices().get(a.destino());
				if (!n.visited.contains(vChild)) {
					Node child = new Node(vChild, n, a);
					executionStack.add(child);
				}
				a = graph.proximoAdjacente(a);
			}

		}

		return bestSolutionGraph;
	}

	/**
	 * Retorna o custo do circuito dado pelo método {@link solve}. Caso o método não
	 * tenha sido invocado, então ele será invocado e seu resultado será retornado.
	 * 
	 * @return o custo do circuito da solução.
	 */
	public double solutionCost() {
		if (bestSolution == Double.MAX_VALUE)
			solve();
		return bestSolution;
	}

	/**
	 * Classe auxiliar para representar um nó do espaço de estados da arvore de
	 * busca.
	 * 
	 * @author Wallace Manzano
	 *
	 */
	private class Node {
		private Grafo<T> reducedGraph;
		private List<Adjacencia> path;
		private T vortex;
		private int level; // também representa a quantidade de cidades visitadas
		private double estimatedCost;
		private ArrayList<T> visited;

		/**
		 * Cria um nó da arvore de busca. Este construtor deve ser usado somente para
		 * criar o nó raiz.
		 * 
		 * @param v
		 *            Primeiro nó do grafo a ser visitado.
		 * @param graph
		 *            Grafo inicial do problema a ser resolvido.
		 */
		private Node(T v, Grafo<T> graph) {
			path = new ArrayList<>();
			level = 0;
			estimatedCost = -1;
			reducedGraph = graph.clone();
			vortex = v;
			visited = new ArrayList<>();
			visited.add(v);
		}

		/**
		 * Cria um nó da arvore de busca. E realiza a redução do grafo referente ao nó e
		 * mantém todas reduções dos nós ascendentes da arvore de busca.
		 * 
		 * @param v
		 *            Nó sendo visitado.
		 * @param parent
		 *            {@linkplain Node Nó} pai.
		 * @param addToPath
		 *            {@link Adjacencia} a ser mantida no grafo.
		 * @see Node#reducedGraph
		 */
		private Node(T v, Node parent, Adjacencia addToPath) {
			path = new ArrayList<>(parent.path);
			path.add(addToPath);
			level = parent.level + 1;
			estimatedCost = -1;
			vortex = v;
			reducedGraph = parent.reducedGraph.clone();
			visited = new ArrayList<>(parent.visited);
			visited.add(v);

			reduceGraph(v, parent, addToPath);
		}

		/**
		 * Realiza a redução do grafo referente ao nó.
		 * <p>
		 * A redução consiste em remover todas as arestas que saem do nó a sendo
		 * visitado, com exceção da aresta selecionada para a possível solução, e remove
		 * todas as arestas que tem o mesmo destino da aresta sendo mantida na possível
		 * solução.
		 * </p>
		 * 
		 * @param v
		 *            Nó sendo visitado.
		 * @param parent
		 *            {@linkplain Node Nó} pai.
		 * @param addToPath
		 *            {@link Adjacencia} a ser mantida no grafo.
		 */
		private void reduceGraph(T v, Node parent, Adjacencia addToPath) {
			Adjacencia a = reducedGraph.primeiroAdjacente(parent.vortex);
			while (a != null) {
				if (!a.equals(addToPath))
					reducedGraph.removeAresta(a.origem(), a.destino());
				a = reducedGraph.proximoAdjacente(a);
			}

			for (T e : reducedGraph.getVertices()) {
				a = reducedGraph.primeiroAdjacente(e);
				while (a != null) {
					if (a.destino() == reducedGraph.getVertices().indexOf(v))
						if (!a.equals(addToPath))
							reducedGraph.removeAresta(a.origem(), a.destino());
					a = reducedGraph.proximoAdjacente(a);
				}
			}
		}

		/**
		 * Verifica se o nó é factível, ou seja, é possível sair do nó inicial passar
		 * por todos os nós e voltar ao inicial, ou seja, não é disjunto. Para isso é
		 * usada a estrutura de dados Union Find (a.k.a. disjoint-set).
		 * 
		 * @return true se o grafo for factível.
		 * @see UnionFind
		 */
		private boolean checkFeasibility() {
			UnionFind uf = new UnionFind(reducedGraph.size());
			for (T e : reducedGraph.getVertices()) {
				Adjacencia a = reducedGraph.primeiroAdjacente(e);
				while (a != null) {
					uf.union(a.origem(), a.destino());
					a = reducedGraph.proximoAdjacente(a);
				}

			}
			return uf.quantidadeGrupos() == 1;
		}

		/**
		 * Calcula o custo estimado do grafo reduzido do nó (lower bound).
		 * <p>
		 * Para isso é gerada a 1-Tree mínima do grafo reduzido e é somado o pesos de
		 * todas as arestas da 1-Tree gerada.
		 * </p>
		 * 
		 * @return custo estimado.
		 * @see Grafo#executarOneTree(int)
		 * @see OneTree
		 */
		private double calculateEstimatedCost() {
			Adjacencia a;
			// estimar custo usando 1-Tree (lower bound)
			if (estimatedCost == -1) {
				estimatedCost = 0;
				Grafo<T> oneTree = reducedGraph.executarOneTree(0);
				for (T e : reducedGraph.getVertices()) {
					a = oneTree.primeiroAdjacente(e);
					while (a != null) {
						estimatedCost += a.peso();
						a = oneTree.proximoAdjacente(a);
					}
				}
			}
			return estimatedCost;
		}
	}
}
