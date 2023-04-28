package org.processmining.OCLPMDiscovery.parameters;

import java.util.Set;

import javax.swing.DefaultListModel;

import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;

public class OCLPMDiscoveryParameters {
	private Set<String> objectTypesAll;
	private Set<String> objectTypesPlaceNets; // object types for which the log is flattened and place nets are discovered
	private Set<String> objectTypesLeadingTypes; // object types which are used as leading types for discovering process executions
	private SPECppParameters specppParameters;
	private XLogHybridILPMinerParametersImpl ilpParameters;
	private DefaultListModel<String> objectTypesList = new DefaultListModel<String>();
	
	//TODO set default when the real strategies work
	private Miner placeDiscoveryAlgorithm 			= Miner.ILP;
	private CaseNotionStrategy caseNotionStrategy 	= CaseNotionStrategy.DUMMY;
	
	public OCLPMDiscoveryParameters(Set<String> objectTypes) {
		this.objectTypesAll = objectTypes;
		this.setObjectTypesPlaceNets(objectTypes);
		this.setObjectTypesLeadingTypes(objectTypes);
		// list all object types
        for (String curType : this.objectTypesAll) {
        	objectTypesList.addElement(curType);
        }
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
        return "OCLPM Discovery parameters:\n"
        		+ "Place Discovery Algorithm: "+this.getPlaceDiscoveryAlgorithm().getName()+"\n"
        		+ "Case Notion Strategy: "+this.getCaseNotionStrategy(); 
        //TODO Add the other parameters
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

	public CaseNotionStrategy getCaseNotionStrategy() {
		return caseNotionStrategy;
	}

	public void setCaseNotionStrategy(CaseNotionStrategy caseNotionStrategy) {
		this.caseNotionStrategy = caseNotionStrategy;
	}
	
	public DefaultListModel<String> getObjectTypesList() {
		return this.objectTypesList;
	}
}
