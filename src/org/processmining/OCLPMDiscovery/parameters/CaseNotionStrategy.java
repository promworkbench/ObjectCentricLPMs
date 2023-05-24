package org.processmining.OCLPMDiscovery.parameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum CaseNotionStrategy {
	DUMMY ("Single Dummy Case"),
	PE_LEADING ("Process Executions Leading Type"), // leading type strat from the paper
	PE_LEADING_RELAXED ("Process Executions Leading Type Relaxed"), // assign each to the closest object of leading type
	PE_CONNECTED ("Process Executions Connected Components"),
	PE_LEADING_O1 ("Leading Type Optimization 1"), // leading type strat but for events with leading type object only assign to those PEs
	PE_LEADING_RELAXED_O1 ("Leading Type Relaxed Optimization 1") // leading type relaxed strat but for events with leading type object only assign to those PEs
	;
	
	private final String name;
	
	// case notions using specific object types selected by the user
	public static final Set<CaseNotionStrategy> typeSelectionNeeded = new HashSet<CaseNotionStrategy>(Arrays.asList(
			CaseNotionStrategy.PE_LEADING,
			PE_LEADING_RELAXED,
			PE_LEADING_O1,
			PE_LEADING_RELAXED_O1
			));
	
	// case notions needing the object graph to be constructed
	public static final Set<CaseNotionStrategy> objectGraphNeeded = new HashSet<CaseNotionStrategy>(Arrays.asList(
			PE_LEADING,
			PE_CONNECTED,
			PE_LEADING_RELAXED,
			PE_LEADING_O1,
			PE_LEADING_RELAXED_O1
			));
	
	CaseNotionStrategy(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}