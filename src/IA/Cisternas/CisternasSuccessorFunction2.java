package IA.Cisternas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import IA.Gasolina.Gasolinera;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class CisternasSuccessorFunction2 implements SuccessorFunction {
	@Override
	public List getSuccessors(Object arg0) {
		// TODO Auto-generated method stub
		ArrayList<Successor> successors = new ArrayList<>();
		CisternasState state = (CisternasState) arg0;
		
		int nGasolineras = state.getNumGasolineras();
		int nCentros = state.getNumCentros();
		
		ArrayList <ArrayList <Pair> > itinerarios = state.getItinerario();
		ArrayList <ArrayList <Pair> > peticiones = state.getPeticiones();
		ArrayList<Gasolinera> gas = state.getGasolineras();
		
		Random r = new Random();
		int c = r.nextInt(3);
		
		int Ci = 0;
		int Cj, Vi, Vj, Pi;
		Boolean foundSuccessor = false;
		int iterations_left = 10*nGasolineras;
		
		while (!foundSuccessor && iterations_left > 0) {
			CisternasState newState = new CisternasState(state);
			switch(c) {
				case 0:
					// swapGasolineras
					Ci = r.nextInt(nCentros);
					Cj = Ci;
					int count = 10*nGasolineras;
					while ((Ci == Cj || itinerarios.get(Ci).size() == 0 || itinerarios.get(Cj).size() == 0) && --count > 0) {
						Ci = r.nextInt(nCentros);		//NUEVO
						Cj = r.nextInt(nCentros);
					}
					if (count == 0) break;
					
					Vi = r.nextInt(itinerarios.get(Ci).size());
					Vj = r.nextInt(itinerarios.get(Cj).size());
					
					int pos = r.nextInt(2);
					int pos2 = r.nextInt(2);
					
					if (newState.swapGasolineras(Ci, Cj, Vi, Vj, pos, pos2)) {
						newState.computarBeneficio();
						String S = new String();
						S = "swapGasolineras(Centro: " + Ci + ", Viaje: " + Vi +", Centro: " + Cj + ", Viaje: " + Vj + ", Orden: " + "(" + pos + ", " + pos2 + ")" + ")";	
						successors.add(new Successor(S, newState));
					}
					foundSuccessor = true;
					break;
					
				case 1:
					// swapPeticionNoAsignada
					Ci = r.nextInt(nCentros);
					
					Vi = r.nextInt(itinerarios.get(Ci).size());
					count = 10*nGasolineras;
					while (itinerarios.get(Ci).get(Vi).getKey() == -1 && --count > 0) {
						Vi = r.nextInt(itinerarios.get(Ci).size());
					}
					if (count == 0) break;
					
					if (itinerarios.get(Ci).get(Vi).getKey() != -1 && itinerarios.get(Ci).get(Vi).getValue() != -1) {
						Pi = r.nextInt(2);
					} else Pi = 0;
					
					count = 10*nGasolineras;
					int Gj = r.nextInt(nGasolineras);
					while (gas.get(Gj).getPeticiones().size() == 0 && --count > 0) {
						Gj = r.nextInt(nGasolineras);
					}
					if (count == 0) break;
					
					int npeticiones = gas.get(Gj).getPeticiones().size();
					int Pj = r.nextInt(npeticiones);
					int dias = gas.get(Gj).getPeticiones().get(Pj);
					
					if (newState.swapPeticionNoAsignada(Ci, Vi, Pi, Gj, dias)) {
						newState.computarBeneficio();
						String S = new String();
						S = "swapPeticionNoAsignada(Centro: " + Ci + ", Viaje: " + Vi + ", Peticion: " + Pi + ", Gasolinera: " + Gj + ", Dias: "+ dias + ")";	
						successors.add(new Successor(S, newState));
					}
					foundSuccessor = true;
					break;
					
				case 2:
					// addPeticion
					Ci = r.nextInt(nCentros);
					int Gi = r.nextInt(nGasolineras);
					count = 10*nGasolineras;
					while(gas.get(Gi).getPeticiones().size() == 0 && --count > 0) {
						Gi = r.nextInt(nGasolineras);
					}
					if (count == 0) break;
					int pet = r.nextInt(gas.get(Gi).getPeticiones().size());
					
					Pi = gas.get(Gi).getPeticiones().get(pet);
					if (newState.addPeticion(Ci, Gi, Pi)) {
						newState.computarBeneficio();
						String S = new String();
						S = "addPeticion(Centro: " + Ci + ", Gasolinera: " + Gi + ", Peticion: " + Pi + ")";
						successors.add(new Successor(S, newState));
					}
					foundSuccessor = true;
					break;
			}
			iterations_left--;
			c = r.nextInt(3);		//AÑADIDO NUEVO
		}
		return successors;
	}
}