package org.processmining.OCLPMDiscovery.model;

import java.awt.Color;
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
    private HashMap<String,Color> mapIdColor; // maps each place.id to a color
    private HashMap<String,Color> mapTypeColor; // maps each object type to a color
    
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
    	this.refreshColors();
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
	
	public void refreshColors() {
		// generate colors
		int numTypes = this.getObjectTypes().size();
		float saturation = 1.0f;
		float luminance = 0.3f;
		float hueStart = 0f;
		float hueEnd = 255f;
		float hue = hueStart;
		Color[] colors = new Color[numTypes];
		for (int i=0; i<numTypes; i++) {
			hue = hueStart + ((hueEnd-hueStart)/numTypes)*i; // hue in [0,255] but java uses a value in [0,1] to get the 360 degree angle for the hue circle
			hue = hue/255f; 
			colors[i] = Color.getHSBColor(hue, saturation, luminance);
		}
		
		// assign object types to colors
		HashMap <String,Color> mapTypeColor = new HashMap<String,Color>();
		int counter = 0;
		for (String type : this.getObjectTypes()) {
			mapTypeColor.put(type, colors[counter]);
			counter++;
		}
		this.setMapTypeColor(mapTypeColor);
		
		// assign place ids to colors
		HashMap<String,Color> mapIdColor = new HashMap<String,Color>();
		HashMap<String,String> mapIdType = this.getTypeMap();
		Color color;
		for (String id : this.getTypeMap().keySet()) {
			color = mapTypeColor.get(mapIdType.get(id)); 
			mapIdColor.put(id, color);
		}
		this.setMapIdColor(mapIdColor);
	}

	public HashMap<String, Color> getMapIdColor() {
		return mapIdColor;
	}

	public void setMapIdColor(HashMap<String, Color> mapIdColor) {
		this.mapIdColor = mapIdColor;
	}

	public HashMap<String, Color> getMapTypeColor() {
		return mapTypeColor;
	}

	public void setMapTypeColor(HashMap<String, Color> mapTypeColor) {
		this.mapTypeColor = mapTypeColor;
	}

}
