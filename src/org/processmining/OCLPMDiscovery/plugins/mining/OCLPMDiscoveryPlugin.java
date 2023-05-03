package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.OCLPMDiscovery.wizards.OCLPMDiscoveryWizard;
import org.processmining.OCLPMDiscovery.wizards.steps.LPMDiscoveryWizardStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoveryDummyFinishStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoveryLPMStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoverySPECppStep;
import org.processmining.OCLPMDiscovery.wizards.steps.OCLPMDiscoverySettingsStep;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.plugins.mining.PlaceBasedLPMDiscoveryPlugin;

@Plugin(
		name = "Discovery of Object-Centric Local Process Models", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"Log", "Set of Places", "Petri Net", "Parameters"},
		returnLabels = { "OCLPM set" },
		returnTypes = { OCLPMResult.class },
		help = "Discovers Object-Centric Local Process Models on an object-centric event log (OCEL standard)."
)
public class OCLPMDiscoveryPlugin {
		
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "OCLPM test uiLabel" // name shown in ProM
	)
	@PluginVariant(
			variantLabel = "OCLPM test variantLabel", // this isn't shown anywhere?
			requiredParameterLabels = {}
	)
	public static String helloWorld(PluginContext context) {
		return "Hello World";
	}
	
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Object-Centric Local Process Model Discovery given OCEL"
	)
	@PluginVariant(
			variantLabel = "Object-Centric Local Process Model Discovery",
			requiredParameterLabels = {0}
	)
	public static OCLPMResult mineOCLPMs(UIPluginContext context, OcelEventLog ocel) {
		Main.setUp(context);
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);

		Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> stepMap = new HashMap<>();
		
		// let user select object types for which to discover place nets
		// let user select object types to use as leading types for process executions		
		stepMap.put(OCLPMDiscoveryWizard.INITIAL_KEY, new OCLPMDiscoverySettingsStep(parameters));
		
		// TODO let user select parameters for Place Net discovery
//		stepMap.put(OCLPMDiscoveryWizard.PD_ILP, new OCLPMDiscoveryILPStep(parameters));
		stepMap.put(OCLPMDiscoveryWizard.PD_SPECPP, new OCLPMDiscoverySPECppStep(parameters));
		
		
		// TODO let user select parameters for LPM discovery
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
		Set<Place> placeNetsUnion = new HashSet<>();
		// place net discovery
		for (String currentType : parameters.getObjectTypesPlaceNets()) {
			// flatten ocel
			XLog flatLog = Flattening.flatten(ocel, currentType);
			System.out.println("Flattened ocel for type "+currentType);
			
			// discover petri net using est-miner (use specpp)
			// split petri net into place nets
			// tag places with current object type
			Set<Place> placeNets = FlatLogProcessing.processFlatLog(context, flatLog, currentType, parameters);
			System.out.println("Finished discovery of place nets for object type "+currentType);

			// unite place nets
			placeNetsUnion.addAll(placeNets);
		}
		
		// convert set of places to PlaceSet
		PlaceSet placeSet = new PlaceSet(placeNetsUnion);
		
		//TODO initialize LPMResult				
		
		switch (parameters.getCaseNotionStrategy()) {
		
			case PE_LEADING:
				// TODO enhance log by process executions as case notions
				// LPM discovery for each new case notion
				for (String currentType : parameters.getObjectTypesLeadingTypes()) {
				
					// TODO flatten ocel
				
					// TODO discover LPMs (name of the currentType column needs concept:name, which the flattening does)
				
				}
				break;
				
			case PE_CONNECTED:
				break;
				
			case DUMMY:
			default:
				String dummyType = "DummyType";
				OcelEventLog dummyOcel = OCELUtils.addDummyCaseNotion(ocel,"DummyType","42");
				XLog log = Flattening.flatten(dummyOcel,dummyType);
				Object[] lpmResults = PlaceBasedLPMDiscoveryPlugin.mineLPMs(context, log, placeSet, parameters.getPBLPMDiscoveryParameters());
		}
		
		//TODO make OCLPMResult object
		
		// TODO assign places to objects
		
		// TODO identify variable arcs
		
		return (OCLPMResult) Main.run()[0];// TODO restructure such that this makes sense 
	}
	
	// variant skipping the place net discovery
	//TODO: variant with input (ocel, set(set(place net),object type))
	
	// variant skipping the place net discovery and process execution computation
	//TODO: variant with input (ocel, set(set(place net),object type), (log,set(column names)))
	
	// TODO all variants without UI
}
