package org.processmining.OCLPMDiscovery.parameters;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.swing.DefaultListModel;

import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.logs.EventLog;
import org.processmining.placebasedlpmdiscovery.prom.plugins.mining.PlaceBasedLPMDiscoveryParameters;

public class OCLPMDiscoveryParameters implements EventLog{
	private boolean computeExtraStats = false; // compute e.g., events per case median
	
	private OcelEventLog ocel;
	private Set<String> activities;
	private Set<String> objectTypesAll; // object types before enriching ocel with new case notion
	private DefaultListModel<String> objectTypesList = new DefaultListModel<String>();
	private Set<String> objectTypesPlaceNets; // object types for which the log is flattened and place nets are discovered
	private Set<String> objectTypesLeadingTypes; // object types which are used as leading types for discovering process executions
	private Set<String> objectTypesCaseNotion; // object types considered when doing case notion discovery
	
	// place discovery
	private SPECppParameters specppParameters;
	private XLogHybridILPMinerParametersImpl ilpParameters;
	private PlaceBasedLPMDiscoveryParameters PBLPMDiscoveryParameters;
	private int modelLimit = Integer.MAX_VALUE;
	private boolean placeSetPostProcessing = true; // remove duplicate places from placeSet (should always be true)
	
	// variable arc identification
	private VariableArcIdentification variableArcIdentification = VariableArcIdentification.PER_LPM; // IMO the most accurate should always be chosen, so use PER_LPM and do not make it user adjustable
	private float variableArcThreshold = 0.95f; // threshold which the score function is compared against
	
	// place completion
	private PlaceCompletion placeCompletion = PlaceCompletion.NONE; // adds isomorphic places to each model
	// place completion can be done afterwards, in the visualizer
	
	// set defaults
	private Miner placeDiscoveryAlgorithm 			= Miner.SPECPP;
	private CaseNotionStrategy caseNotionStrategy 	= CaseNotionStrategy.PE_CONNECTED;
	
