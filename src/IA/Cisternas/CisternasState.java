package IA.Cisternas;

import java.util.*;

import IA.Gasolina.*;
public class CisternasState {
	final static int numViajes = 5;
	final static int numKilometros = 640;
	final static int precioKm = 2;
	final static int valorDeposito = 1000;

	private int beneficio;

	private Gasolineras gas;
	private CentrosDistribucion cent;

	private ArrayList <Integer> kilometros;
	private ArrayList <ArrayList <Pair> > itinerario; // [cisterna1][viaje1, viaje2, ... , viajen]
	private ArrayList <ArrayList <Pair> > peticiones; // [cisterna1][<peticion1, peticion2>, <peticion3, peticion4>...]

	// Constructoras
	public CisternasState(Gasolineras gas, CentrosDistribucion cent) {
		this.beneficio = 0;

		this.gas = gas;

		this.cent = cent;

		int ncen = cent.size();

		// kilometros
		kilometros = new ArrayList <Integer> (ncen);
		for (int i = 0; i < ncen; i++) kilometros.add(0);

		// itinerario
		itinerario = new ArrayList <> (ncen);
		for (int i = 0; i < ncen; i++) {
			itinerario.add(new ArrayList<> ());
			for (int j = 0; j < numViajes; j++) {
				itinerario.get(i).add(new Pair());
			}

		}
		// peticiones
		peticiones = new ArrayList <> (ncen);
		for (int i = 0; i < ncen; i++) {
			peticiones.add(new ArrayList<> ());
			for (int j = 0; j < numViajes; j++) {
				peticiones.get(i).add(new Pair());
			}
		}
	}

	public CisternasState(CisternasState estado) {
		gas = estado.gas;
		cent = estado.cent;
		
		kilometros = estado.kilometros;                       
		itinerario = new ArrayList<ArrayList<Pair>>(estado.itinerario);  
		peticiones = new ArrayList<ArrayList<Pair>>(estado.peticiones);
		beneficio = estado.beneficio;
	}

	// Getters
	public int getBeneficio() { return beneficio; }

	public int getNumGasolineras() { return gas.size(); }

	public int getNumCentros() { return cent.size(); }
	
	public int getTotalKilometros() { int sum = 0; for (int km : kilometros) sum += km; return sum; }

	public ArrayList <Integer> getKilometros() { return kilometros; }

	public ArrayList <ArrayList <Pair> > getItinerario() { return itinerario; }

	public ArrayList <ArrayList <Pair> > getPeticiones() { return peticiones; }

	public ArrayList <Gasolinera> getGasolineras() { return gas; }

	public ArrayList <Distribucion> getCentrosDistribucion() { return cent; }

