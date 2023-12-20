package org.processmining.OCLPMDiscovery.lpmEvaluation;

import java.util.Iterator;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.lpmevaluators.WindowLPMEvaluator;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.LPMEvaluationResult;
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
        
        // beginning of new trace variant
        if (window.getTraceVariantId() != result.getTraceVariantId()) {
        	// reset overlap checker
        	result.resetOverlapCheck(window.getTraceVariantId(), window.getIntegerWindow().size());
        	result.setWindowLastEventPos(window.getWindowLastEventPos());
        }
        
        // shift overlap checker to new window position
        if (result.getOverlapCheck().length != window.getIntegerWindow().size()) {
        	result.adjustOverlapCheckSize(window.getIntegerWindow().size());
        }
        result.shiftOverlapCheck(window.getWindowLastEventPos());
        boolean [] overlapCheck = result.getOverlapCheck();
        
        // count events
        for (int replayedEventIndex : window.getReplayedEventsIndices()) {
        	int indexInWindow = replayedEventIndex - window.getWindowFirstEventPos();
        	if (!overlapCheck[indexInWindow]) { // event hasn't been counted yet
        		Iterator<XTrace> it = window.getOriginalTraces().iterator();
        		while (it.hasNext()) {
        			// get event
        			XEvent event = it.next().get(replayedEventIndex);
        			// get activity name
        			String activity = event.getAttributes().get("concept:name").toString();
        			for (String type : this.objectTypes) {
        				// get number of objects of type
        				XAttributeDiscreteImpl countAtt = (XAttributeDiscreteImpl) (event.getAttributes().get(type)); 
        				Long count = 0l;
        				if (countAtt != null) {
        					count = countAtt.getValue();
        				}
        				// do the counting
        				if (count == 1) {
        					result.countSingle(activity, type);
        				}
        				result.countAll(activity, type); 
        			}
        		}
        		
        		overlapCheck[indexInWindow] = true;
        	}
        }
//        result.setOverlapCheck(overlapCheck);
        result.setWindowLastEventPos(window.getWindowLastEventPos());

        return result;
    }

    @Override
    public String getKey() {
        return CustomLPMEvaluatorIds.VariableArcIdentificator.name();
    }

    @Override
    public String getResultKey() {
        return CustomLPMEvaluatorResultIds.VariableArcIdentificationResult.name();
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