	public OCLPMDiscoveryParameters(OcelEventLog ocel) {
		this.setOcel(ocel);
		Set<String> objectTypes = new HashSet<>(); 
		// add non-empty object types
		for (String ot : ocel.objectTypes.keySet()) {
			if (ocel.objectTypes.get(ot) != null) {
				objectTypes.add(ot);
			}
		}
		this.objectTypesAll = objectTypes;
		this.setObjectTypesPlaceNets(objectTypes);
		this.setObjectTypesLeadingTypes(objectTypes);
		this.setActivities(OCELUtils.getActivities(ocel));
		this.setObjectTypesCaseNotion(objectTypes);
		
//		this.removeEmptyObjectTypes();
		
		// list all object types
        for (String curType : this.objectTypesAll) {
        	objectTypesList.addElement(curType);
        }
        
        // setup LPM discovery
        this.PBLPMDiscoveryParameters = new PlaceBasedLPMDiscoveryParameters(this); // null-init leads to problems, needs a log with all activities  
        this.PBLPMDiscoveryParameters.setLpmCount(Integer.MAX_VALUE);
        this.PBLPMDiscoveryParameters.getPlaceChooserParameters().setPlaceLimit(1000);
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OCLPMDiscoveryParameters that = (OCLPMDiscoveryParameters) o;
		return this.specppParameters.equals(that.getSpecppParameters())
			&& this.ilpParameters.equals(that.getIlpParameters())
			&& this.PBLPMDiscoveryParameters.equals(that.getPBLPMDiscoveryParameters())
			&& this.placeDiscoveryAlgorithm == that.getPlaceDiscoveryAlgorithm()
			&& this.caseNotionStrategy == that.getCaseNotionStrategy()
			&& this.objectTypesPlaceNets.equals(that.getObjectTypesPlaceNets())
			&& this.objectTypesLeadingTypes.equals(that.getObjectTypesLeadingTypes())
			;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        		this.specppParameters,
        		this.ilpParameters,
        		this.PBLPMDiscoveryParameters,
        		this.placeDiscoveryAlgorithm,
        		this.caseNotionStrategy,
        		this.objectTypesPlaceNets,
        		this.objectTypesLeadingTypes
        		);
    }
    
	@Override
    public String toString() {
        String caption = "OCLPM Discovery parameters:\n";
        String broadParameters = 		
        		"Place Discovery Algorithm: "+this.getPlaceDiscoveryAlgorithm().getName()+"\n"
        		+ "Case Notion Strategy: "+this.getCaseNotionStrategy().getName()+"\n"
        		;
        String otherParams = "";
        int s,i;
        
        otherParams += "Place Discovery Object Types: ";
        s = this.getObjectTypesPlaceNets().size();
        i = 0;
        for (String current : this.getObjectTypesPlaceNets()) {
        	otherParams += current;
        	i++;
        	if (i<s) otherParams+=", ";
        	else otherParams+="\n";
        }
        
        otherParams += "LPM Case Notion Object Types: ";
        s = this.getObjectTypesCaseNotion().size();
        i = 0;
        for (String current : this.getObjectTypesCaseNotion()) {
        	otherParams += current;
        	i++;
        	if (i<s) otherParams+=", ";
        	else otherParams+="\n";
        }
        
        if (CaseNotionStrategy.typeSelectionNeeded.contains(this.getCaseNotionStrategy())) {
        	otherParams += "LPM Case Notion Leading Types: ";
            s = this.getObjectTypesLeadingTypes().size();
            i = 0;
            for (String current : this.getObjectTypesLeadingTypes()) {
            	otherParams += current;
            	i++;
            	if (i<s) otherParams+=", ";
            	else otherParams+="\n";
            }
        }
        
        if (this.getPlaceDiscoveryAlgorithm() == Miner.SPECPP) {
        	if (this.getSpecppParameters() == null) {
        		otherParams += "SPECpp parameters haven't been saved.\n";
        	}
        	else {
        		otherParams += "SPECpp parameters:\n";
        		otherParams += this.getSpecppParameters().toString();        		
        	}
        }
        
        else if (this.getPlaceDiscoveryAlgorithm() == Miner.ILP) {
        	if (this.getPlaceDiscoveryAlgorithm() == null) {
        		otherParams += "ILP parameters haven't been saved.\n";
        	}
        	else {
        		otherParams += "ILP parameters:\n";
        		otherParams += "Wizard follows now.\n";
//        		otherParams += this.getIlpParameters().toString();        		
        	}
        }
        
        otherParams += "Variable arc identification: "+this.getVariableArcIdentification().getName()+"\n";
        otherParams += "Variable arc threshold: "+this.getVariableArcThreshold()+"\n";
//        otherParams += "Place completion: "+this.getPlaceCompletion().getName()+"\n";
        
        return caption + broadParameters + otherParams;
    }
	
    public String toHTML() {
    	String htmlStart = "<html><body>";
        String htmlEnd = "</body></html>";
        return htmlStart + this.toHTMLBody() + htmlEnd;
    }
    
    public String toHTMLBody() {
    	String caption = "<b>OCLPM Discovery parameters:</b><br>";
        String otherParams = "";
        int s,i;
        
        otherParams += "Object Types in place set: ";
        s = this.getObjectTypesPlaceNets().size();
        i = 0;
        for (String current : this.getObjectTypesPlaceNets()) {
        	otherParams += current;
        	i++;
        	if (i<s) otherParams+=", ";
        	else otherParams+="<br>";
        }
        
        otherParams += "Case Notion Strategy: "+this.getCaseNotionStrategy().getName()+"<br>";
        otherParams += "LPM Case Notion Object Types: ";
        s = this.getObjectTypesCaseNotion().size();
        i = 0;
        for (String current : this.getObjectTypesCaseNotion()) {
        	otherParams += current;
        	i++;
        	if (i<s) otherParams+=", ";
        	else otherParams+="<br>";
        }
        
        if (CaseNotionStrategy.typeSelectionNeeded.contains(this.getCaseNotionStrategy())) {
        	otherParams += "LPM Case Notion Leading Types: ";
            s = this.getObjectTypesLeadingTypes().size();
            i = 0;
            for (String current : this.getObjectTypesLeadingTypes()) {
            	otherParams += current;
            	i++;
            	if (i<s) otherParams+=", ";
            	else otherParams+="<br>";
            }
        }
        
//        otherParams += "Place Discovery Algorithm: "+this.getPlaceDiscoveryAlgorithm().getName()+"<br>";
//        if (this.getPlaceDiscoveryAlgorithm() == Miner.SPECPP) {
//        	if (this.getSpecppParameters() == null) {
//        		otherParams += "SPECpp parameters haven't been saved.<br>";
//        	}
//        	else {
//        		otherParams += "SPECpp parameters:<br>";
//        		otherParams += this.getSpecppParameters().toString();        		
//        	}
//        }
        
//        else if (this.getPlaceDiscoveryAlgorithm() == Miner.ILP) {
//        	otherParams += "ILP parameters haven't been saved.<br>";
////        	if (this.getPlaceDiscoveryAlgorithm() == null) {
////        		otherParams += "ILP parameters haven't been saved.<br>";
////        	}
////        	else {
////        		otherParams += "ILP parameters:<br>";
////        		otherParams += "Wizard follows now.<br>";
//////        		otherParams += this.getIlpParameters().toString();        		
////        	}
//        }
        
        otherParams += "Variable arc identification: "+this.getVariableArcIdentification().getName()+"<br>";
        otherParams += "Variable arc threshold: "+this.getVariableArcThreshold()+"<br>";
//        otherParams += "Place completion: "+this.getPlaceCompletion().getName()+"<br>";
        
        return caption + otherParams;
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
	
	public void setLeadingType(String type) {
		this.objectTypesLeadingTypes = Collections.singleton(type);
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

	public float getVariableArcThreshold() {
		return variableArcThreshold;
	}

	public void setVariableArcThreshold(float variableArcThreshold) {
		this.variableArcThreshold = variableArcThreshold;
	}

	public Set<String> getObjectTypesCaseNotion() {
		return objectTypesCaseNotion;
	}

	public void setObjectTypesCaseNotion(Set<String> objectTypesCaseNotion) {
		this.objectTypesCaseNotion = objectTypesCaseNotion;
	}

	public PlaceCompletion getPlaceCompletion() {
		return placeCompletion;
	}

	public void setPlaceCompletion(PlaceCompletion placeCompletion) {
		this.placeCompletion = placeCompletion;
	}

	public VariableArcIdentification getVariableArcIdentification() {
		return variableArcIdentification;
	}

	public void setVariableArcIdentification(VariableArcIdentification variableArcIdentification) {
		this.variableArcIdentification = variableArcIdentification;
	}
 

	public boolean doPlaceSetPostProcessing() {
		return this.placeSetPostProcessing;
	}
	
	public void setPlaceSetPostProcessing( boolean b) {
		this.placeSetPostProcessing = b;
	}

	public int getModelLimit() {
		return modelLimit;
	}

	public void setModelLimit(int modelLimit) {
		this.modelLimit = modelLimit;
	}

	// removes all empty object types from the sets so they will be ignored
	public void removeEmptyObjectTypes() {
		Set<String> removeTypes = new HashSet<>();
		for (String type : ocel.getObjectTypes()) {
			if (ocel.objectTypes.get(type) == null) {
				removeTypes.add(type);
			}
		}
		this.objectTypesAll.removeAll(removeTypes);
		this.objectTypesCaseNotion.removeAll(removeTypes);
		this.objectTypesLeadingTypes.removeAll(removeTypes);
		for (String t : removeTypes) {
			this.objectTypesList.removeElement(t);
		}
		this.objectTypesPlaceNets.removeAll(removeTypes);
	}

	public boolean isComputeExtraStats() {
		return computeExtraStats;
	}

	public void setComputeExtraStats(boolean computeExtraStats) {
		this.computeExtraStats = computeExtraStats;
	}
}