	// Setters
	public void setInitialSolutionRandom() {
		//Vamos a generar una solucion mala inicial para que Hill climbing / Simulated annealing tenga que trabajar
		//Utilizamos la estrategia de llenar cisternas hasta que no puedan contener mÃ¡s y pasar a la siguiente
		Random gasRandom = new Random();
		Random petRandom = new Random();
		int peticionesRestantes = 0;
		for (int i = 0; i < gas.size(); i++) {
			peticionesRestantes += gas.get(i).getPeticiones().size();
		}

		for (int Ci = 0; Ci < cent.size(); Ci++) {
			for (int Vi = 0; Vi < numViajes; Vi++) {
				//Centro i
				Distribucion c = cent.get(Ci);
				int diasP1 = 0, diasP2 = 0;
				int control = 10*gas.size();
				//Buscar 2 gasolineras random
				//Gasolinera 1
				int gasId = gasRandom.nextInt(gas.size());
				int dist = calcularDistancia(c.getCoordX(), c.getCoordY(), gas.get(gasId).getCoordX(), gas.get(gasId).getCoordY());

				while (control > 0 && peticionesRestantes > 0 && (2*dist + kilometros.get(Ci) > numKilometros || gas.get(gasId).getPeticiones().size() == 0)) {
					gasId = gasRandom.nextInt(gas.size());
					dist = calcularDistancia(c.getCoordX(), c.getCoordY(), gas.get(gasId).getCoordX(), gas.get(gasId).getCoordY());
					control--;
				}

				if (control != 0) {
					int auxDist = dist;
					if (peticionesRestantes == 0)
						return;

					int petId = petRandom.nextInt(gas.get(gasId).getPeticiones().size());

					Gasolinera firstGas = gas.get(gasId);
					petId = petRandom.nextInt(gas.get(gasId).getPeticiones().size());
					ArrayList<Integer> peticionesGas1 = firstGas.getPeticiones();
					diasP1 = peticionesGas1.get(petId);
					peticionesGas1.remove(petId);
					gas.get(gasId).setPeticiones(peticionesGas1);

					kilometros.set(Ci, 2*dist+kilometros.get(Ci));
					itinerario.get(Ci).get(Vi).setKey(gasId);
					peticiones.get(Ci).get(Vi).setKey(diasP1);

					peticionesRestantes--;
					control = 10*gas.size();
					
					//Gasolinera 2		
					int gasId2 = gasRandom.nextInt(gas.size());
					int dist2 = calcularDistancia(gas.get(gasId2).getCoordX(), gas.get(gasId2).getCoordY(), c.getCoordX(), c.getCoordY())
							    + calcularDistancia(gas.get(gasId2).getCoordX(), gas.get(gasId2).getCoordY(), gas.get(gasId).getCoordX(), gas.get(gasId).getCoordY());
					while (control > 0 && peticionesRestantes > 0 && (dist2 + kilometros.get(Ci) - auxDist > numKilometros || gas.get(gasId2).getPeticiones().size() == 0)) {
						gasId2 = gasRandom.nextInt(gas.size());
						dist2 = calcularDistancia(gas.get(gasId2).getCoordX(), gas.get(gasId2).getCoordY(), c.getCoordX(), c.getCoordY())
					    		+ calcularDistancia(gas.get(gasId2).getCoordX(), gas.get(gasId2).getCoordY(), gas.get(gasId).getCoordX(), gas.get(gasId).getCoordY());

						control--;
					}

					if (control != 0) {
						if (peticionesRestantes == 0)
							return;

						int petId2 = petRandom.nextInt(gas.get(gasId2).getPeticiones().size());

						Gasolinera secondGas = gas.get(gasId2);
						petId2 = petRandom.nextInt(gas.get(gasId2).getPeticiones().size());
						ArrayList<Integer> peticionesGas2 = secondGas.getPeticiones();
						diasP2 = peticionesGas2.get(petId2);
						peticionesGas2.remove(petId2);
						gas.get(gasId2).setPeticiones(peticionesGas2);

						kilometros.set(Ci, dist2+kilometros.get(Ci)-auxDist);
						itinerario.get(Ci).get(Vi).setValue(gasId2);
						peticiones.get(Ci).get(Vi).setValue(diasP2);

						peticionesRestantes--;
					}
				}
			}

		}
		computarBeneficio();
	}

