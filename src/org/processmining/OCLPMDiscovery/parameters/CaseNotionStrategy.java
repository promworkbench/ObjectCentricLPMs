package org.processmining.OCLPMDiscovery.parameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum CaseNotionStrategy {
	DUMMY ("Single Dummy Case"),
	PE_LEADING ("Process Executions Leading Type"), // leading type strat from the paper
	PE_LEADING_RELAXED ("Process Executions Leading Type Relaxed"), // assign each to the closest object of leading type
	PE_CONNECTED ("Process Executions Connected Components")
	;
	
	private final String name;
	
	// case notions using specific object types selected by the user
	public static final Set<CaseNotionStrategy> typeSelectionNeeded = new HashSet<CaseNotionStrategy>(Arrays.asList(
			CaseNotionStrategy.PE_LEADING
			));
	
	// case notions needing the object graph to be constructed
	public static final Set<CaseNotionStrategy> objectGraphNeeded = new HashSet<CaseNotionStrategy>(Arrays.asList(
			PE_LEADING,
			PE_CONNECTED,
			PE_LEADING_RELAXED
			));
	
	CaseNotionStrategy(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}