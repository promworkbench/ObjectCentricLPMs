package org.processmining.OCLPMDiscovery.parameters;

public enum Miner {
	SPECPP ("SPECpp"),
	EST ("eST-Miner"),
	ILP ("Hybrid ILP Miner")// "ILP-Based Process Discovery" base on the hybrid ILP miner
	;
	
	private final String name;
	
	Miner(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
}