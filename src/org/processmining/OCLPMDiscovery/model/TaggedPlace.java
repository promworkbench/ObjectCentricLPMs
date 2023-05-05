package org.processmining.OCLPMDiscovery.model;

import org.processmining.placebasedlpmdiscovery.model.Place;

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

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
