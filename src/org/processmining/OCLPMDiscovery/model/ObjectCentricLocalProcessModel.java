package org.processmining.OCLPMDiscovery.model;

import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;

public class ObjectCentricLocalProcessModel extends LocalProcessModel{
	private static final long serialVersionUID = -1533299531712229854L;
	
	// leading type for which this OCLPM has been discovered
	private final String discoveryType;
	
	public ObjectCentricLocalProcessModel(LocalProcessModel lpm, String discoveryType) {
		super(lpm);
		this.discoveryType = discoveryType;
	}

	public String getDiscoveryType() {
		return discoveryType;
	}

}
