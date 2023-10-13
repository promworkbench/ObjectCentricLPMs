package org.processmining.OCLPMDiscovery.model;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.parameters.ExternalObjectFlow;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.parameters.PlaceCompletion;
import org.processmining.OCLPMDiscovery.parameters.VariableArcIdentification;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableList;
import org.processmining.placebasedlpmdiscovery.prom.plugins.mining.PlaceBasedLPMDiscoveryParameters;

public class OCLPMResult extends SerializableList<ObjectCentricLocalProcessModel> {
    private static final long serialVersionUID = 9159252267279978544L; //?
    
    private Set<String> objectTypes = new HashSet<>(); // all object types from the ocel
    private Set<String> lpmDiscoveryTypes; // types which were used as a case notion for LPM discovery
    private String oclpmDiscoverySettings; // settings used for the discovery of this result
    private String oclpmDiscoverySettingsHTML; // settings used for the discovery of this result, no html start & end
    private HashMap<String,Color> mapTypeColor; // maps each object type to a color
    private TaggedPlaceSet placeSet; 
    private PlaceBasedLPMDiscoveryParameters PBLPMDiscoveryParameters;
    private ExternalObjectFlow showExternalObjectFlow = ExternalObjectFlow.NONE;
    private long executionTime = -1; // execution time starting after the place discovery in milliseconds, excluding place completion (which is done in the visualizer)
    private long executionTimePlaceCompletion = 0; // in milliseconds
    private long executionTimeExternalObjectFlow = 0; // in milliseconds
    private String timeStartingFrom = "";
    
    public OCLPMResult() {
    	
    }
    
    public OCLPMResult (OCLPMDiscoveryParameters discoveryParameters, LPMResultsTagged tlpms) {
    	super();
    	
    	HashSet<ObjectCentricLocalProcessModel> oclpms = new HashSet<ObjectCentricLocalProcessModel>(tlpms.size());
    	// convert LPM objects into OCLPM objects
    	for (TaggedLPMResult res : tlpms.getElements()) { // for all used case notions
	    	for (LocalProcessModel lpm : res.getElements()) { // for all lpms discovered for that notion
	    		ObjectCentricLocalProcessModel oclpm = new ObjectCentricLocalProcessModel(lpm, res.getCaseNotion(), discoveryParameters.getVariableArcThreshold());
	    		oclpm.setObjectTypesAll(discoveryParameters.getObjectTypesPlaceNets());
	    		oclpms.add(oclpm);
	    	}
    	}
    	if (discoveryParameters.getVariableArcIdentification() == VariableArcIdentification.PER_LPM) {
    		Main.updateProgress("Finished variable arc identification");
    	}
    	this.addAll(oclpms);
    	copyDiscoveryParameters(discoveryParameters);
    	this.refreshColors();
    }
    
    /**
     * Ensures that the places in the OCLPMs have the same ids as the places in the placeSet by replacing them 
	 * with equal (isomorphic + same type) places from the placeSet. 
	 * (done in the grabIsomorphicPlaces function)
     * @param discoveryParameters
     * @param tlpms
     * @param placeSet
     */
    public OCLPMResult (OCLPMDiscoveryParameters discoveryParameters, LPMResultsTagged tlpms, TaggedPlaceSet placeSet) {
    	HashSet<ObjectCentricLocalProcessModel> oclpms = new HashSet<ObjectCentricLocalProcessModel>(tlpms.size());
    	// convert LPM objects into OCLPM objects
    	for (TaggedLPMResult res : tlpms.getElements()) { // for all used case notions
	    	for (LocalProcessModel lpm : res.getElements()) { // for all lpms discovered for that notion
	    		ObjectCentricLocalProcessModel oclpm = new ObjectCentricLocalProcessModel(lpm, res.getCaseNotion(), discoveryParameters.getVariableArcThreshold(), placeSet);
	    		oclpm.setObjectTypesAll(discoveryParameters.getObjectTypesPlaceNets());
	    		if (!oclpms.add(oclpm)) {
	    			// oclpm is already contained (but with different discovery type)
	    			// Yeah this for loop is bad but there is no option to get the equal element from the HashSet
	    			for (ObjectCentricLocalProcessModel oclpm2 : oclpms) {
	    				if (oclpm.equals(oclpm2)) {
	    					oclpm2.getDiscoveryTypes().addAll(oclpm.getDiscoveryTypes());
	    				}
	    			}
	    		}
	    	}
    	}
    	if (discoveryParameters.getVariableArcIdentification() == VariableArcIdentification.PER_LPM) {
    		Main.updateProgress("Finished variable arc identification");
    	}
    	this.addAll(oclpms);
    	copyDiscoveryParameters(discoveryParameters);
    	this.refreshColors();
    	this.setPlaceSet(placeSet);
    }
    
