package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.wizards.OCLPMDiscoveryWizard;
import org.processmining.OCLPMDiscovery.wizards.steps.LPMDiscoveryWizardStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoveryDummyFinishStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoveryLPMStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoverySPECppStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoverySettingsStep;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;

@Plugin(
		name = "Discovery of Place Set on OCEL", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"OCEL", "Parameters", "Petri Net"},
		returnLabels = { "Place Set", "Object Type Map"},
		returnTypes = { PlaceSet.class, HashMap.class },
		help = "Discovers a Place Set from an object-centric event log (OCEL standard)."
)
public class PlaceSetDiscoveryPlugin {
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Discovery of Place Set on OCEL"
	)
	@PluginVariant(
			variantLabel = "Discovery of Place Set on OCEL",
			requiredParameterLabels = {0}
	)
	public static Object[] minePlaceSet (UIPluginContext context, OcelEventLog ocel) {
		Main.setUp(context);
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);

		Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> stepMap = new HashMap<>();
		
		// let user select object types for which to discover place nets
		// let user select object types to use as leading types for process executions		
		stepMap.put(OCLPMDiscoveryWizard.INITIAL_KEY, new OCLPMDiscoverySettingsStep(parameters));
		
		// let user select parameters for Place Net discovery
//		stepMap.put(OCLPMDiscoveryWizard.PD_ILP, new OCLPMDiscoveryILPStep(parameters)); 
			//TODO integrate ILP wizard here or warn the user that it follows afterwards
		stepMap.put(OCLPMDiscoveryWizard.PD_SPECPP, new OCLPMDiscoverySPECppStep(parameters));
		// TODO implement the eST miner or just remove it from the selection
		
		
		// let user select parameters for LPM discovery
		stepMap.put(OCLPMDiscoveryWizard.LPM_NOTION, new OCLPMDiscoveryLPMStep(parameters));
		stepMap.put(OCLPMDiscoveryWizard.LPM_CONFIG, new LPMDiscoveryWizardStep(parameters));
		
		stepMap.put(OCLPMDiscoveryWizard.FINISH, new OCLPMDiscoveryDummyFinishStep(parameters));
		OCLPMDiscoveryWizard wizard = new OCLPMDiscoveryWizard(stepMap, true);
		
		// show wizard
		parameters = ProMWizardDisplay.show(context, wizard, parameters);

		if (parameters == null)
			return null;		
		
		// open external wizards for place discovery
		switch (parameters.getPlaceDiscoveryAlgorithm()) {
			case ILP:
				XLogHybridILPMinerParametersImpl ilpParameters = ILPMiner.startILPWizard(context, parameters, ocel);
				parameters.setIlpParameters(ilpParameters);
				break;
			default:
		}
		return Main.discoverPlaceSet(ocel, parameters);
	}
	
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Convert Petri Net into Place Set"
	)
	@PluginVariant(
			variantLabel = "Convert Petri Net into Place Set",
			requiredParameterLabels = {2}
	)
	public static PlaceSet convertToPlaceSet (UIPluginContext context, Petrinet petriNet) {
		Main.setUp(context);
		
		Set<Place> places = FlatLogProcessing.convertPetriNetToPlaceNets(context, petriNet, "");
		PlaceSet placeSet = new PlaceSet(places);

		return placeSet;
	}
}
