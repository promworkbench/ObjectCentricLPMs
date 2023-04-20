package org.processmining.OCLPMDiscovery.plugins.mining.wizards;

import java.util.Collection;
import java.util.Map;

import org.processmining.OCLPMDiscovery.plugins.mining.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.wizard.MapWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryWizard extends MapWizard<OCLPMDiscoveryParameters, String>{

	public static final String INITIAL_KEY = "OCLPMDiscoveryParameters";
	
	private final boolean discoverPlaces;
	
	public OCLPMDiscoveryWizard(Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> steps, boolean discoverPlaces) {
		super(steps);
        this.discoverPlaces = discoverPlaces;
    }
	
	@Override
	public Collection<String> getFinalKeys(MapModel<OCLPMDiscoveryParameters, String> currentWizardModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInitialKey(OCLPMDiscoveryParameters settings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNextKey(MapModel<OCLPMDiscoveryParameters, String> currentWizardModel) {
		// TODO Auto-generated method stub
		return null;
	}

}
