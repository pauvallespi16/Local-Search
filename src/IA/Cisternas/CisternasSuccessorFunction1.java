package IA.Cisternas;

import java.util.*;

import IA.Gasolina.Distribucion;
import IA.Gasolina.Gasolinera;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class CisternasSuccessorFunction1 implements SuccessorFunction {
	@Override
	public List getSuccessors(Object arg0) {
		// TODO Auto-generated method stub
		
		ArrayList<Successor> successors = new ArrayList<>();
		CisternasState state = (CisternasState) arg0;
		
		int nCentros = state.getNumCentros();
		int nGasolineras = state.getNumGasolineras();
		ArrayList<Gasolinera> gas = state.getGasolineras();
		ArrayList<ArrayList<Pair>> itinerario = state.getItinerario();
		
		for (int Ci = 0; Ci < nCentros; Ci++) {				
 			
			//swapGasolineras
			
			for (int Vi = 0; Vi < itinerario.get(Ci).size(); Vi++) {
				for (int Cj = 0; Cj < Ci; Cj++) {
					for (int Vj = 0; Vj < itinerario.get(Cj).size(); Vj++) {
						for (int pos = 0; pos <= 1; pos++) {
							for (int pos2 = 0; pos2 <= 1; pos2++) {
								CisternasState newState = new CisternasState(state);
								if (newState.swapGasolineras(Ci, Cj, Vi, Vj, pos, pos2)) {
									newState.computarBeneficio();
									String S = new String();
									S = "swapGasolineras(Centro: " + Ci + ", Viaje: " + Vi +", Centro: " + Cj + ", Viaje: " + Vj + ", Orden: " + "(" + pos + ", " + pos2 + ")" + ")";	
									successors.add(new Successor(S, newState));
								}
							}
						}
					}
				}
			}

			//swapPeticionNoAsignada
			for (int Vi = 0; Vi < itinerario.get(Ci).size(); Vi++) {
				for (int Pi = 0; Pi <= 1; Pi++) {
					for (int Gj = 0; Gj < nGasolineras; Gj++) {
						for (int Pj = 0; Pj < gas.get(Gj).getPeticiones().size(); Pj++) {
							int dias = gas.get(Gj).getPeticiones().get(Pj);
							CisternasState newState = new CisternasState(state);
							if (newState.swapPeticionNoAsignada(Ci, Vi, Pi, Gj, dias)) {
								newState.computarBeneficio();
								String S = new String();
								S = "swapPeticionNoAsignada(Centro: " + Ci + ", Viaje: " + Vi + ", Posicion: " + Pi + ", Gasolinera: " + Gj + ", Dias: "+ dias + ")";	
								successors.add(new Successor(S, newState));
							}
						}
					}	
				}
			}
			
			
			//addPeticion
			for (int Gi = 0; Gi < nGasolineras; Gi++) {
				for (int pet = 0; pet < gas.get(Gi).getPeticiones().size(); pet++) {
					int Pi = gas.get(Gi).getPeticiones().get(pet);
					CisternasState newState = new CisternasState(state);
					if (newState.addPeticion(Ci, Gi, Pi)) {
						newState.computarBeneficio();
						String S = new String();
						S = "addPeticion(Centro: " + Ci + ", Gasolinera: " + Gi + ", Peticion:  " + Pi + ")";
						successors.add(new Successor(S, newState));
					}
				}
			}
			
		}
		
		return successors;
	}
}