    /**
     * Sets the places as OCLPMs. (for place set visualization)
     * @param placeSet
     */
    public OCLPMResult (TaggedPlaceSet placeSet) {
    	super();
    	this.placeSet = placeSet;
    	this.objectTypes = new HashSet<String>();
    	for (TaggedPlace tp : placeSet.getElements()) {
    		this.objectTypes.add(tp.getObjectType());
    		ObjectCentricLocalProcessModel oclpm = new ObjectCentricLocalProcessModel(tp);
    		this.add(oclpm);
    	}
    	this.refreshColors();
    }
    
    public void copyDiscoveryParameters(OCLPMDiscoveryParameters discoveryParameters) {
    	this.oclpmDiscoverySettings = discoveryParameters.toString();
    	this.oclpmDiscoverySettingsHTML = discoveryParameters.toHTMLBody();
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
	}

	public HashMap<String, Color> getMapTypeColor() {
		return mapTypeColor;
	}

	public void setMapTypeColor(HashMap<String, Color> mapTypeColor) {
		this.mapTypeColor = mapTypeColor;
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
		newResult.setOclpmDiscoverySettings(this.getOclpmDiscoverySettings()); // make place completion changeable? removed it from the settings print
    	newResult.setLpmDiscoveryTypes(this.getLpmDiscoveryTypes());
    	
    	newResult.getObjectTypes().addAll(this.getObjectTypes());
    	
		// places themselves will not be altered, only which places the OCLPMs use
		for (ObjectCentricLocalProcessModel oclpm : this.elements) {
			ObjectCentricLocalProcessModel newOclpm = new ObjectCentricLocalProcessModel(oclpm);
			newResult.add(newOclpm);
		}
		
    	newResult.refreshColors();
		return newResult;
	}

	public void showExternalObjectFlow(ExternalObjectFlow selected, PlaceCompletion currentPlaceCompletion) {
		if (this.showExternalObjectFlow.equals(selected)) {
			return; // already in correct state
		}
		
		// remove external object flow places if present
		if (this.isShowExternalObjectFlow()) {
			this.removeExternalObjectFlow();
		}
		
		// add places
		switch (selected) {
			case NONE:
				// places have already been removed
				break;
			case ALL:
			case ALL_VISIBLE:
				// Add all places so that all object flow interruptions are fixed ?
				for (ObjectCentricLocalProcessModel oclpm : this.getElements()) {
					oclpm.addExternalObjectFlowAll(selected, currentPlaceCompletion);
				}
				this.showExternalObjectFlow = selected;
				break;
			case START_END:
			case START_END_VISIBLE:
				// Add places for starting and ending transitions
				for (ObjectCentricLocalProcessModel oclpm : this.getElements()) {
					oclpm.addExternalObjectFlowStartEnd(this.getStartingActivities(), this.getEndingActivities(), selected, currentPlaceCompletion);
				}
				this.showExternalObjectFlow = selected;
				break;
			default:
				break;
		}
	}
	
	public void removeExternalObjectFlow() {
		for (ObjectCentricLocalProcessModel oclpm : this.getElements()) {
			oclpm.removeExternalObjectFlow(this.getStartingActivities(), this.getEndingActivities());
		}
		this.showExternalObjectFlow = ExternalObjectFlow.NONE;
	}
	
