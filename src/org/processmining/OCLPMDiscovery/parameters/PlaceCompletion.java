package org.processmining.OCLPMDiscovery.parameters;

public enum PlaceCompletion {
	NONE ("No Place Completion"), // Leaves the models as they were discovered
	ALL ("Add all places"), // adds all isometric places of other types to a model
	BETTERFLOW ("Extend object type flow"), // adds isometric places to the model if the type already occurs in the model
	FEWVARIABLE ("Fewer variable arcs", true), // swaps existing places with the places that have the fewest variable arcs
	FEWVARIABLE_BETTERFLOW ("Fewer variable arcs and extended flow", true)
	;
	
	private final String name;
	private final boolean needsExactVariableArcs;
	
	PlaceCompletion(String name){
		this.name=name;
		this.needsExactVariableArcs=false;
	}
	
	PlaceCompletion(String name, Boolean exactArcs){
		this.name=name;
		this.needsExactVariableArcs=exactArcs;
	}

	public String getName() {
		return this.name;
	}

	public boolean needsExactVariableArcs() {
		return this.needsExactVariableArcs;
	}
}