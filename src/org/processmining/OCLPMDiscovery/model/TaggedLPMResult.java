package org.processmining.OCLPMDiscovery.model;

import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;

public class TaggedLPMResult extends LPMResult{
	private String caseNotion;

	public TaggedLPMResult() {
		super();
	}
	
	public TaggedLPMResult(LPMResult lpmResult, String caseNotion) {
		super();
		this.caseNotion = caseNotion;
		for (LocalProcessModel lpm : lpmResult.getElements()) {
			this.add(lpm);
		}
	}
	
	public String getCaseNotion() {
		return caseNotion;
	}

	public void setCaseNotion(String caseNotion) {
		this.caseNotion = caseNotion;
	}
	
}
