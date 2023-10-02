package org.processmining.OCLPMDiscovery.parameters;

public enum ExternalObjectFlow {
	NONE ("None","No extra places"), // Leaves the models as they were discovered
	ALL ("All","Add all places"), // adds all places where the flow is interrupted
	START_END ("Start and End","Add starting/ending places"), // adds places if flow is interrupted at starting or ending transitions
	ALL_VISIBLE ("All Visible","All places but only present types."),
	START_END_VISIBLE ("Start and End Visible","Start/End places but only present types.")
	;
	
	private final String name;
	private final String help;
	
	ExternalObjectFlow(String name, String help){
		this.name=name;
		this.help = help;
	}

	public String getName() {
		return this.name;
	}
	
	public String getHelp() {
		return this.help;
	}
	
	public static ExternalObjectFlow getValueFromName(String name) {
		for (ExternalObjectFlow f : ExternalObjectFlow.values()) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}
	
	public static String[] names() {
		String [] names = new String[ExternalObjectFlow.values().length];
		int i = 0;
		for (ExternalObjectFlow f : ExternalObjectFlow.values()) {
			names[i] = f.getName();
			i++;
		}
		return names;
	}
}