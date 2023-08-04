package org.processmining.OCLPMDiscovery.parameters;

public enum OCLPMEvaluationMetrics {
	FITTING_WINDOWS ("Fitting Window Score"), // 
	PASSAGE_COVERAGE ("Passage Coverage Score"), //
	PASSAGE_REPETITION ("Passage Repetition Score"), //
	TRANSITION_COVERAGE ("Transition Coverage Score"), //
	COMBINED_SCORE ("Combined Score"), // 
	TRACE_SUPPORT ("Trace Support Score"), //
	;
	
	private final String name;
	
	OCLPMEvaluationMetrics(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}