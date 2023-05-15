package org.processmining.OCLPMDiscovery.model;

import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.Transition;

public class TaggedPlace extends Place{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3783671545908492239L;
	private String objectType;
	
	public TaggedPlace(String objectType) {
		super();
		this.setObjectType(objectType);
	}
	
	public TaggedPlace() {
		super();
	}
	
	// convert place to tagged place
	public TaggedPlace(Place place) {
		super(place.getId());
		for (Transition t : place.getInputTransitions()) {
			this.addInputTransition(t);
		}
		for (Transition t : place.getOutputTransitions()) {
			this.addOutputTransition(t);
		}
		this.setNumTokens(place.getNumTokens());
		this.setAdditionalInfo(place.getAdditionalInfo());
		this.setFinal(place.isFinal());
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
