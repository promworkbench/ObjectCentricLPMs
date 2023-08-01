package org.processmining.OCLPMDiscovery.model;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableList;

public class OCLPMResult extends SerializableList<ObjectCentricLocalProcessModel> {
    private static final long serialVersionUID = 9159252267279978544L; //?
    
    private Set<String> objectTypes; // all object types from the ocel
    private Set<String> lpmDiscoveryTypes; // types which were used as a case notion for LPM discovery
    private String oclpmDiscoverySettings; // settings used for the discovery of this result
    private HashMap<String,String> typeMap; // maps each place.id to an object type
    private HashMap<String,Color> mapIdColor; // maps each place.id to a color.
    private HashMap<String,Color> mapTypeColor; // maps each object type to a color
    private HashSet<List<String>> variableArcSet = new HashSet<>(); // saves all variable arcs [Activity,ObjectType]
    private HashMap<String,HashSet<String>> variableArcActivities = new HashMap<>(); // saves for each place id the activities with which the place forms variable arcs
    private TaggedPlaceSet placeSet; 
    private boolean showExternalObjectFlow = false;
    
    public OCLPMResult() {
    	
    }
    
    public OCLPMResult (OCLPMDiscoveryParameters discoveryParameters, LPMResultsTagged tlpms) {
    	super();
    	
    	HashSet<ObjectCentricLocalProcessModel> oclpms = new HashSet<ObjectCentricLocalProcessModel>(tlpms.size());
    	// convert LPM objects into OCLPM objects
    	for (TaggedLPMResult res : tlpms.getElements()) { // for all used case notions
	    	for (LocalProcessModel lpm : res.getElements()) { // for all lpms discovered for that notion
	    		ObjectCentricLocalProcessModel oclpm = new ObjectCentricLocalProcessModel(lpm, res.getCaseNotion());
	    		oclpms.add(oclpm);
	    	}
    	}
    	this.addAll(oclpms);
    	copyDiscoveryParameters(discoveryParameters);
    	this.refreshColors();
    }
    
    public OCLPMResult (OCLPMDiscoveryParameters discoveryParameters, LPMResultsTagged tlpms, TaggedPlaceSet placeSet) {
    	this(discoveryParameters, tlpms);
    	this.setPlaceSet(placeSet);
    }
    
    /**
     * Sets the places as OCLPMs. (for place set visualization)
     * @param placeSet
     */
    public OCLPMResult (PlaceSet placeSet) {
    	super();
    	this.placeSet = new TaggedPlaceSet(placeSet);
    	this.objectTypes = new HashSet<String>();
    	this.typeMap = new HashMap<String,String>();
    	for (Place p : placeSet.getElements()) {
    		TaggedPlace tp = (TaggedPlace)p;
    		this.objectTypes.add(tp.getObjectType());
    		this.typeMap.put(tp.getId(), tp.getObjectType());
    		ObjectCentricLocalProcessModel oclpm = new ObjectCentricLocalProcessModel(tp);
    		this.add(oclpm);
    	}
    	this.storeVariableArcs();
    	this.refreshColors();
    }
    
