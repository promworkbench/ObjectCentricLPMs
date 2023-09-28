package org.processmining.OCLPMDiscovery.parameters;

public enum ExternalObjectFlow {
	NONE ("No extra places"), // Leaves the models as they were discovered
	ALL ("Add all places"), // adds all places where the flow is interrupted
	START_END ("Add starting/ending places"), // adds places if flow is interrupted at starting or ending transitions
	ALL_VISIBLE ("All places but only present types."),
	START_END_VISIBLE ("Start/End places but only present types.")
	;
	
	private final String name;
	
	ExternalObjectFlow(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}