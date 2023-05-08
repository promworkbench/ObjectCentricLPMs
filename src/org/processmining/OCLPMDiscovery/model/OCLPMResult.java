package org.processmining.OCLPMDiscovery.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableList;

public class OCLPMResult extends SerializableList<ObjectCentricLocalProcessModel> {
    private static final long serialVersionUID = 9159252267279978544L; //?
    
    private Set<String> objectTypes; // all object types from the ocel
    private Set<String> discoveryTypes; // types which were used as a case notion for LPM discovery
    private String oclpmDiscoverySettings; // settings used for the discovery of this result
    private HashMap<String,String> typeMap; // maps each place.id to an object type
    
    public OCLPMResult() {
    	
    }
    
    public OCLPMResult (OCLPMDiscoveryParameters discoveryParameters, LPMResult lpmResult, HashMap<String,String> typeMap) {
    	this(discoveryParameters, (Collection<ObjectCentricLocalProcessModel>) lpmResult.getSet(), typeMap);
    }
    
    public OCLPMResult (OCLPMDiscoveryParameters discoveryParameters, Collection<ObjectCentricLocalProcessModel> elements, HashMap<String,String> typeMap) {
    	super(elements);
    	this.typeMap = typeMap;
    	copyDiscoveryParameters(discoveryParameters);
    }
    
    public void copyDiscoveryParameters(OCLPMDiscoveryParameters discoveryParameters) {
    	this.oclpmDiscoverySettings = discoveryParameters.toString();
    	this.objectTypes = discoveryParameters.getObjectTypesAll();
    	this.discoveryTypes = discoveryParameters.getObjectTypesLeadingTypes();
    }

	public Set<String> getObjectTypes() {
		return objectTypes;
	}

	public Set<String> getDiscoveryTypes() {
		return discoveryTypes;
	}

	public String getOclpmDiscoverySettings() {
		return oclpmDiscoverySettings;
	}

	public HashMap<String,String> getTypeMap() {
		return typeMap;
	}

}
