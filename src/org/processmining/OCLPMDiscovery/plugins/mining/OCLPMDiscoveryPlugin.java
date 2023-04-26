package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.wizards.OCLPMDiscoveryWizard;
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
			uiLabel = "OCLPM flatten ocel for all object-types."
	)
	@PluginVariant(
			variantLabel = "OCLPM flatten ocel for all object-types.",
			requiredParameterLabels = {0}
	)
	public static String flattenAll(PluginContext context, OcelEventLog ocel) {
		return (String) Main.run()[0];
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
		
		// get object types
		Set<String> objectTypes = ocel.getObjectTypes();
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(objectTypes);
		

		Map<String, ProMWizardStep<OCLPMDiscoveryParameters>> stepMap = new HashMap<>();
		
		// let user select object types for which to discover place nets
		// let user select object types to use as leading types for process executions		
		stepMap.put(OCLPMDiscoveryWizard.INITIAL_KEY, new OCLPMDiscoverySettingsStep(parameters));
		
		// TODO let user select parameters for Place Net discovery
//		stepMap.put(OCLPMDiscoveryWizard.PD_ILP, new OCLPMDiscoveryILPStep(parameters));
		
		
		// TODO let user select parameters for LPM discovery
		
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
				
		// TODO enhance log by process executions as case notions
		
		// LPM discovery for each new case notion
		for (String currentType : parameters.getObjectTypesLeadingTypes()) {
		
			// TODO flatten ocel
		
			// TODO discover LPMs
		
		}
		
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
