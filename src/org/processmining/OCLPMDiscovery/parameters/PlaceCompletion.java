package org.processmining.OCLPMDiscovery.parameters;

import java.util.HashSet;

public enum PlaceCompletion {
	NONE ("None", "Leaves the models as they were discovered.", false),
	ALL ("All", "Adds all isometric places of other types to a model.", true),
	BETTERFLOW ("Better Flow", "Adds isometric places to the model if the type already occurs in the model.", false),
	FEWVARIABLE ("Fewer Variable", true, "Swaps existing places with the places that have the fewest variable arcs.", true),
	FEWVARIABLE_BETTERFLOW ("Fewer Variable and Better Flow", true , "", true)
	;
	
	private final String name;
	private final String help;
	private final boolean needsExactVariableArcs;
	private final boolean userAccessible;
	
	PlaceCompletion(String name, String help, Boolean userAccessible){
		this.name=name;
		this.help=help;
		this.needsExactVariableArcs=false;
		this.userAccessible = userAccessible;
	}
	
	PlaceCompletion(String name, Boolean exactArcs, String help, Boolean userAccessible){
		this.name=name;
		this.help=help;
		this.needsExactVariableArcs=exactArcs;
		this.userAccessible = userAccessible;
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
	
	public boolean isUserAccessible() {
		return this.userAccessible;
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
	
	public static String[] namesUserAccessible() {
		HashSet<String> nameSet = new HashSet<>();
		for (PlaceCompletion f : PlaceCompletion.values()) {
			if (f.isUserAccessible()) {
				nameSet.add(f.getName());
			}
		}
		String [] names = nameSet.toArray(new String[0]);
		return names;
	}
}