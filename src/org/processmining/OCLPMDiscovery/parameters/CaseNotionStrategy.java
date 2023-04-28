package org.processmining.OCLPMDiscovery.parameters;

public enum CaseNotionStrategy {
	DUMMY ("Single Dummy Case"),
	PE_LEADING ("Process Executions Leading Type"),
	PE_CONNECTED ("Process Executions Connected Components")
	;
	
	private final String name;
	
	CaseNotionStrategy(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}