    /**
     * Sets the places as OCLPMs. (for place set visualization)
     * @param placeSet
     */
    public OCLPMResult (TaggedPlaceSet placeSet) {
    	super();
    	this.placeSet = placeSet;
    	this.objectTypes = new HashSet<String>();
    	this.typeMap = new HashMap<String,String>();
    	for (TaggedPlace tp : placeSet.getElements()) {
    		this.objectTypes.add(tp.getObjectType());
    		this.typeMap.put(tp.getId(), tp.getObjectType());
    		ObjectCentricLocalProcessModel oclpm = new ObjectCentricLocalProcessModel(tp);
    		this.add(oclpm);
    	}
    	this.storeVariableArcs();
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
	
	public void setObjectTypes(Set<String> types) {
		this.objectTypes = types;
	}

	public Set<String> getLpmDiscoveryTypes() {
		return lpmDiscoveryTypes;
	}
	
	public void setLpmDiscoveryTypes(Set<String> types) {
		this.lpmDiscoveryTypes = types;
	}

	public String getOclpmDiscoverySettings() {
		return oclpmDiscoverySettings;
	}
	
	public void setOclpmDiscoverySettings(String settings) {
		this.oclpmDiscoverySettings = settings;
	}
	
	public void refreshColors() {
		// generate colors
		int numTypes = this.getObjectTypes().size();
		float saturation = 1.0f;
		float luminance = 0.8f;
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
		for (ObjectCentricLocalProcessModel oclpm : this.elements) {
			for (TaggedPlace tp : oclpm.getPlaces()) {
				mapIdColor.put(tp.getId(), mapTypeColor.get(tp.getObjectType()));
			}
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

	public HashSet<List<String>> getVariableArcSet() {
		return variableArcSet;
	}

	public void setVariableArcSet(HashSet<List<String>> variableArcSet) {
		this.variableArcSet = variableArcSet;
	}

	public HashMap<String,String> getTypeMap() {
		return typeMap;
	}

	public void setTypeMap(HashMap<String,String> typeMap) {
		this.typeMap = typeMap;
	}

	public HashMap<String,HashSet<String>> getVariableArcActivities() {
		return variableArcActivities;
	}

	public void setVariableArcActivities(HashMap<String,HashSet<String>> variableArcActivities) {
		this.variableArcActivities = variableArcActivities;
	}

	/**
	 * Fetches the variable arc activities from the tagged places and stores them in a map
	 * mapping from the place id to the activities which have variable arcs for the corresponding
	 * object type.
	 * Used because it isn't possible for the visualizer to access the tagged places.
	 */
	public void storeVariableArcs() {
		for (ObjectCentricLocalProcessModel oclpm : this.elements) {
			for (TaggedPlace tp : oclpm.getPlaces()) {
				if (!(tp.getVariableArcActivities().isEmpty()) && !(tp.getVariableArcActivities() == null)) {
					this.variableArcActivities.put(tp.getId(), tp.getVariableArcActivities());
				}
				else {
					this.variableArcActivities.put(tp.getId(), new HashSet<String>(1));
				}
			}
		}
	}

	/**
	 * Stores for each place id the object type of that place.
	 * Used in the visualizer because after the conversion to Petri nets for the visualization
	 * the tagged places aren't accessible anymore.
	 */
	public void createTypeMap() {
		this.typeMap = new HashMap<String,String>();
		for (ObjectCentricLocalProcessModel oclpm : this.elements) {
			for (TaggedPlace tp : oclpm.getPlaces()) {
				this.typeMap.put(tp.getId(), tp.getObjectType());
			}
		}
	}
	
	/**
	 * Deletes duplicate OCLPMs, ignoring variable arcs
	 */
	public void deleteDuplicates () {
		// Identify equal OCLPMs (ignoring variable arcs)
		HashSet<Integer> deletionSet = new HashSet<Integer>(this.getElements().size());
		for (int i1 = 0; i1<this.getElements().size()-1; i1++) {
			if (deletionSet.contains(i1)) {
				continue; // model is already tagged to be deleted
			}
			for (int i2 = i1+1; i2<this.getElements().size(); i2++) {
				if (deletionSet.contains(i2)) {
					continue; // model is already tagged to be deleted
				}
				ObjectCentricLocalProcessModel oclpm1 = this.getElement(i1);
				ObjectCentricLocalProcessModel oclpm2 = this.getElement(i2);
				if (oclpm1.isEqual(oclpm2)) {
					deletionSet.add(i2);
				}
			}
		}
		// delete equal OCLPMs
		HashSet<ObjectCentricLocalProcessModel> deleteModels = new HashSet<ObjectCentricLocalProcessModel>(deletionSet.size());
		for (int i : deletionSet) {
			deleteModels.add(this.getElement(i));
		}
		for (ObjectCentricLocalProcessModel o : deleteModels) {
			this.remove(o);			
		}
	}
	
	/**
	 * Deletes duplicate OCLPMs, ignoring variable arcs and object types
	 */
	public void deleteIsomorphic () {
		// Identify isomorphic OCLPMs (ignoring variable arcs and object types)
		HashSet<Integer> deletionSet = new HashSet<Integer>(this.getElements().size());
		for (int i1 = 0; i1<this.getElements().size()-1; i1++) {
			if (deletionSet.contains(i1)) {
				continue; // model is already tagged to be deleted
			}
			for (int i2 = i1+1; i2<this.getElements().size(); i2++) {
				if (deletionSet.contains(i2)) {
					continue; // model is already tagged to be deleted
				}
				ObjectCentricLocalProcessModel oclpm1 = this.getElement(i1);
				ObjectCentricLocalProcessModel oclpm2 = this.getElement(i2);
				if (oclpm1.isIsomorphic(oclpm2)) {
					deletionSet.add(i2);
				}
			}
		}
		// delete isomorphic OCLPMs
		HashSet<ObjectCentricLocalProcessModel> deleteModels = new HashSet<ObjectCentricLocalProcessModel>(deletionSet.size());
		for (int i : deletionSet) {
			deleteModels.add(this.getElement(i));
		}
		for (ObjectCentricLocalProcessModel o : deleteModels) {
			this.remove(o);			
		}
	}

	public TaggedPlaceSet getPlaceSet() {
		return placeSet;
	}

	public void setPlaceSet(TaggedPlaceSet placeSet) {
		this.placeSet = placeSet;
	}
	
	public void setPlaceSet(PlaceSet placeSet) {
		this.placeSet = new TaggedPlaceSet(placeSet);
	}
	
	/**
	 * Returns a new object such that place completion can be performed without altering the original OCLPMResult.
	 * @return
	 */
	public OCLPMResult copyForPlaceCompletion () {
		// shared attributes
		OCLPMResult newResult = new OCLPMResult();
		newResult.setPlaceSet(this.getPlaceSet());
    	newResult.setObjectTypes(this.getObjectTypes());
    	newResult.setTypeMap(this.getTypeMap());
		newResult.setOclpmDiscoverySettings(this.getOclpmDiscoverySettings()); // make place completion changeable? removed it from the settings print
    	newResult.setLpmDiscoveryTypes(this.getLpmDiscoveryTypes());
    	
    	// independent attributes
    	newResult.setVariableArcSet(new HashSet<>());
    	newResult.setVariableArcActivities(new HashMap<>());
    	
		// places themselves will not be altered, only which places the OCLPMs use
		for (ObjectCentricLocalProcessModel oclpm : this.elements) {
			ObjectCentricLocalProcessModel newOclpm = new ObjectCentricLocalProcessModel(oclpm);
			newResult.add(newOclpm);
		}
		
		newResult.storeVariableArcs();
    	newResult.refreshColors();
		return newResult;
	}

	public void showExternalObjectFlow(boolean selected) {
		if (this.showExternalObjectFlow == selected) {
			return; // already in correct state
		}
		else if (selected) {
			// add external flow places
			for (ObjectCentricLocalProcessModel oclpm : this.getElements()) {
				oclpm.addExternalObjectFlow(this.getStartingActivities(), this.getEndingActivities());
			}
		}
		else {
			// remove external flow places
			for (ObjectCentricLocalProcessModel oclpm : this.getElements()) {
				oclpm.removeExternalObjectFlow(this.getStartingActivities(), this.getEndingActivities());
			}
		}
		this.storeVariableArcs(); // variable arcs for starting and ending places might've been added
		
		// add new place labels to the type map
		for (String type : this.objectTypes) {
			this.typeMap.put("StartingPlace:"+type,type);
			this.typeMap.put("EndingPlace:"+type,type);
		}
	}

	public Map<String, Set<String>> getStartingActivities() {
		return this.placeSet.getStartingActivities();
	}

	public void setStartingActivities(Map<String, Set<String>> startingActivities) {
		this.placeSet.setStartingActivities(startingActivities);
	}

	public Map<String, Set<String>> getEndingActivities() {
		return this.placeSet.getEndingActivities();
	}

	public void setEndingActivities(Map<String, Set<String>> endingActivities) {
		this.placeSet.setEndingActivities(endingActivities);
	}

}
