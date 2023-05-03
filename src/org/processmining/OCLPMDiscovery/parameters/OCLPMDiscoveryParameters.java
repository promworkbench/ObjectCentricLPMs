package org.processmining.OCLPMDiscovery.parameters;

import java.util.Set;

import javax.swing.DefaultListModel;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.plugins.mining.PlaceBasedLPMDiscoveryParameters;

public class OCLPMDiscoveryParameters {
	private OcelEventLog ocel;
	private Set<String> activities;
	private Set<String> objectTypesAll;
	private Set<String> objectTypesPlaceNets; // object types for which the log is flattened and place nets are discovered
	private Set<String> objectTypesLeadingTypes; // object types which are used as leading types for discovering process executions
	private SPECppParameters specppParameters;
	private XLogHybridILPMinerParametersImpl ilpParameters;
	private DefaultListModel<String> objectTypesList = new DefaultListModel<String>();
	private PlaceBasedLPMDiscoveryParameters PBLPMDiscoveryParameters;
	
	//TODO set default when the real strategies work
	private Miner placeDiscoveryAlgorithm 			= Miner.ILP;
	private CaseNotionStrategy caseNotionStrategy 	= CaseNotionStrategy.DUMMY;
	
	public OCLPMDiscoveryParameters(OcelEventLog ocel) {
		this.setOcel(ocel);
		Set<String> objectTypes = ocel.getObjectTypes();
		this.objectTypesAll = objectTypes;
		this.setObjectTypesPlaceNets(objectTypes);
		this.setObjectTypesLeadingTypes(objectTypes);
		this.setActivities(OCELUtils.getActivities(ocel));
		
		// list all object types
        for (String curType : this.objectTypesAll) {
        	objectTypesList.addElement(curType);
        }
        
        // setup LPM discovery
        String dummyType = "DummyType";
		OcelEventLog dummyOcel = OCELUtils.addDummyCaseNotion(ocel,"DummyType","42");
		XLog flatDummyLog = Flattening.flatten(dummyOcel,dummyType);
        this.PBLPMDiscoveryParameters = new PlaceBasedLPMDiscoveryParameters(flatDummyLog); // null-init leads to problems, needs a log with all activities
        
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

	public PlaceBasedLPMDiscoveryParameters getPBLPMDiscoveryParameters() {
		return PBLPMDiscoveryParameters;
	}

	public void setPBLPMDiscoveryParameters(PlaceBasedLPMDiscoveryParameters pBLPMDiscoveryParameters) {
		PBLPMDiscoveryParameters = pBLPMDiscoveryParameters;
	}

	public Set<String> getActivities() {
		return activities;
	}

	public void setActivities(Set<String> activities) {
		this.activities = activities;
	}

	public OcelEventLog getOcel() {
		return ocel;
	}

	public void setOcel(OcelEventLog ocel) {
		this.ocel = ocel;
	}
}