	public boolean isShowExternalObjectFlow() {
		return !this.showExternalObjectFlow.equals(ExternalObjectFlow.NONE);
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

	public String getOclpmDiscoverySettingsHTML() {
		return "<html><body>" + oclpmDiscoverySettingsHTML + "</body></html>";
	}
	
	public String getOclpmDiscoverySettingsHTMLBody() {
		return oclpmDiscoverySettingsHTML;
	}

	public void setOclpmDiscoverySettingsHTML(String oclpmDiscoverySettingsHTML) {
		this.oclpmDiscoverySettingsHTML = oclpmDiscoverySettingsHTML;
	}
	
	public String getLPMDiscoveryParametersHTMLBody() {
		String settings = " <br>"+"<b>LPM Discovery Parameters:</b>"+"<br>";
		settings += "Place Limit: "+this.PBLPMDiscoveryParameters.getPlaceChooserParameters().getPlaceLimit()+"<br>";
		settings += "Time Limit: "+this.PBLPMDiscoveryParameters.getTimeLimit()/60000+" minutes <br>";
		settings += "Min Places: "+this.PBLPMDiscoveryParameters.getLpmCombinationParameters().getMinNumPlaces()+"<br>";
		settings += "Max Places: "+this.PBLPMDiscoveryParameters.getLpmCombinationParameters().getMaxNumPlaces()+"<br>";
		settings += "Min Transitions: "+this.PBLPMDiscoveryParameters.getLpmCombinationParameters().getMinNumTransitions()+"<br>";
		settings += "Max Transitions: "+this.PBLPMDiscoveryParameters.getLpmCombinationParameters().getMaxNumTransitions()+"<br>";
		settings += "Proximity Size: "+this.PBLPMDiscoveryParameters.getLpmCombinationParameters().getLpmProximity()+"<br>";
		settings += "Concurrent Cardinality: "+this.PBLPMDiscoveryParameters.getLpmCombinationParameters().getConcurrencyCardinality()+"<br>";
		return settings;
	}

	public void recalculateEvaluation() {
		for (ObjectCentricLocalProcessModel oclpm : this.elements) {
			oclpm.recalculateEvaluation();
		}
		
	}

	public void setExecutionTime(long elapsedTime, String startingFrom) {
		this.executionTime = elapsedTime;
		this.timeStartingFrom = startingFrom;
	}
	
	public double getExecutionTimeMinutes() {
		return Math.round((this.executionTime/1000.0/60.0) * 1000.0)/1000.0;
	}
	
	/**
	 * Returns when the timer was started. E.g., "places" when the timer was started after the place discovery.
	 * Other variants are "enhanced OCEL" and "LPMs".
	 */
	public String getTimeStartingVariant() {
		return this.timeStartingFrom;
	}

	public long getExecutionTimePlaceCompletion() {
		return executionTimePlaceCompletion;
	}
	
	public long getExecutionTimeExternalObjectFlow() {
		return executionTimeExternalObjectFlow;
	}
	
	public double getExecutionTimePlaceCompletionSeconds() {
		return Math.round((executionTimePlaceCompletion/1000.0)*1000.0)/1000.0;
	}
	
	public double getExecutionTimeExternalObjectFlowSeconds() {
		return Math.round((executionTimeExternalObjectFlow/1000.0)*1000.0)/1000.0;
	}
	
	public void setExecutionTimePlaceCompletion(long time) {
		this.executionTimePlaceCompletion = time;
	}
	
	public void setExecutionTimeExternalObjectFlow(long time) {
		this.executionTimeExternalObjectFlow = time;
	}

	public PlaceBasedLPMDiscoveryParameters getPBLPMDiscoveryParameters() {
		return PBLPMDiscoveryParameters;
	}

	public void setPBLPMDiscoveryParameters(PlaceBasedLPMDiscoveryParameters pBLPMDiscoveryParameters) {
		PBLPMDiscoveryParameters = pBLPMDiscoveryParameters;
	}

}