	public void setInitialSolutionGreedy() {
		//Vamos a generar una solucion mala inicial para que Hill climbing / Simulated annealing tenga que trabajar
		//Utilizamos la estrategia de llenar cisternas hasta que no puedan contener mÃ¡s y pasar a la siguiente

		for (int Vi = 0; Vi < numViajes; Vi++) {
			for (int Ci = 0; Ci < cent.size(); Ci++) {
				//Centro i
				Distribucion c = cent.get(Ci);
				int index = 0, diasP1 = 0, diasP2 = 0;

				//Buscar 2 gasolineras mas cercanas
				Gasolinera firstClosestGas = null;
				ArrayList<Integer> peticionesGas1 = new ArrayList<>();

				firstClosestGas = findClosestGas(c.getCoordX(), c.getCoordY());

				int peticionMasAntigua = Integer.MIN_VALUE;
				if (firstClosestGas != null) {
					peticionesGas1 = firstClosestGas.getPeticiones();

					for (int Pi = 0; Pi < peticionesGas1.size(); Pi++) {
						if (peticionesGas1.get(Pi) > peticionMasAntigua) {
							peticionMasAntigua = peticionesGas1.get(Pi);
							index = Pi;
						}
					}
					if (2*calcularDistancia(firstClosestGas.getCoordX(), firstClosestGas.getCoordY(), c.getCoordX(), c.getCoordY()) +kilometros.get(Ci)<= numKilometros) {
						diasP1 = peticionesGas1.get(index);
						peticionesGas1.remove(index);
						gas.get(gas.indexOf(firstClosestGas)).setPeticiones(peticionesGas1);
					} else return;

					Gasolinera secondClosestGas = null;
					ArrayList<Integer> peticionesGas2 = new ArrayList<>();

					int distancia = calcularDistancia(firstClosestGas.getCoordX(), firstClosestGas.getCoordY(), c.getCoordX(), c.getCoordY());


					secondClosestGas = findClosestGas(c.getCoordX(), c.getCoordY());

					if (secondClosestGas != null) {
						peticionesGas2 = secondClosestGas.getPeticiones();

						peticionMasAntigua = Integer.MIN_VALUE;
						for (int Pi = 0; Pi < peticionesGas1.size(); Pi++) {
							if (peticionesGas1.get(Pi) > peticionMasAntigua) {
								peticionMasAntigua = peticionesGas1.get(Pi);
								index = Pi;
							}
						}
						int dist = calcularDistancia(firstClosestGas.getCoordX(), firstClosestGas.getCoordY(), c.getCoordX(), c.getCoordY());
						int auxDist = dist;
						if (2*dist + kilometros.get(Ci) <= numKilometros) {
							dist += calcularDistancia(secondClosestGas.getCoordX(), secondClosestGas.getCoordY(), c.getCoordX(), c.getCoordY());
							dist += calcularDistancia(firstClosestGas.getCoordX(), firstClosestGas.getCoordY(), secondClosestGas.getCoordX(), secondClosestGas.getCoordY());
							dist += kilometros.get(Ci);
							if (dist <= numKilometros) {
								kilometros.set(Ci, dist);
								diasP2 = peticionesGas2.get(index);
								peticionesGas2.remove(index);
								gas.get(gas.indexOf(secondClosestGas)).setPeticiones(peticionesGas2);
								Pair p = new Pair(diasP1, diasP2);
								peticiones.get(Ci).set(Vi, p);

								p = new Pair(gas.indexOf(firstClosestGas), gas.indexOf(secondClosestGas));
								itinerario.get(Ci).set(Vi, p);
							} else {
								kilometros.set(Ci, 2*auxDist + kilometros.get(Ci));
								Pair p = new Pair(diasP1, -1);
								peticiones.get(Ci).set(Vi, p);

								p = new Pair(gas.indexOf(firstClosestGas), -1);
								itinerario.get(Ci).set(Vi, p);
							}
						}
					} else {
						int dist = 2*calcularDistancia(firstClosestGas.getCoordX(), firstClosestGas.getCoordY(), c.getCoordX(), c.getCoordY());
						if (dist + kilometros.get(Ci) <= numKilometros) {
							kilometros.set(Ci, dist + kilometros.get(Ci));
							Pair p = new Pair(diasP1, -1);
							peticiones.get(Ci).set(Vi, p);


							p = new Pair(gas.indexOf(firstClosestGas), -1);
							itinerario.get(Ci).set(Vi, p);
						}
					}
				}
			}
		}
		computarBeneficio();
	}

