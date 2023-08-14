package org.processmining.OCLPMDiscovery.lpmEvaluation;

import java.util.Set;

import org.processmining.placebasedlpmdiscovery.lpmevaluation.lpmevaluators.LPMEvaluatorId;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.lpmevaluators.WindowLPMEvaluator;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.LPMEvaluationResult;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.LPMEvaluationResultId;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.model.fpgrowth.LPMTemporaryWindowInfo;

public class VariableArcIdentificator implements WindowLPMEvaluator<VariableArcIdentificationResult> {
	
	private Set<String> objectTypes;
	
	public VariableArcIdentificator (Set<String> objectTypes) {
		this.objectTypes = objectTypes;
	}

    @Override
    public VariableArcIdentificationResult evaluate(LocalProcessModel lpm,
                                                   LPMTemporaryWindowInfo window,
                                                   LPMEvaluationResult existingEvaluation) {
        if (!(existingEvaluation instanceof VariableArcIdentificationResult)) {
            throw new IllegalArgumentException("The passed evaluation result should be of type " +
            		VariableArcIdentificationResult.class);
        }

        VariableArcIdentificationResult result = (VariableArcIdentificationResult) existingEvaluation;
        
        // beginning new trace variant
        if (window.getTraceVariantId() != result.getTraceVariantId()) {
        	result.resetOverlapCheck(window.getTraceVariantId(), window.getIntegerWindow().size());
        }
        
        //TODO do the counting

        return result;
    }

    @Override
    public String getKey() {
        return LPMEvaluatorId.FittingWindowEvaluator.name();
    }

    @Override
    public String getResultKey() {
        return LPMEvaluationResultId.FittingWindowsEvaluationResult.name();
    }

    @Override
    public VariableArcIdentificationResult createEmptyResult(LocalProcessModel lpm) {
        return new VariableArcIdentificationResult(lpm);
    }

    @Override
    public Class<VariableArcIdentificationResult> getResultClass() {
        return VariableArcIdentificationResult.class;
    }
}
