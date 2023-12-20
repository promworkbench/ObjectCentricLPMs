package org.processmining.OCLPMDiscovery.parameters;

public enum OCLPMEvaluationMetrics {
	FITTING_WINDOWS ("Fitting Window Score", true), // 
	PASSAGE_COVERAGE ("Passage Coverage Score", true), //
	PASSAGE_REPETITION ("Passage Repetition Score", true), //
	TRANSITION_COVERAGE ("Transition Coverage Score", true), //
	TRACE_SUPPORT ("Trace Support Score", true), //
	TYPE_USAGE ("Type Usage Score", true), // Measures how well the object types are used in order to most clearly show the process to an observer.
	COMBINED_SCORE ("Combined Score"), // 
	NUM_TYPES("Number of Types"), // number of different place types in the model
	NUM_PLACES("Number of Places"),
	NUM_TRANSITIONS("Number of Transitions"),
	NUM_VARIABLEARCS("Number of Variable Arcs"),
	NUM_ARCS("Number of Arcs"),
	FRAC_NONVARIABLEARCS("Fraction Non-Variable Arcs"),
	DISCOVERY_TYPES("Case Notion Used"),
	;
	
	private final String name;
	private final boolean useInCombinedScore;
	
	OCLPMEvaluationMetrics(String name){
		this.name=name;
		this.useInCombinedScore=false;
	}
	
	OCLPMEvaluationMetrics(String name, boolean useInCombinedScore){
		this.name=name;
		this.useInCombinedScore=useInCombinedScore;
	}

	public String getName() {
		return this.name;
	}
	
	public boolean isUsedInCombinedScore() {
		return this.useInCombinedScore;
	}
}