	// Operadores
	public boolean swapGasolineras(int Ci, int Cj, int Vi, int Vj, int positionFirst, int positionSecond)  {
		int Gi, Gj;
		int Gi2, Gj2;
		if (positionFirst == 0) {
			Gi = itinerario.get(Ci).get(Vi).getKey();
			Gi2 = itinerario.get(Ci).get(Vi).getValue();
		}
		else {
			Gi = itinerario.get(Ci).get(Vi).getValue();
			Gi2 = itinerario.get(Ci).get(Vi).getKey();
		}
		if (positionSecond == 0) {
			Gj = itinerario.get(Cj).get(Vj).getKey();
			Gj2 = itinerario.get(Cj).get(Vj).getValue();
		}
		else {
			Gj = itinerario.get(Cj).get(Vj).getValue();
			Gj2 = itinerario.get(Cj).get(Vj).getKey();
		}

		if (Gi == -1 || Gj == -1)
			return false;

		int Cix = cent.get(Ci).getCoordX();
		int Ciy = cent.get(Ci).getCoordY();
		int Cjx = cent.get(Cj).getCoordX();
		int Cjy = cent.get(Cj).getCoordY();

		int Gix = gas.get(Gi).getCoordX();
		int Giy = gas.get(Gi).getCoordY();
		int Gjx = gas.get(Gj).getCoordX();
		int Gjy = gas.get(Gj).getCoordY();
		int Gix2, Giy2, Gjx2, Gjy2;

 		if (Gi2 == -1) {
 			Gix2 = Cix;
 			Giy2 = Ciy;
 		}
 		else {
 			Gix2 = gas.get(Gi2).getCoordX();
 			Giy2 = gas.get(Gi2).getCoordY();
 		}
 		if (Gj2 == -1) {
 			Gjx2 = Cjx;
 			Gjy2 = Cjy;
 		}
 		else {
 			Gjx2 = gas.get(Gj2).getCoordX();
 			Gjy2 = gas.get(Gj2).getCoordY();
 		}

		int old_kilometros1 = calcularDistancia(Cix, Ciy, Gix, Giy) + calcularDistancia(Gix, Giy, Gix2, Giy2) + calcularDistancia(Cix, Ciy, Gix2, Giy2);
		int new_kilometros1 = calcularDistancia(Cix, Ciy, Gjx, Gjy) + calcularDistancia(Gix2, Giy2, Gjx, Gjy) + calcularDistancia(Cix, Ciy, Gix2, Giy2);

		int old_kilometros2 = calcularDistancia(Cjx, Cjy, Gjx, Gjy) + calcularDistancia(Gjx, Gjy, Gjx2, Gjy2) + calcularDistancia(Cjx, Cjy, Gjx2, Gjy2);
		int new_kilometros2 = calcularDistancia(Cjx, Cjy, Gix, Giy) + calcularDistancia(Gjx2, Gjy2, Gix, Giy) + calcularDistancia(Cjx, Cjy, Gjx2, Gjy2);

		if ((new_kilometros1-old_kilometros1+kilometros.get(Ci)) <= numKilometros && (new_kilometros2-old_kilometros2+kilometros.get(Cj)) <= numKilometros) {
			kilometros.set(Ci, new_kilometros1 - old_kilometros1 + kilometros.get(Ci));
			kilometros.set(Cj, new_kilometros2 - old_kilometros2 + kilometros.get(Cj));

			int dias1 = 0, dias2 = 0;

			if (positionFirst == 0) {
				if (positionSecond == 0) {
					// set viaje1<Gj, _>, viaje2<Gi, _>
					itinerario.get(Ci).get(Vi).setKey(Gj);
					itinerario.get(Cj).get(Vj).setKey(Gi);

					// set peticiones
					dias1 = peticiones.get(Ci).get(Vi).getKey();
					dias2 = peticiones.get(Cj).get(Vj).getKey();
					peticiones.get(Ci).get(Vi).setKey(dias2);
					peticiones.get(Cj).get(Vj).setKey(dias1);
				} else {
					// set viaje1<Gj, _>, viaje2<_, Gi>
					itinerario.get(Ci).get(Vi).setKey(Gj);
					itinerario.get(Cj).get(Vj).setValue(Gi);

					// set peticiones
					dias1 = peticiones.get(Ci).get(Vi).getKey();
					dias2 = peticiones.get(Cj).get(Vj).getValue();
					peticiones.get(Ci).get(Vi).setKey(dias2);
					peticiones.get(Cj).get(Vj).setValue(dias1);
				}
			} else {
				if (positionSecond == 0) {
					// set viaje1<_, Gj>, viaje2<Gi, _>
					itinerario.get(Ci).get(Vi).setValue(Gj);
					itinerario.get(Cj).get(Vj).setKey(Gi);

					// set peticiones
					dias1 = peticiones.get(Ci).get(Vi).getValue();
					dias2 = peticiones.get(Cj).get(Vj).getKey();
					peticiones.get(Ci).get(Vi).setValue(dias2);
					peticiones.get(Cj).get(Vj).setKey(dias1);
				} else {
					// set viaje1<_, Gj>, viaje2<_, Gi>
					itinerario.get(Ci).get(Vi).setValue(Gj);
					itinerario.get(Cj).get(Vj).setValue(Gi);

					// set peticiones
					dias1 = peticiones.get(Ci).get(Vi).getValue();
					dias2 = peticiones.get(Cj).get(Vj).getValue();
					peticiones.get(Ci).get(Vi).setValue(dias2);
					peticiones.get(Cj).get(Vj).setValue(dias1);
				}
			}
			return true;
		}
		return false;
	}

