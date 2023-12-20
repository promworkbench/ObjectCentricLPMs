package org.processmining.OCLPMDiscovery.lpmEvaluation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.LPMEvaluationResult;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.LPMEvaluationResultId;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;

public class ObjectTypesPerTransitionResult implements LPMEvaluationResult {

	private static final long serialVersionUID = 3670025609204968061L;
	private final LPMEvaluationResultId id;
    protected LocalProcessModel lpm;
	
	private HashMap<String,HashSet<String>> mapActivityToTypes = new HashMap<>();

	public ObjectTypesPerTransitionResult (LocalProcessModel lpm) {
		this.lpm = lpm;
		this.id = CustomLPMEvaluatorResultIds.ObjectTypesPerTransitionResult;
	}
	
	public void registerType(String activity, String type) {
		if (this.mapActivityToTypes.containsKey(activity)){
			this.mapActivityToTypes.get(activity).add(type);
		}
		else {
			this.mapActivityToTypes.put(activity, new HashSet<>(Collections.singleton(type)));
		}
	}
	
	@Override
	/**
	 * Don't use this, this class is only for obtaining the mapActivityToTypes
	 */
    public double getResult() {
        return 0;
    }

    @Override
    /**
	 * Don't use this, this class is only for obtaining the mapActivityToTypes
	 */
    public double getNormalizedResult() {
        return 0;
    }
    
    public HashMap<String,HashSet<String>> getMap(){
    	return this.mapActivityToTypes;
    }

	public LPMEvaluationResultId getId() {
		return this.id;
	}

	/**
	 * Check if the type is already stored in the map for this activity.
	 * @param activity
	 * @param type
	 * @return
	 */
	public boolean isRegistered(String activity, String type) {
		if (!this.mapActivityToTypes.containsKey(activity)) {
			return false;
		}
		else {
			return this.mapActivityToTypes.get(activity).contains(type);
		}
	}

}
