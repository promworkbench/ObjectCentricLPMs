package org.processmining.OCLPMDiscovery.lpmEvaluation;

import org.processmining.OCLPMDiscovery.parameters.OCLPMEvaluationMetrics;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.lpmevaluators.LPMEvaluatorId;

public class VariableArcIdentificatorEvaluatorId implements LPMEvaluatorId{
//TODO remove this class, I probably don't need it
	public String name() {
		return OCLPMEvaluationMetrics.TYPE_USAGE.name();
	}
}
