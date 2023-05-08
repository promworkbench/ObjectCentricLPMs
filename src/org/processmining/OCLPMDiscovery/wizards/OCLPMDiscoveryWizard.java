package org.processmining.OCLPMDiscovery.wizards;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.wizard.MapWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryWizard extends MapWizard<OCLPMDiscoveryParameters, String> {

	public static final String INITIAL_KEY = "OCLPMDiscoverySettings"; // has place discovery settings
	public static final String PD_ILP = "OCLPMDiscoveryILP";
	public static final String PD_SPECPP = "OCLPMDiscoverySPECpp";
	public static final String LPM_NOTION = "OCLPMDiscoveryLPMCaseNotion";
	public static final String LPM_CONFIG = "OCLPMDiscoveryLPMDiscoveryConfig";
	public static final String FINISH = "OCLPMDiscoveryDummyFinish"; // because I failed to make it work without it 
	public static final String[] FINALS = {FINISH};
	
	private final boolean discoverPlaces;
	
	public OCLPMDiscoveryWizard(Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> steps, boolean discoverPlaces) {
		super(steps);
        this.discoverPlaces = discoverPlaces;
    }
	
	@Override
	public Collection<String> getFinalKeys(MapModel<OCLPMDiscoveryParameters, String> currentWizardModel) {
		return Arrays.asList(FINALS);
	}

	@Override
	public String getInitialKey(OCLPMDiscoveryParameters settings) {
		if (this.discoverPlaces) {		
			return INITIAL_KEY;
		}
		else {
			return LPM_NOTION;
		}
	}

	@Override
	public String getNextKey(MapModel<OCLPMDiscoveryParameters, String> wizard) {

		if (this.discoverPlaces) {
			// check if currently at beginning and selected place discovery has wizard step 
			if (wizard.getCurrent().equals(INITIAL_KEY)) {
				switch (wizard.getModel().getPlaceDiscoveryAlgorithm()){
					case ILP:
						return PD_ILP;
					case SPECPP:
						return PD_SPECPP;
					default:
						break;
				}
			}
			
		}
		
		// check if LPM case notion discovery needs a wizard step
		if ( !wizard.getCurrent().equals(LPM_NOTION)
				&& !wizard.getCurrent().equals(LPM_CONFIG)
				){
			return LPM_NOTION;
		}
		
		// do LPM discovery config if not already done
		if (!wizard.getCurrent().equals(LPM_CONFIG)){
			return LPM_CONFIG;
		}
		
		return FINISH;
	}

}
