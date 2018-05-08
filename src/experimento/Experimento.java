package experimento;

import static aestrela.PCVAEstrela.aEstrela;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import aestrela.Estado;
import branch_and_bound.PCVBranchAndBound;
import utils.Grafo;

public class Experimento {
	public static void main(String[] args) {
		Integer c;
		Integer D_INICIAL = 5;
		Integer d = D_INICIAL; // define o n�mero de cidades inicial
		double media1, media2;
		Grafo<Integer> g = new Grafo<Integer>(), g2;
		String file;
		System.out.println("Executando...");
		Integer a = 0;
		Integer D = 21; // define o número de cidades final-1
		Integer C = 10; // define o número de execuções-1
		while (d != D) {
			file = "Resultados" + d + ".txt";
			File f = new File(file);
			try {
				BufferedWriter w = new BufferedWriter(new FileWriter(f));
				c = 0;
				media1 = 0;
				media2 = 0;
				w.write("Número de cidades: " + d + "\n");
				while (c != C) {
					String aux = "|";
					Double aux2 = Math.floor(((double) (a / ((double) (C) * (D - D_INICIAL))) * ((double) 50)));
					for (int i = 0; i < 50; i++) {
						if (i < aux2)
							aux += "=";
						else
							aux += " ";
					}
					aux += "|";

					System.out.print("\r" + aux);
					a++;
					g = gerarGrafo(d, g);
					w.write("Execução: " + (c + 1) + "\n");
					w.write("A*\n");
					c++;
					g2 = g.clone();

					double startTime1 = System.currentTimeMillis();

					Estado caminho = aEstrela(g);

					double endTime1 = System.currentTimeMillis();
					double totalTime1 = endTime1 - startTime1;
					media1 = media1 + (totalTime1 / 1000);

					w.write("Caminho obtido -> ");

					for (int i = 0; i < caminho.cidadesVisitadas.size(); i++) {
						w.write(Integer.toString(caminho.cidadesVisitadas.get(i)));
						if (i != caminho.cidadesVisitadas.size() - 1) {
							w.write(":");
						}
					}
					w.write("\n\nTempo gasto: " + totalTime1 / 1000 + "\n\n");

					w.write("Branch and Bound\n");

					double startTime2 = System.currentTimeMillis();

					PCVBranchAndBound<Integer> t = new PCVBranchAndBound<Integer>(g2);
					Grafo<Integer> r = t.solve();

					double endTime2 = System.currentTimeMillis();
					double totalTime2 = endTime2 - startTime2;
					media2 = media2 + (totalTime2 / 1000);

					w.write(r.toString());
					w.write("\nTempo gasto: " + totalTime2 / 1000 + "\n\n");

					g.clear();
					g2.clear();
				}
				media1 = media1 / c;
				media2 = media2 / c;
				w.write("\nMédia de tempo gasto A*: " + media1 + "\n");
				w.write("Média de tempo gasto B&B: " + media2 + "\n\n");
				d++;
				w.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String aux = "|";
		for (int i = 0; i < 50; i++) {
			aux += "=";
		}
		aux += "|";

		System.out.println("\r" + aux);

		System.out.println("Fim...\nResultado disponível nos arquivos Resultados[numero da execução].txt ");
	}

	public static Grafo<Integer> gerarGrafo(Integer i, Grafo<Integer> g) {
		// gera um grafo com i vertices e pesos randomicos das arestas
		Random r = new Random();
		Integer p;
		for (int v = 1; v <= i; v++) {
			g.addVertice(v);
		}
		for (int v = 1; v <= i; v++) {
			for (int j = v; j <= i; j++) {
				if (v == j) {
					g.addAresta(v, j, 0.0);
				} else {
					p = 1 + r.nextInt(99);
					g.addAresta(v, j, p);
					// System.out.println(Integer.toString(v) + ':' + Integer.toString(j) + ':' +
					// Integer.toString(p));
				}
			}
		}
		return g;
	}
}
