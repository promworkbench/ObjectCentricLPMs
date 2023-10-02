package org.processmining.OCLPMDiscovery.parameters;

public enum PlaceCompletion {
	NONE ("None", "Leaves the models as they were discovered."),
	ALL ("All", "Adds all isometric places of other types to a model."),
	BETTERFLOW ("Better Flow", "Adds isometric places to the model if the type already occurs in the model."),
	FEWVARIABLE ("Fewer Variable", true, "Swaps existing places with the places that have the fewest variable arcs."),
	FEWVARIABLE_BETTERFLOW ("Fewer Variable and Better Flow", true , "")
	;
	
	private final String name;
	private final String help;
	private final boolean needsExactVariableArcs;
	
	PlaceCompletion(String name, String help){
		this.name=name;
		this.help=help;
		this.needsExactVariableArcs=false;
	}
	
	PlaceCompletion(String name, Boolean exactArcs, String help){
		this.name=name;
		this.help=help;
		this.needsExactVariableArcs=exactArcs;
	}

	public String getName() {
		return this.name;
	}
	public String getHelp() {
		return this.help;
	}

	public boolean needsExactVariableArcs() {
		return this.needsExactVariableArcs;
	}
	
	public static PlaceCompletion getValueFromName(String name) {
		for (PlaceCompletion f : PlaceCompletion.values()) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}
	
	public static String[] names() {
		String [] names = new String[PlaceCompletion.values().length];
		int i = 0;
		for (PlaceCompletion f : PlaceCompletion.values()) {
			names[i] = f.getName();
			i++;
		}
		return names;
	}
}