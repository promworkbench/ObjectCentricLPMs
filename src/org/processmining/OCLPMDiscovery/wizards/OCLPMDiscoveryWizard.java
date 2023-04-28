package org.processmining.OCLPMDiscovery.wizards;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.processmining.OCLPMDiscovery.parameters.CaseNotionStrategy;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.wizard.MapWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryWizard extends MapWizard<OCLPMDiscoveryParameters, String> {

	public static final String INITIAL_KEY = "OCLPMDiscoverySettings";
	public static final String PD_ILP = "OCLPMDiscoveryILP";
	public static final String PD_SPECPP = "OCLPMDiscoverySPECpp";
	public static final String LPM = "OCLPMDiscoveryLPM";
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
		return OCLPMDiscoveryWizard.INITIAL_KEY;
	}

	@Override
	public String getNextKey(MapModel<OCLPMDiscoveryParameters, String> wizard) {

		// check if currently at beginning and selected place discovery has wizard step 
		if (wizard.getCurrent().equals(INITIAL_KEY)) {
			switch (wizard.getModel().getPlaceDiscoveryAlgorithm()){
				case ILP:
					break; // currently using external wizard from ILP
//					return PD_ILP;
				case SPECPP:
					return PD_SPECPP;
				default:
					break;
			}
		}
		
		// check if LPM case notion discovery needs a wizard step
		if (wizard.getModel().getCaseNotionStrategy().equals(CaseNotionStrategy.PE_LEADING)
				&& !wizard.getCurrent().equals(LPM)){
			return LPM;
		}
		
		return FINISH;
	}

}
