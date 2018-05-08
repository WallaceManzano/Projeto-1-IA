package aestrela;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import branch_and_bound.TSPBranchAndBound;

import java.io.*;

import utils.Grafo;

public class PCVAEstrela {

	public static void main(String[] args) {
		Integer c;
		Integer d = 5;
		double media;
		Grafo<Integer> g = new Grafo<Integer>(), g2;
		File f = new File("Resultados.txt");
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
		while (d !=6) {
			c = 0;
			media = 0;
			w.write("N�mero de cidades: " + d + "\n");
			while (c != 10){
				g = gerarGrafo(d, g);
				w.write("Execu��o: " + (c+1) + "\n");
				w.write("A*\n");
				c++;
			
				double startTime1 = System.currentTimeMillis();
			
				Estado caminho = aEstrela(g);
				g2 = g.clone();
			
				double endTime1   = System.currentTimeMillis();
				double totalTime1 = endTime1 - startTime1;
				media = media + (totalTime1/1000);
				
				w.write("Caminho obtido -> ");
				
				for (int i = 0 ; i < caminho.cidadesVisitadas.size() ; i++) {
					w.write(Integer.toString(caminho.cidadesVisitadas.get(i)));
					if (i != caminho.cidadesVisitadas.size() - 1) {
						w.write(":");
					}
				}
				w.write("\nTempo gasto: " + totalTime1/1000 + "\n");
				
				w.write("Branch and Bound\n");
				
				TSPBranchAndBound<Integer> t = new TSPBranchAndBound<Integer>(g2);
				Grafo<Integer> r = t.solve();
				w.write(r.toString());
				
				g.clear();
				
			}
			media = media/c;
			w.write("M�dia de tempo gasto: " + media + "\n\n");
			d++;
		}
		w.close();
		} catch (Exception e) {e.printStackTrace();}

	}
	
		public static Estado aEstrela(Grafo<Integer> g) {
			ArrayList<Integer> inicio = new ArrayList<Integer>();
			ArrayList<Integer> cidades = new ArrayList<Integer>();
			cidades = g.getVertices();
			inicio.add(cidades.get(0));
			Estado estadoInicial = new Estado(inicio, cidades);
			
			ArrayList<Estado> estadosAbertos = new ArrayList<Estado>();
			estadosAbertos.add(estadoInicial);
			
			HashMap<Estado, Double> valoresG = new HashMap<Estado, Double>();
			valoresG.put(estadoInicial, 0.0);
			
			HashMap<Estado, Double> valoresF = new HashMap<Estado, Double>();
			valoresF.put(estadoInicial, heuristica(cidades.get(0), new ArrayList<Integer>(), estadoInicial.cidadesNaoVisitadas, g));
			
			while(!estadosAbertos.isEmpty()) {
				
				// Verifica todos os estados abertos para achar aquele com o melhor valor de f
				double valorEstadoAtual = valoresF.get(estadosAbertos.get(0));
				Estado estadoAtual = estadosAbertos.get(0);
				for (int i = 0 ; i < estadosAbertos.size() ; i++) {
					if(valoresF.get(estadosAbertos.get(i)) < valorEstadoAtual)
					{
						estadoAtual = estadosAbertos.get(i);
						valorEstadoAtual = valoresF.get(estadosAbertos.get(i));
					}
				}

				//Caso tenha chego no estado final, retorna o caminho
				if (estadoAtual.cidadesNaoVisitadas.isEmpty()) {
					return estadoAtual;
				}

				//Após chegar ao estado com menor f, remove-o da lista de estados abertos
				estadosAbertos.remove(estadoAtual);

				//Descobre os novos estados após expandir o estado anterior
				LinkedHashMap<Estado, Double> vizinhos = getVizinhos(estadoAtual, cidades, g);
				for (Map.Entry<Estado, Double> entrada : vizinhos.entrySet()) {

					//Caso ele seja novo, o adiciona aos estados abertos
					if (!estadosAbertos.contains(entrada.getKey())) {
						estadosAbertos.add(entrada.getKey());
					}

					//Calcula o valor g para o novo estado
					double valorG = valoresG.get(estadoAtual) + entrada.getValue();

					//Caso o valor g calculado seja otimo para esse estado, atualiza os valors de g e f
					if (valoresG.get(entrada.getKey()) ==  null || valorG < valoresG.get(entrada.getKey())) {
						valoresG.put(entrada.getKey(), valorG);
						valoresF.put(entrada.getKey(), valorG + heuristica(cidades.get(0), new ArrayList<Integer>(), entrada.getKey().cidadesNaoVisitadas, g));
					}
				}
			}
			return new Estado(null, null);
	}
		public static double heuristica(Integer comeco, ArrayList<Integer> arvore, ArrayList<Integer> cidadesNaoVisitadas, Grafo<Integer> g) {
			if (cidadesNaoVisitadas.isEmpty()) {
				//Caso tenha chego no nó final, o valor da heuristica é zero
				return 0;
			} else {
				// Copia as cidadeds não visitadas do estado atual, para não haver mudanças não intencionadas ao se modificar as cidades
				ArrayList<Integer> factiveis = new ArrayList<Integer>();
				for (int j = 0 ; j < cidadesNaoVisitadas.size() ; j++) {
					Integer proxElem = cidadesNaoVisitadas.get(j);
					factiveis.add(proxElem);
				}

				//Adiciona o ponto inicial como objetivo, uma vez que não estamos mexendo no própio estado
				if (!factiveis.contains(comeco) && !arvore.contains(comeco)) {
					factiveis.add(comeco);
				}

				if (arvore.isEmpty()) {
					//Se esse for o primeiro nó, começa a árvore com um nó arbitrário
					arvore.add(factiveis.get(0));
					factiveis.remove(cidadesNaoVisitadas.get(0));
					return heuristica(comeco, arvore, factiveis, g);
				} else {
					//Caso contrário, calcula-se o custo de uma árvore geradora mínima, passando por todos os nós não visitados e de volta ao inicial
					double distancia = getDistancia(arvore.get(0), factiveis.get(0), g);
					Integer cidadeProx = factiveis.get(0);
					for (int i = 0 ; i < arvore.size() ; i++) {
						for (int j = 0 ; j < factiveis.size() ; j++) {
							if (getDistancia(arvore.get(i), factiveis.get(j), g) < distancia) {
								distancia = getDistancia(arvore.get(i), factiveis.get(j), g);
								cidadeProx = factiveis.get(j);
							}
						}
					}

					arvore.add(cidadeProx);
					factiveis.remove(cidadeProx);

					return distancia + heuristica(comeco, arvore, factiveis, g);
				}
			}
		}
		
		public static LinkedHashMap<Estado, Double> getVizinhos(Estado estado, ArrayList<Integer> cidades, Grafo<Integer> g) {
			LinkedHashMap<Estado, Double> vizinhos = new LinkedHashMap<Estado, Double>();
			Integer cidadeAtual = estado.cidadesVisitadas.get(estado.cidadesVisitadas.size()-1);

			for (int i = 0 ; i < estado.cidadesNaoVisitadas.size() ; i++) {
				Integer vizinho = estado.cidadesNaoVisitadas.get(i);

				//Copia as cidadeds visitadas do estado atual, para não haver mudanças não intencionadas ao se modificar as cidades
				ArrayList<Integer> caminho = new ArrayList<Integer>();
				for (int j = 0 ; j < estado.cidadesVisitadas.size() ; j++) {
					Integer proxElem = estado.cidadesVisitadas.get(j);
					caminho.add(proxElem);
				}
				caminho.add(vizinho);

				Estado novoEstado = new Estado(caminho, cidades);

				//Adiciona o novo estado formado a lista de vizinhos, com seu valor g correspondente
				vizinhos.put(novoEstado, getDistancia(cidadeAtual, vizinho, g));
			}

			return vizinhos;
		}
		
		public static double getDistancia(Integer a, Integer b, Grafo<Integer> g) {
			return g.getPesoAresta(a, b);
		}
		
		public static Grafo<Integer> gerarGrafo(Integer i, Grafo<Integer> g){
			//gera um grafo com i vertices e pesos randomicos das arestas
			Random r = new Random();
			Integer p;
			for (int v = 1; v<=i; v++) {
				g.addVertice(v);
			}
			for (int v = 1; v<=i; v++) {
				for (int j = v; j<=i; j++) {
					if (v ==j) {
						g.addAresta(v, j, 0.0);
					}
					else {
						p = 1 + r.nextInt(99);
						g.addAresta(v, j, p);
						//System.out.println(Integer.toString(v) + ':' + Integer.toString(j) + ':' + Integer.toString(p));
					}
				}
			}
			return g;
		}
}

