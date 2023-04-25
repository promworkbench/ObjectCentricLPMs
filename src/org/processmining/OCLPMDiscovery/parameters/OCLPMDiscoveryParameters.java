package org.processmining.OCLPMDiscovery.parameters;

import java.util.Set;

import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;

public class OCLPMDiscoveryParameters {
	private Set<String> objectTypesAll;
	private Set<String> objectTypesPlaceNets; // object types for which the log is flattened and place nets are discovered
	private Set<String> objectTypesLeadingTypes; // object types which are used as leading types for discovering process executions
	private Miner placeDiscoveryAlgorithm; 
	private SPECppParameters specppParameters;
	private XLogHybridILPMinerParametersImpl ilpParameters;
	
	public OCLPMDiscoveryParameters(Set<String> objectTypes) {
		this.objectTypesAll = objectTypes;
		this.setObjectTypesPlaceNets(objectTypes);
		this.setObjectTypesLeadingTypes(objectTypes);
		this.setPlaceDiscoveryAlgorithm(Miner.ILP);
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OCLPMDiscoveryParameters that = (OCLPMDiscoveryParameters) o;
        return true; // TODO
    }

    @Override
    public int hashCode() {
        return 1; //TODO
    }
    
	@Override
    public String toString() {
        return "Hi"; //TODO
    }

	public Set<String> getObjectTypesAll() {
		return objectTypesAll;
	}

	public Set<String> getObjectTypesPlaceNets() {
		return objectTypesPlaceNets;
	}

	public void setObjectTypesPlaceNets(Set<String> objectTypesPlaceNets) {
		this.objectTypesPlaceNets = objectTypesPlaceNets;
	}

	public Set<String> getObjectTypesLeadingTypes() {
		return objectTypesLeadingTypes;
	}

	public void setObjectTypesLeadingTypes(Set<String> objectTypesLeadingTypes) {
		this.objectTypesLeadingTypes = objectTypesLeadingTypes;
	}

	public SPECppParameters getSpecppParameters() {
		return specppParameters;
	}

	public void setSpecppParameters(SPECppParameters specppParameters) {
		this.specppParameters = specppParameters;
	}

	public Miner getPlaceDiscoveryAlgorithm() {
		return placeDiscoveryAlgorithm;
	}

	public void setPlaceDiscoveryAlgorithm(Miner placeDiscoveryAlgorithm) {
		this.placeDiscoveryAlgorithm = placeDiscoveryAlgorithm;
	}

	public XLogHybridILPMinerParametersImpl getIlpParameters() {
		return ilpParameters;
	}

	public void setIlpParameters(XLogHybridILPMinerParametersImpl ilpParameters) {
		this.ilpParameters = ilpParameters;
	}
}
