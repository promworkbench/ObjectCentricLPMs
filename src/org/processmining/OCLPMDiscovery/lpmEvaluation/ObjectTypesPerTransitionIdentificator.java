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

public class ObjectTypesPerTransitionIdentificator implements WindowLPMEvaluator<ObjectTypesPerTransitionResult> {
	
	private Set<String> objectTypes;
	
	public ObjectTypesPerTransitionIdentificator (Set<String> objectTypes) {
		this.objectTypes = objectTypes;
	}

    @Override
    public ObjectTypesPerTransitionResult evaluate(LocalProcessModel lpm,
                                                   LPMTemporaryWindowInfo window,
                                                   LPMEvaluationResult existingEvaluation) {
        if (!(existingEvaluation instanceof ObjectTypesPerTransitionResult)) {
            throw new IllegalArgumentException("The passed evaluation result should be of type " +
            		ObjectTypesPerTransitionResult.class);
        }

        ObjectTypesPerTransitionResult result = (ObjectTypesPerTransitionResult) existingEvaluation;
        
        // check events
        for (int replayedEventIndex : window.getReplayedEventsIndices()) {
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
    				if (count >= 1) {
    					result.registerType(activity, type);
    				}
    			}
    		}
    	}
        
        // check events
        for (int replayedEventIndex : window.getReplayedEventsIndices()) {
        	Iterator<XTrace> it = window.getOriginalTraces().iterator();
        	for (String type : this.objectTypes) {
        		while (it.hasNext()) {
        			// get event
        			XEvent event = it.next().get(replayedEventIndex);
	        		// get activity name
	        		String activity = event.getAttributes().get("concept:name").toString();
	        		if (result.isRegistered(activity, type)) {
	        			// type has already been observed for that activity
	        			break;
	        		}
    				// get number of objects of type
    				Long count = ((XAttributeDiscreteImpl) (event.getAttributes().get(type))).getValue();
    				// do the counting
    				if (count >= 1) {
    					result.registerType(activity, type);
    				}
    			}
    		}
    	}

        return result;
    }

    @Override
    public String getKey() {
        return CustomLPMEvaluatorIds.ObjectTypesPerTransitionIdentificator.name();
    }

    @Override
    public String getResultKey() {
        return CustomLPMEvaluatorResultIds.ObjectTypesPerTransitionResult.name();
    }

    @Override
    public ObjectTypesPerTransitionResult createEmptyResult(LocalProcessModel lpm) {
        return new ObjectTypesPerTransitionResult(lpm);
    }

    @Override
    public Class<ObjectTypesPerTransitionResult> getResultClass() {
        return ObjectTypesPerTransitionResult.class;
    }
}
