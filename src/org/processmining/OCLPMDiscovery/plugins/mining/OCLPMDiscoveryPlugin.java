package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.parameters.CaseNotionStrategy;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.wizards.OCLPMDiscoveryWizard;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;

@Plugin(
		name = "Discovery of Object-Centric Local Process Models", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"OCEL", 			// 0
				"Set of Places", 			// 1
				"Object Type HashMap", 		// 2 
				"Petri Net", "Parameters", 	// 3
				"LPMs", 					// 4
				"Object Graph" 				// 5
				},
		returnLabels = { "OCLPM Result"},
		returnTypes = { OCLPMResult.class},
		help = "Discovers Object-Centric Local Process Models on an object-centric event log (OCEL standard).",
		userAccessible = true
)
public class OCLPMDiscoveryPlugin {
	
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
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);
		
		// setup wizard with place discovery and LPM discovery
		OCLPMDiscoveryWizard wizard = OCLPMDiscoveryWizard.setUp(parameters, true, true); 
		
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
		
		Main.setUp(context, parameters, true, true);
		
		Object[] result = Main.run(ocel, parameters); 
		return (OCLPMResult) result[0];
	}
	
	// variant skipping the place net discovery
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Object-Centric Local Process Model Discovery given place set and hashmap of object types."
	)
	@PluginVariant(
			variantLabel = "Object-Centric Local Process Model Discovery",
			requiredParameterLabels = {0,1,2}
	)
	public static OCLPMResult mineOCLPMs(UIPluginContext context, OcelEventLog ocel, PlaceSet placeSet, HashMap<String,String> typeMap) {
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);
		
		// just for printing settings...
		parameters.setObjectTypesPlaceNets(new HashSet<String>(typeMap.values()));

		OCLPMDiscoveryWizard wizard = OCLPMDiscoveryWizard.setUp(parameters, false, true);
		
		// show wizard
		parameters = ProMWizardDisplay.show(context, wizard, parameters);

		if (parameters == null)
			return null;		
		
		Main.setUp(context, parameters, false, true);
		Object[] result = Main.run(ocel, parameters, placeSet, typeMap);
		return (OCLPMResult) result[0];
	}
	
	// variant skipping the place net discovery and object graph computation
		@UITopiaVariant(
				affiliation = "RWTH - PADS",
				author = "Marvin Porsil",
				email = "marvin.porsil@rwth-aachen.de",
				uiLabel = "Object-Centric Local Process Model Discovery given place set, hashmap and object graph."
		)
		@PluginVariant(
				variantLabel = "Object-Centric Local Process Model Discovery",
				requiredParameterLabels = {0,1,2,5}
		)
		public static OCLPMResult mineOCLPMs (
				UIPluginContext context, OcelEventLog ocel, PlaceSet placeSet, 
				HashMap<String,String> typeMap, Graph<String,DefaultEdge> graph
				) {
			
			OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);
			
			// just for printing settings...
			parameters.setObjectTypesPlaceNets(new HashSet<String>(typeMap.values()));

			OCLPMDiscoveryWizard wizard = OCLPMDiscoveryWizard.setUp(parameters, false, true);
			
			// show wizard
			parameters = ProMWizardDisplay.show(context, wizard, parameters);

			if (parameters == null)
				return null;		
			
			Main.setUp(context, parameters, false, true);
			Main.setGraph(graph);
			Object[] result = Main.run(ocel, parameters, placeSet, typeMap);
			return (OCLPMResult) result[0];
		}
	
	// variant skipping the place net discovery and process execution computation
	//TODO: variant with input (ocel, set(set(place net),object type), (log,set(column names)))
	
	// variant skipping the place net and LPM discovery
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Object-Centric Local Process Model Discovery given hashmap of object types and LPMs."
	)
	@PluginVariant(
			variantLabel = "Object-Centric Local Process Model Discovery given hashmap of object types and LPMs.",
			requiredParameterLabels = {0,2,5}
	)
	public static OCLPMResult mineOCLPMs(PluginContext context, OcelEventLog ocel, HashMap<String,String> typeMap, LPMResult lpms) {
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel); // TODO OCEL necessary?
		parameters.setCaseNotionStrategy(CaseNotionStrategy.DUMMY); // TODO make user selectable
		Main.setUp(context, parameters, false, false);
		return (OCLPMResult) Main.run(ocel, parameters, typeMap, lpms)[0];
	}
	
	//==============================================
	// TODO all variants without UI
	//==============================================
	public static OCLPMResult mineOCLPMs(OCLPMDiscoveryParameters parameters, OcelEventLog ocel) {
		Object[] result = Main.run(ocel, parameters); 
		return (OCLPMResult) result[0];
	}
	
	public static OCLPMResult mineOCLPMs(OCLPMDiscoveryParameters parameters, OcelEventLog ocel, PlaceSet placeSet, HashMap<String,String> typeMap) {
		Object[] result = Main.run(ocel, parameters, placeSet, typeMap);
		return (OCLPMResult) result[0];
	}
}
