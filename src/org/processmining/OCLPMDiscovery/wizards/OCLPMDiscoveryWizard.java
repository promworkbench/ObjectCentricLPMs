package org.processmining.OCLPMDiscovery.wizards;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.wizards.steps.LPMDiscoveryWizardStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoveryDummyFinishStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoveryILPStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoveryLPMStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoverySPECppStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoverySettingsStep;
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
	private final boolean discoverLPMs;
	
	public OCLPMDiscoveryWizard(Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> steps, boolean discoverPlaces) {
		this(steps, discoverPlaces, true);
    }
	
	public OCLPMDiscoveryWizard(Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> steps, boolean discoverPlaces, boolean discoverLPMs) {
		super(steps);
		this.discoverPlaces = discoverPlaces;
		this.discoverLPMs = discoverLPMs;
    }
	
	public static OCLPMDiscoveryWizard setUp(OCLPMDiscoveryParameters parameters, boolean discoverPlaces, boolean discoverLPMs) {
		
		Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> stepMap = new HashMap<>();
		
		// let user select object types for which to discover place nets
		// let user select object types to use as leading types for process executions		
		stepMap.put(OCLPMDiscoveryWizard.INITIAL_KEY, new OCLPMDiscoverySettingsStep(parameters));
		
		if (discoverPlaces) {
			// let user select parameters for Place Net discovery
			stepMap.put(OCLPMDiscoveryWizard.PD_ILP, new OCLPMDiscoveryILPStep(parameters)); 
			stepMap.put(OCLPMDiscoveryWizard.PD_SPECPP, new OCLPMDiscoverySPECppStep(parameters));		
		}
		
		if (discoverLPMs) {
			// let user select parameters for LPM discovery
			// TODO add wizard step where user can select object types to consider in general (set parameters objectTypesCaseNotion)
			stepMap.put(OCLPMDiscoveryWizard.LPM_NOTION, new OCLPMDiscoveryLPMStep(parameters));
			stepMap.put(OCLPMDiscoveryWizard.LPM_CONFIG, new LPMDiscoveryWizardStep(parameters));
		}
		
		// when users only wants to convert LPMs + HashMap to OCLPMResult they need to specify the used LPM notion
		if (!discoverPlaces && !discoverLPMs) {
			stepMap.put(OCLPMDiscoveryWizard.LPM_NOTION, new OCLPMDiscoveryLPMStep(parameters));
		}
			
		stepMap.put(OCLPMDiscoveryWizard.FINISH, new OCLPMDiscoveryDummyFinishStep(parameters));
		
		return new OCLPMDiscoveryWizard(stepMap, discoverPlaces, discoverLPMs);
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
		
		if (this.discoverLPMs) {
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
		}
		
		return FINISH;
	}

}
