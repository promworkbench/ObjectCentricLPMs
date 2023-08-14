package org.processmining.OCLPMDiscovery.parameters;

public enum OCLPMEvaluationMetrics {
	FITTING_WINDOWS ("Fitting Window Score"), // 
	PASSAGE_COVERAGE ("Passage Coverage Score"), //
	PASSAGE_REPETITION ("Passage Repetition Score"), //
	TRANSITION_COVERAGE ("Transition Coverage Score"), //
	TRACE_SUPPORT ("Trace Support Score"), //
	TYPE_USAGE ("Type Usage Score"), // Measures how well the object types are used in order to most clearly show the process to an observer.
	COMBINED_SCORE ("Combined Score"), // 
	;
	
	private final String name;
	
	OCLPMEvaluationMetrics(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}