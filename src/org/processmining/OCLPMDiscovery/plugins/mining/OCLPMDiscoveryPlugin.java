package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEventLog;

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
	public static OCLPMResult mineOCLPMs(PluginContext context, OcelEventLog ocel) {
		// get object types
		Set<String> objectTypes = ocel.getObjectTypes();
		
		// let user select object types for which to discover place nets
		Set<String> selectedObjectTypes = objectTypes;
		
		// let user select object types to use as leading types for process executions
		
		// let user select parameters for LPM discovery
		
		// for each object type
		for (String currentType : selectedObjectTypes) {
			// flatten ocel
			XLog flatLog = Flattening.flatten(ocel, currentType);
			System.out.println("Flattened ocel for type "+currentType);
			// discover place nets
			
			// tag places with current object type
		}
		
		// unite place nets
		
		// enhance log by process executions as case notions
		
		// for each new case notion
		
			// flatten ocel
		
			// discover LPMs
		
		// assign places to objects
		
		// identify variable arcs
		
		return (OCLPMResult) Main.run()[0];
	}
	
	// variant skipping the place net discovery
	//TODO: variant with input (ocel, set(set(place net),object type))
	
	// variant skipping the place net discovery and process execution computation
	//TODO: variant with input (ocel, set(set(place net),object type), (log,set(column names)))
}
