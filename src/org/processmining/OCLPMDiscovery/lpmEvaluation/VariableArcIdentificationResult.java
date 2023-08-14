package org.processmining.OCLPMDiscovery.lpmEvaluation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.LPMEvaluationResultId;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.SimpleEvaluationResult;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;

public class VariableArcIdentificationResult extends SimpleEvaluationResult {

	private static final long serialVersionUID = 3670025609204968061L;
	
	private int windowLastEventPos = -1;
	private boolean[] overlapCheck = new boolean[7]; // keep track of events already counted
	private Integer traceVariantId = -1;
	
	private HashMap<List<String>,Integer> scoreCountingSingles = new  HashMap<>(); // maps [Activity,ObjectType] to #events of activity and |OT|=1
	private HashMap<List<String>,Integer> scoreCountingAll = new  HashMap<>(); // maps [Activity,ObjectType] to #events of activity

	public VariableArcIdentificationResult (LocalProcessModel lpm) {
//		super(lpm, new LPMEvaluationResultId()); //TODO
		super(lpm, LPMEvaluationResultId.TransitionOverlappingEvaluationResult);
	}
	
	public void countSingle(String activity, String type) {
		this.scoreCountingSingles.merge(Arrays.asList(activity, type), 1, Integer::sum); // puts 1 if key not present, else adds 1 to it
	}
	
	public void countAll(String activity, String type) {
		this.scoreCountingAll.merge(Arrays.asList(activity, type), 1, Integer::sum); // puts 1 if key not present, else adds 1 to it
	}
	
	public void countSingle(String activity, String type, int amount) {
		this.scoreCountingSingles.merge(Arrays.asList(activity, type), amount, Integer::sum);
	}
	
	public void countAll(String activity, String type, int amount) {
		this.scoreCountingAll.merge(Arrays.asList(activity, type), amount, Integer::sum);
	}
	
	public void shiftOverlapCheck(int newLastPosition) {
		int stepsize = newLastPosition - this.windowLastEventPos;
		for (int i = 0; i<this.overlapCheck.length; i++) {
			if (i + stepsize >= this.overlapCheck.length) {
				this.overlapCheck[i] = false;
			}
			else {
				this.overlapCheck[i] = this.overlapCheck[i+stepsize];
			}
		}
	}
	
	@Override
	/**
	 * Don't use this, this class is only for the variable arc identification!
	 */
    public double getResult() {
        return 0;
    }

    @Override
    /**
	 * Don't use this, this class is only for the variable arc identification!
	 */
    public double getNormalizedResult() {
        return 0;
    }
    
    public HashMap<List<String>,Integer> getScoreCountingSingles(){
    	return this.scoreCountingSingles;
    }

	public int getWindowLastEventPos() {
		return windowLastEventPos;
	}

	public void setWindowLastEventPos(int windowLastEventPos) {
		this.windowLastEventPos = windowLastEventPos;
	}

	public boolean[] getOverlapCheck() {
		return overlapCheck;
	}

	public void setOverlapCheck(boolean[] overlapCheck) {
		this.overlapCheck = overlapCheck;
	}

	public Integer getTraceVariantId() {
		return this.traceVariantId ;
	}

	public void resetOverlapCheck(Integer newTraceId, int windowSize) {
		this.overlapCheck = new boolean[windowSize];
		this.traceVariantId = newTraceId;
	}
}