	public boolean swapPeticionNoAsignada(int Ci, int Vi, int Pi, int Gj, int Pj) {
		int Gi;
		int Gi2;
		if (Pi == 0) {
			Gi = itinerario.get(Ci).get(Vi).getKey();
			Gi2 = itinerario.get(Ci).get(Vi).getValue();
		}
		else {
			Gi = itinerario.get(Ci).get(Vi).getValue();
			Gi2 = itinerario.get(Ci).get(Vi).getKey();
		}

		if (Gi == -1) return false;

		int Cix = cent.get(Ci).getCoordX();
		int Ciy = cent.get(Ci).getCoordY();

		int Gix = gas.get(Gi).getCoordX();
		int Giy = gas.get(Gi).getCoordY();
		int Gjx = gas.get(Gj).getCoordX();
		int Gjy = gas.get(Gj).getCoordY();

		int Gix2, Giy2;
		if (Gi2 == -1) {
			Gix2 = Cix;
			Giy2 = Ciy;
		}
		else {
			Gix2 = gas.get(Gi2).getCoordX();
			Giy2 = gas.get(Gi2).getCoordY();
		}

		int old_kilometros1 = calcularDistancia(Cix, Ciy, Gix, Giy) + calcularDistancia(Gix, Giy, Gix2, Giy2) + calcularDistancia(Cix, Ciy, Gix2, Giy2);
		int new_kilometros1 = calcularDistancia(Cix, Ciy, Gjx, Gjy) + calcularDistancia(Gix2, Giy2, Gjx, Gjy) + calcularDistancia(Cix, Ciy, Gix2, Giy2);
		if ((new_kilometros1-old_kilometros1+kilometros.get(Ci)) <= numKilometros) {
			kilometros.set(Ci, new_kilometros1 - old_kilometros1 + kilometros.get(Ci));

			int dias;

			if (Pi == 0) {
				itinerario.get(Ci).get(Vi).setKey(Gj);
				dias = peticiones.get(Ci).get(Vi).getKey();
				peticiones.get(Ci).get(Vi).setKey(Pj);

				ArrayList<Integer> petGi = gas.get(Gi).getPeticiones();
				petGi.add(dias);
				gas.get(Gi).setPeticiones(petGi);

				ArrayList<Integer> petGj = gas.get(Gj).getPeticiones();
				petGj.remove(petGj.indexOf(Pj));
				gas.get(Gj).setPeticiones(petGj);

			} else {
				itinerario.get(Ci).get(Vi).setValue(Gj);

				dias = peticiones.get(Ci).get(Vi).getValue();

				ArrayList<Integer> petGi = gas.get(Gi).getPeticiones();
				petGi.add(dias);
				gas.get(Gi).setPeticiones(petGi);

				ArrayList<Integer> petGj = gas.get(Gj).getPeticiones();
				petGj.remove(petGj.indexOf(Pj));
				gas.get(Gj).setPeticiones(petGj);

				peticiones.get(Ci).get(Vi).setValue(Pj);
			}
			return  true;
		}
		return false;
	}

