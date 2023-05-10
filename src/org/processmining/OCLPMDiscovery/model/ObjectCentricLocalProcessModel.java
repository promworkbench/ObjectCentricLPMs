package org.processmining.OCLPMDiscovery.model;

import java.util.HashSet;

import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;

public class ObjectCentricLocalProcessModel extends LocalProcessModel{
	private static final long serialVersionUID = -1533299531712229854L;
	
	// leading types for which this OCLPM has been discovered
	private final HashSet<String> discoveryTypes;
	
	public ObjectCentricLocalProcessModel(LocalProcessModel lpm, String discoveryType) {
		super(lpm);
		this.discoveryTypes = new HashSet<String>();
		this.discoveryTypes.add(discoveryType);
	}

	public HashSet<String> getDiscoveryTypes() {
		return discoveryTypes;
	}

}
