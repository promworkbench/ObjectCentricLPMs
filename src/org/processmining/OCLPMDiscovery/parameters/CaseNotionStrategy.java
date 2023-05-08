package org.processmining.OCLPMDiscovery.parameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum CaseNotionStrategy {
	DUMMY ("Single Dummy Case"),
	PE_LEADING ("Process Executions Leading Type"),
	PE_CONNECTED ("Process Executions Connected Components")
	// TODO implement my version of the leading type strategy
	;
	
	private final String name;
	
	// case notions using specific object types selected by the user
	public static final Set<CaseNotionStrategy> typeSelectionNeeded = new HashSet<CaseNotionStrategy>(Arrays.asList(
			CaseNotionStrategy.PE_LEADING
			));
	
	CaseNotionStrategy(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}