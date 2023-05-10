package org.processmining.OCLPMDiscovery.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableList;

public class OCLPMResult extends SerializableList<ObjectCentricLocalProcessModel> {
    private static final long serialVersionUID = 9159252267279978544L; //?
    
    private Set<String> objectTypes; // all object types from the ocel
    private Set<String> lpmDiscoveryTypes; // types which were used as a case notion for LPM discovery
    private String oclpmDiscoverySettings; // settings used for the discovery of this result
    private HashMap<String,String> typeMap; // maps each place.id to an object type
    
    public OCLPMResult() {
    	
    }
    
    public OCLPMResult (OCLPMDiscoveryParameters discoveryParameters, LPMResultsTagged tlpms, HashMap<String,String> typeMap) {
    	super();
    	HashSet<ObjectCentricLocalProcessModel> oclpms = new HashSet<ObjectCentricLocalProcessModel>(tlpms.totalLPMs());
    	// convert LPM objects into OCLPM objects
    	for (LPMResult res : tlpms.getTypeMap().keySet()) { // for all used case notions
	    	for (LocalProcessModel lpm : res.getElements()) { // for all lpms discovered for that notion
	    		ObjectCentricLocalProcessModel oclpm = new ObjectCentricLocalProcessModel(lpm, tlpms.getTypeOf(res));
	    		oclpms.add(oclpm);
	    	}
    	}
    	this.addAll(oclpms);
    	this.typeMap = typeMap;
    	copyDiscoveryParameters(discoveryParameters);
    }
    
    public void copyDiscoveryParameters(OCLPMDiscoveryParameters discoveryParameters) {
    	this.oclpmDiscoverySettings = discoveryParameters.toString();
    	this.objectTypes = discoveryParameters.getObjectTypesAll();
    	this.lpmDiscoveryTypes = discoveryParameters.getObjectTypesLeadingTypes();
    }

	public Set<String> getObjectTypes() {
		return objectTypes;
	}

	public Set<String> getLpmDiscoveryTypes() {
		return lpmDiscoveryTypes;
	}

	public String getOclpmDiscoverySettings() {
		return oclpmDiscoverySettings;
	}

	public HashMap<String,String> getTypeMap() {
		return typeMap;
	}

}
