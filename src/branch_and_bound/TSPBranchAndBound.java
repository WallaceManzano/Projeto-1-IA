package branch_and_bound;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import utils.Grafo;
import utils.Grafo.Adjacencia;

public class TSPBranchAndBound<T> {
	private Grafo<T> graph;
	private double bestSolution = Double.MAX_VALUE;
	private Grafo<T> bestSolutionGraph = null;

	public TSPBranchAndBound(Grafo<T> g) {
		this.graph = g;
	}

	public Grafo<T> solve() {
		T v = graph.getVertices().get(0);
		Node n = new Node(v, graph);
		Stack<Node> executionStack = new Stack<>();

		executionStack.push(n);

		while (!executionStack.empty()) {
			n = executionStack.pop();
			v = n.vortex;
			System.out.println("Visited graph with cost " + n.estimatedCost);
			System.out.println(n.visited.toString());
			System.out.println(n.reducedGraph.toString());

			// poda por qualidade
			// if (n.estimatedCost > bestSolution) {
			// continue;
			// }
			
			// todas as cidades visitadas
			if (n.level == graph.getVertices().size() - 1) {
				boolean aux = false;
				
				// ver se o circuito fecha
				Adjacencia a = n.reducedGraph.primeiroAdjacente(v);
				while (a != null) {
					if (a.destino() == 0)
						aux = true;
					a = n.reducedGraph.proximoAdjacente(a);
				}
				
				// se o cricuito fechar, atualiza a melhor solução.
				if (aux & (n.estimatedCost < bestSolution)) {
					bestSolution = n.estimatedCost;
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

	private class Node {
		private Grafo<T> reducedGraph;
		private List<Adjacencia> path;
		private T vortex;
		private int level; // cidades visitadas
		private double estimatedCost;
		private ArrayList<T> visited;

		private Node(T v, Grafo<T> graph) {
			path = new ArrayList<>();
			level = 0;
			reducedGraph = graph.clone();
			vortex = v;
			visited = new ArrayList<>();
			visited.add(v);
		}

		private Node(T v, Node parent, Adjacencia addToPath) {
			path = new ArrayList<>(parent.path);
			path.add(addToPath);
			level = parent.level + 1;
			vortex = v;
			reducedGraph = parent.reducedGraph.clone();
			visited = new ArrayList<>(parent.visited);
			visited.add(v);

			// reduzir o grafo
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

			// estimar custo usando 1-Tree (lower bound)
			estimatedCost = 0;
			Grafo<T> oneTree = reducedGraph;// reducedGraph.executarOneTree(0);
			for (T e : reducedGraph.getVertices()) {
				a = oneTree.primeiroAdjacente(e);
				while (a != null) {
					estimatedCost += a.peso();
					a = oneTree.proximoAdjacente(a);
				}
			}

		}

	}

	public static void main(String[] agrgs) throws IOException {
		Grafo<Integer> g = new Grafo<>(true);
		for (int i = 1; i <= 8; i++) {
			g.addVertice(i);
		}
		System.setOut((new PrintStream(System.getenv("HOME") + "/testTSP.txt")));
		g.addAresta(1, 2, 2);
		g.addAresta(1, 3, 4);
		g.addAresta(1, 4, 5);

		g.addAresta(2, 1, 2);
		g.addAresta(2, 3, 4);
		g.addAresta(2, 6, 7);
		g.addAresta(2, 7, 5);

		g.addAresta(3, 1, 4);
		g.addAresta(3, 2, 4);
		g.addAresta(3, 4, 1);
		g.addAresta(3, 5, 7);
		g.addAresta(3, 6, 4);

		g.addAresta(4, 1, 5);
		g.addAresta(4, 3, 1);
		g.addAresta(4, 5, 10);

		g.addAresta(5, 3, 7);
		g.addAresta(5, 4, 10);
		g.addAresta(5, 6, 1);
		g.addAresta(5, 8, 4);

		g.addAresta(6, 2, 7);
		g.addAresta(6, 3, 4);
		g.addAresta(6, 5, 1);
		g.addAresta(6, 7, 3);
		g.addAresta(6, 8, 5);

		g.addAresta(7, 2, 5);
		g.addAresta(7, 6, 3);
		g.addAresta(7, 8, 2);

		g.addAresta(8, 5, 4);
		g.addAresta(8, 6, 5);
		g.addAresta(8, 7, 2);

		TSPBranchAndBound<Integer> t = new TSPBranchAndBound<>(g);
		Grafo<Integer> r = t.solve();// t.solve();
		System.out.println("Resolution");
		System.out.println(r.toString());
	}
}
