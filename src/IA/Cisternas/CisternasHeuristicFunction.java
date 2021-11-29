package IA.Cisternas;

import aima.search.framework.HeuristicFunction;

public class CisternasHeuristicFunction implements HeuristicFunction {
	@Override
	public double getHeuristicValue(Object arg0) {
		// TODO Auto-generated method stub
		CisternasState state = (CisternasState) arg0;
		return -state.getBeneficio();
	}
}