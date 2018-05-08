package aestrela;

import java.util.ArrayList;

public class Estado {
	
	public ArrayList<Integer> cidadesVisitadas;
	
	public ArrayList<Integer> cidadesNaoVisitadas;
	
	public Integer peso;
	
	public Estado(ArrayList<Integer> cidadesVisitadas, ArrayList<Integer> cidades) {
		cidadesNaoVisitadas = new ArrayList<Integer>();
		this.cidadesVisitadas = cidadesVisitadas;

		//Constroi a lista de cidades n�o visitadas a partir do que n�o esta presente na lista de cidades visitadas
		for (int i = 0 ; i < cidades.size() ; i++) {
			if (!cidadesVisitadas.contains((cidades.get(i)))) {
				cidadesNaoVisitadas.add(cidades.get(i));
			}
		}

		//Após a construção da lista final, adiciona a cidade inicial a lista
		if (cidadesNaoVisitadas.isEmpty() && cidadesVisitadas.get(cidadesVisitadas.size()-1) != cidadesVisitadas.get(0)) {
			cidadesNaoVisitadas.add(cidadesVisitadas.get(0));
		}
	}
}
