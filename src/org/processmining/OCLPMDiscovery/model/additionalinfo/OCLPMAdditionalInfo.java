package org.processmining.OCLPMDiscovery.model.additionalinfo;

import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.model.additionalinfo.LPMAdditionalInfo;

public class OCLPMAdditionalInfo extends LPMAdditionalInfo{

	private static final long serialVersionUID = -7263640884643072110L;

	public OCLPMAdditionalInfo(LocalProcessModel lpm) {
    	super(lpm);
    }
	
//	public OCLPMAdditionalInfo(ObjectCentricLocalProcessModel oclpm) {
//    	super(oclpm.getLpm());
//    }
	
	public OCLPMAdditionalInfo(LPMAdditionalInfo info) {
    	super(info);
    }

}
