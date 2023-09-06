package org.processmining.OCLPMDiscovery.parameters;

public enum ExternalObjectFlow {
	NONE ("No extra places"), // Leaves the models as they were discovered
	ALL ("Make well-formed"), // adds all places where the flow is interrupted to make it well-formed according to def. 5.2. in Discovering OCPNs
	START_END ("Add starting/ending places") // adds places if flow is interrupted at starting or ending transitions
	;
	
	private final String name;
	
	ExternalObjectFlow(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}