package org.processmining.OCLPMDiscovery.wizards;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.wizard.MapWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryWizard extends MapWizard<OCLPMDiscoveryParameters, String> {

	public static final String INITIAL_KEY = "OCLPMDiscoverySettings";
	public static final String PD_ILP = "OCLPMDiscoveryILP";
	
	private final boolean discoverPlaces;
	
	public OCLPMDiscoveryWizard(Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> steps, boolean discoverPlaces) {
		super(steps);
        this.discoverPlaces = discoverPlaces;
    }
	
	@Override
	public Collection<String> getFinalKeys(MapModel<OCLPMDiscoveryParameters, String> currentWizardModel) {
		// TODO Auto-generated method stub
		return Collections.singletonList(OCLPMDiscoveryWizard.INITIAL_KEY);
	}

	@Override
	public String getInitialKey(OCLPMDiscoveryParameters settings) {
		return OCLPMDiscoveryWizard.INITIAL_KEY;
	}

	@Override
	public String getNextKey(MapModel<OCLPMDiscoveryParameters, String> wizard) {
		
		switch (wizard.getModel().getPlaceDiscoveryAlgorithm()){
			case ILP:
				return PD_ILP;
			default:
				return PD_ILP;
		}
	}

}