	public boolean addPeticion(Integer Ci, Integer Gj, Integer Pj) {
		if (!gas.get(Gj).getPeticiones().contains(gas.get(Gj).getPeticiones().indexOf(Pj))) return false;

		int Cix = cent.get(Ci).getCoordX();
		int Ciy = cent.get(Ci).getCoordY();

		int Gjx = gas.get(Gj).getCoordX();
		int Gjy = gas.get(Gj).getCoordY();

		//Buscamos el primer pair que uno de los elementos sea -1
		int i;
		for (i = 0; i < numViajes; i++) {
			if (itinerario.get(Ci).get(i).getKey() == -1 || itinerario.get(Ci).get(i).getValue() == -1) break;
		}
		if (i == numViajes) return false;

		Pair viaje = itinerario.get(Ci).get(i);
		Pair peticion = peticiones.get(Ci).get(i);
		int Gjx2 = -1;
		int Gjy2 = -1;

		if (viaje.getKey() != -1) {
			Gjx2 = gas.get(viaje.getKey()).getCoordX();
			Gjy2 = gas.get(viaje.getKey()).getCoordY();

			int new_distancia = calcularDistancia(Cix, Ciy, Gjx, Gjy) + calcularDistancia(Gjx, Gjy, Gjx2, Gjy2)
								- calcularDistancia(Cix, Ciy, Gjx2, Gjy2);
			if (new_distancia + kilometros.get(Ci) <= numKilometros) {
				viaje.setValue(Gj); peticion.setValue(Pj);
				itinerario.get(Ci).set(i, viaje);
				peticiones.get(Ci).set(i, peticion);
				kilometros.set(Ci, new_distancia + kilometros.get(Ci));

				ArrayList<Integer> pet = gas.get(Gj).getPeticiones();
				pet.remove(pet.indexOf(Pj));
				gas.get(Gj).setPeticiones(pet);
				return true;
			}
		}
		else {
			int new_distancia = 2 * calcularDistancia(Cix, Ciy, Gjx, Gjy);
			if (new_distancia + kilometros.get(Ci) <= numKilometros) {
				Pair new_viaje = new Pair();
				new_viaje.setKey(Gj); peticion.setKey(Pj);
				itinerario.get(Ci).set(i, new_viaje);
				peticiones.get(Ci).set(i, peticion);
				kilometros.set(Ci, new_distancia + kilometros.get(Ci));
				ArrayList<Integer> pet = gas.get(Gj).getPeticiones();
				pet.remove(pet.indexOf(Pj));
				gas.get(Gj).setPeticiones(pet);
				return true;
			}
		}
		return false;
	}

	//Funciones auxiliares

	// PRIVATE
	public void computarBeneficio() {
		int b = 0;
		for (int i = 0; i < cent.size(); i++) {
			int Cx = cent.get(i).getCoordX();
			int Cy = cent.get(i).getCoordY();
			for (int j = 0; j < itinerario.get(i).size(); j++) {
				int Gi = itinerario.get(i).get(j).getKey(), Gj = itinerario.get(i).get(j).getValue();
				if (Gi != -1) {
					b = b - calcularDistancia(Cx, Cy, gas.get(Gi).getCoordX(), gas.get(Gi).getCoordY())*precioKm;
					b = b + calcularBeneficio(peticiones.get(i).get(j).getKey());
					if (Gj != -1) {
						b = b - calcularDistancia(Cx, Cy, gas.get(Gj).getCoordX(), gas.get(Gj).getCoordY())*precioKm;
						b = b - calcularDistancia(gas.get(Gi).getCoordX(), gas.get(Gi).getCoordY(), gas.get(Gj).getCoordX(), gas.get(Gj).getCoordY())*precioKm;
						b = b + calcularBeneficio(peticiones.get(i).get(j).getValue());
					}
					else b = b - calcularDistancia(Cx, Cy, gas.get(Gi).getCoordX(), gas.get(Gi).getCoordY())*precioKm;
				}
			}
		}

		for (int i = 0; i < gas.size(); i++) {
			for (int j = 0; j < gas.get(i).getPeticiones().size(); j++) {
				b = b - calcularBeneficio(gas.get(i).getPeticiones().get(j));
			}
		}
		beneficio = b;
	}

	public int perdidasPorPeticionesNoAsignadas() {
		int perdidas = 0;
		for (int i = 0; i < gas.size(); i++) {
			for (int j = 0; j < gas.get(i).getPeticiones().size(); j++) {
				perdidas = perdidas + calcularBeneficio(gas.get(i).getPeticiones().get(j));
			}
		}
		return perdidas;
	}

	private int calcularBeneficio(int dias) {
		if (dias == 0) return (int) (1.02*valorDeposito);
		return (int) (100 - Math.pow(2, dias)) * valorDeposito / 100;
	 }

	public int calcularDistancia(int Di, int Dj, int Gi, int Gj) { return Math.abs(Di - Gi) + Math.abs(Dj - Gj); }

	private Gasolinera findClosestGas(int X, int Y) {
		int minDist = Integer.MAX_VALUE;
		Gasolinera gasolinera = null;
		for (Gasolinera g : gas) {
			int x = g.getCoordX();
			int y = g.getCoordY();

			if (g.getCoordX() == X && g.getCoordY() == Y)
				if (g.getPeticiones().size() > 1) return g;

			int aux = calcularDistancia(X, Y, x, y);
			if (g.getPeticiones().size() > 0 && aux < minDist) {
				minDist = aux;
				gasolinera = g;
			}
		}
		return gasolinera;
	}

}