package aestrela;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import utils.Grafo;

public class PCVAEstrela {

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
		valoresF.put(estadoInicial,
				heuristica(cidades.get(0), new ArrayList<Integer>(), estadoInicial.cidadesNaoVisitadas, g));

		while (!estadosAbertos.isEmpty()) {

			// Verifica todos os estados abertos para achar aquele com o melhor valor de f
			double valorEstadoAtual = valoresF.get(estadosAbertos.get(0));
			Estado estadoAtual = estadosAbertos.get(0);
			for (int i = 0; i < estadosAbertos.size(); i++) {
				if (valoresF.get(estadosAbertos.get(i)) < valorEstadoAtual) {
					estadoAtual = estadosAbertos.get(i);
					valorEstadoAtual = valoresF.get(estadosAbertos.get(i));
				}
			}

			// Caso tenha chego no estado final, retorna o caminho
			if (estadoAtual.cidadesNaoVisitadas.isEmpty()) {
				return estadoAtual;
			}

			// Ap�s chegar ao estado com menor f, remove-o da lista de estados abertos
			estadosAbertos.remove(estadoAtual);

			// Descobre os novos estados ap�s expandir o estado anterior
			LinkedHashMap<Estado, Double> vizinhos = getVizinhos(estadoAtual, cidades, g);
			for (Map.Entry<Estado, Double> entrada : vizinhos.entrySet()) {

				// Caso ele seja novo, o adiciona aos estados abertos
				if (!estadosAbertos.contains(entrada.getKey())) {
					estadosAbertos.add(entrada.getKey());
				}

				// Calcula o valor g para o novo estado
				double valorG = valoresG.get(estadoAtual) + entrada.getValue();

				// Caso o valor g calculado seja otimo para esse estado, atualiza os valors de g
				// e f
				if (valoresG.get(entrada.getKey()) == null || valorG < valoresG.get(entrada.getKey())) {
					valoresG.put(entrada.getKey(), valorG);
					valoresF.put(entrada.getKey(), valorG + heuristica(cidades.get(0), new ArrayList<Integer>(),
							entrada.getKey().cidadesNaoVisitadas, g));
				}
			}
		}
		return new Estado(null, null);
	}

	public static double heuristica(Integer comeco, ArrayList<Integer> arvore, ArrayList<Integer> cidadesNaoVisitadas,
			Grafo<Integer> g) {
		if (cidadesNaoVisitadas.isEmpty()) {
			// Caso tenha chego no n� final, o valor da heuristica � zero
			return 0;
		} else {
			// Copia as cidadeds n�o visitadas do estado atual, para n�o haver mudan�as n�o
			// intencionadas ao se modificar as cidades
			ArrayList<Integer> factiveis = new ArrayList<Integer>();
			for (int j = 0; j < cidadesNaoVisitadas.size(); j++) {
				Integer proxElem = cidadesNaoVisitadas.get(j);
				factiveis.add(proxElem);
			}

			// Adiciona o ponto inicial como objetivo, uma vez que n�o estamos mexendo no
			// pr�pio estado
			if (!factiveis.contains(comeco) && !arvore.contains(comeco)) {
				factiveis.add(comeco);
			}

			if (arvore.isEmpty()) {
				// Se esse for o primeiro n�, come�a a �rvore com um n� arbitr�rio
				arvore.add(factiveis.get(0));
				factiveis.remove(cidadesNaoVisitadas.get(0));
				return heuristica(comeco, arvore, factiveis, g);
			} else {
				// Caso contr�rio, calcula-se o custo de uma �rvore geradora m�nima, passando
				// por todos os n�s n�o visitados e de volta ao inicial
				double distancia = getDistancia(arvore.get(0), factiveis.get(0), g);
				Integer cidadeProx = factiveis.get(0);
				for (int i = 0; i < arvore.size(); i++) {
					for (int j = 0; j < factiveis.size(); j++) {
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

	public static LinkedHashMap<Estado, Double> getVizinhos(Estado estado, ArrayList<Integer> cidades,
			Grafo<Integer> g) {
		LinkedHashMap<Estado, Double> vizinhos = new LinkedHashMap<Estado, Double>();
		Integer cidadeAtual = estado.cidadesVisitadas.get(estado.cidadesVisitadas.size() - 1);

		for (int i = 0; i < estado.cidadesNaoVisitadas.size(); i++) {
			Integer vizinho = estado.cidadesNaoVisitadas.get(i);

			// Copia as cidadeds visitadas do estado atual, para n�o haver mudan�as n�o
			// intencionadas ao se modificar as cidades
			ArrayList<Integer> caminho = new ArrayList<Integer>();
			for (int j = 0; j < estado.cidadesVisitadas.size(); j++) {
				Integer proxElem = estado.cidadesVisitadas.get(j);
				caminho.add(proxElem);
			}
			caminho.add(vizinho);

			Estado novoEstado = new Estado(caminho, cidades);

			// Adiciona o novo estado formado a lista de vizinhos, com seu valor g
			// correspondente
			vizinhos.put(novoEstado, getDistancia(cidadeAtual, vizinho, g));
		}

		return vizinhos;
	}

	public static double getDistancia(Integer a, Integer b, Grafo<Integer> g) {
		return g.getPesoAresta(a, b);
	}
}
