package IA.Cisternas;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.*;

import IA.Gasolina.CentrosDistribucion;
import IA.Gasolina.Gasolinera;
import IA.Gasolina.Gasolineras;

public class Main {
	static int mediaBeneficio = 0;
	static int mediaTiempo = 0;
	
	public static void main(String[] args) throws Exception{
		introduction();

		// DEFINICIÓN DE PARÁMETROS DEL TESTING //
		//--------------------------------------//
		Integer mult         = 	         1;
		Integer seed         = 		  4781;
    	Integer itMax        = 	    150000;						//Iteraciones máximas del algoritmo
    	Integer itMaxT       = 			10;						//Iteraciones máximas por valor de temperatura
    	Integer k            =	  	     1;						//Parámetro de criterio de la función de temperatura
    	Double lambda        = 		0.0001;						//Parámetro de criterio de la función de temperatura
    	//-------------------------------------//

		Boolean close = false;
		while (!close) {
			Integer ngas = 100, ncent  = 10;
			Boolean Random = true, HillClimbing = true;
			Scanner in = new Scanner(System.in);
			System.out.println("Introduce número de gasolineras: ");
			ngas  = in.nextInt();
			System.out.println();
			System.out.println("Introduce número de centros: ");
			ncent = in.nextInt();
			System.out.println();

			System.out.println("Introduce el tipo de solución inicial: ");
			System.out.println("	Presiona 1: Solución Random");
			System.out.println("	Presiona 2: Solución Greedy");

			Integer solution = in.nextInt();
			if (solution == 1) Random = true;
			else Random = false;
			System.out.println();

			System.out.println("Introduce el algoritmo: ");
			System.out.println("	Presiona 1: Hill Climbing");
			System.out.println("	Presiona 2: Simulated Annealing");

			solution = in.nextInt();
			if (solution == 1) HillClimbing = true;
			else HillClimbing = false;
			System.out.println();

			execute(ngas, ncent, mult, seed, Random, HillClimbing, itMax, itMaxT, k, lambda);

			System.out.println();
			System.out.println("¿Desea continuar?");
			System.out.println("	Escriba SI para continuar");
			System.out.println("	Escriba NO para parar");
			String s = in.next();
			s = s.trim(); s = s.toLowerCase();
			if (s.equals("no"))
				close = true;
			System.out.println("-----------------------------------------------------------");
			System.out.println();
		}
    }

	public static void introduction() {
		System.out.println("-----------------------------------------------------------");
		System.out.println("                  INTELIGENCIA ARTIFICIAL                  ");
		System.out.println("-----------------------------------------------------------");
		System.out.println("Miembros: Pau Vallespí, Andrés Bercowsky y Guillem González");
		System.out.println(); System.out.println();

	}

	private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
        
    }
    
    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
    
    public static void generarSolucionInicial(CisternasState state, boolean Random) {
    	if (Random) state.setInitialSolutionRandom();
    	else state.setInitialSolutionGreedy();
    }
    
    public static void CisternasHillClimbing(CisternasState state, int ncent) throws Exception {
    	long start = System.currentTimeMillis();
    	Problem p = new Problem(state,
				new CisternasSuccessorFunction1(),
				new CisternasGoalTest(),
				new CisternasHeuristicFunction());
		Search alg = new HillClimbingSearch();
		SearchAgent agent = new SearchAgent(p, alg);
		CisternasState newState = (CisternasState) alg.getGoalState();
		long end = System.currentTimeMillis();
		//printActions(agent.getActions());
        //printInstrumentation(agent.getInstrumentation());
		System.out.println("-----------------------------------------------------------");
		System.out.println("	BENEFICIO: " + (newState.getBeneficio() + newState.perdidasPorPeticionesNoAsignadas()));
		System.out.println("	TIEMPO:    " + (end-start) + "ms");
		System.out.println("-----------------------------------------------------------");
        mediaBeneficio += newState.getBeneficio() + newState.perdidasPorPeticionesNoAsignadas();
        mediaTiempo += (end-start);
    }
    
    public static void CisternasSimulatedAnnealing(CisternasState state, int ncent, Integer itMax, Integer itMaxT,
    		Integer k, Double lambda) throws Exception{
    	long start = System.currentTimeMillis();
    	Problem p = new Problem(state,
				new CisternasSuccessorFunction2(),
				new CisternasGoalTest(),
				new CisternasHeuristicFunction());
		Search alg = new SimulatedAnnealingSearch(itMax, itMaxT, k, lambda);
		SearchAgent agent = new SearchAgent(p, alg);
		CisternasState newState = (CisternasState) alg.getGoalState();
		long end = System.currentTimeMillis();
        System.out.println();
		//printActions(agent.getActions());
        //printInstrumentation(agent.getInstrumentation());
		System.out.println("-----------------------------------------------------------");
		System.out.println("BENEFICIO: " + (newState.getBeneficio() + newState.perdidasPorPeticionesNoAsignadas()));
		System.out.println("TIEMPO:    " + (end-start) + "ms");
		System.out.println("-----------------------------------------------------------");
        mediaBeneficio += newState.getBeneficio() + newState.perdidasPorPeticionesNoAsignadas();
        mediaTiempo += (end-start);
    }
    
    public static void execute(Integer ngas, Integer ncent, Integer mult, Integer seed, boolean Random, boolean HillClimbing,
    		Integer itMax, Integer itMaxT, Integer k, Double lambda) throws Exception{

		Gasolineras gas = new Gasolineras(ngas, seed);
		CentrosDistribucion cent = new CentrosDistribucion(ncent, mult, seed);
		CisternasState state = new CisternasState(gas, cent);

		generarSolucionInicial(state, Random);

		if (HillClimbing) CisternasHillClimbing(state, ncent);
		else CisternasSimulatedAnnealing(state, ncent, itMax, itMaxT, k, lambda);
    }
}