package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.model.TaggedPlaceSet;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.OCLPMDiscovery.wizards.OCLPMDiscoveryWizard;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.ocel.ocelobjects.OcelEventLog;

@Plugin(
		name = "Discovery of Object-Centric Local Process Models", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"OCEL", 			// 0
				"Set of Places", 			// 1
				"Object Type HashMap", 		// 2 
				"Petri Net", 				// 3
				"Parameters", 				// 4
				"LPMs", 					// 5
				"Object Graph",				// 6
				"Case Notion Labels",		// 7
				"OCLPM Result"				// 8
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
	public static OCLPMResult mineOCLPMs(UIPluginContext context, OcelEventLog ocelOriginal) {
		
		// copy ocel so that we do not alter the input ocel when adding a new case notion
			// this removes empty object types from the log 
		OcelEventLog ocel = OCELUtils.deepCopy(ocelOriginal);
		
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
			uiLabel = "Object-Centric Local Process Model Discovery given tagged place set."
	)
	@PluginVariant(
			variantLabel = "Object-Centric Local Process Model Discovery",
			requiredParameterLabels = {0,1}
	)
	public static OCLPMResult mineOCLPMs(UIPluginContext context, OcelEventLog ocelOriginal, TaggedPlaceSet placeSet) throws Exception {
		
		if (!(placeSet.getList().getElement(0) instanceof TaggedPlace)) {
			throw new Exception("Given places aren't tagged with object types.");
		}
		
		// copy ocel so that we do not alter the input ocel when adding a new case notion
			// this removes empty object types from the log 
		OcelEventLog ocel = OCELUtils.deepCopy(ocelOriginal);
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);

		OCLPMDiscoveryWizard wizard = OCLPMDiscoveryWizard.setUp(parameters, false, true);
		
		// show wizard
		parameters = ProMWizardDisplay.show(context, wizard, parameters);

		if (parameters == null)
			return null;		
		
		Main.setUp(context, parameters, false, true);
		Object[] result = Main.run(ocel, parameters, placeSet);
		return (OCLPMResult) result[0];
	}
	
	// variant skipping the place net discovery and object graph computation
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Object-Centric Local Process Model Discovery given place set and object graph."
	)
	@PluginVariant(
			variantLabel = "Object-Centric Local Process Model Discovery",
			requiredParameterLabels = {0,1,6}
	)
	public static OCLPMResult mineOCLPMs (
			UIPluginContext context, OcelEventLog ocelOriginal, TaggedPlaceSet placeSet, Graph<String,DefaultEdge> graph
			) throws Exception {
		
		if (!(placeSet.getList().getElement(0) instanceof TaggedPlace)) {
			throw new Exception("Given places aren't tagged with object types.");
		}
		
		// copy ocel so that we do not alter the input ocel when adding a new case notion
			// this removes empty object types from the log 
		OcelEventLog ocel = OCELUtils.deepCopy(ocelOriginal);
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);

		OCLPMDiscoveryWizard wizard = OCLPMDiscoveryWizard.setUp(parameters, false, true);
		
		// show wizard
		parameters = ProMWizardDisplay.show(context, wizard, parameters);

		if (parameters == null)
			return null;		

		Main.setUp(context, parameters, false, true);
		Main.setGraph(graph);
		Object[] result = Main.run(ocel, parameters, placeSet);
		return (OCLPMResult) result[0];
	}
	
	// variant skipping the place net and LPM discovery
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Object-Centric Local Process Model Discovery given LPMs with tagged places."
	)
	@PluginVariant(
			variantLabel = "Object-Centric Local Process Model Discovery given LPMs with tagged places.",
			requiredParameterLabels = {0,5,1}
	)
	public static OCLPMResult mineOCLPMs(PluginContext context, OcelEventLog ocelOriginal, LPMResultsTagged lpms, TaggedPlaceSet placeSet) throws Exception {
		// copy ocel so that we do not alter the input ocel when adding a new case notion
			// this removes empty object types from the log 
		OcelEventLog ocel = OCELUtils.deepCopy(ocelOriginal);
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);
		Main.setUp(context, parameters, false, false);
		return (OCLPMResult) Main.run(ocel, parameters, lpms, placeSet)[0];
	}
	
//	// variant accepting enhanced ocel (ocel which already has case notions to be used for LPM discovery)
//	@UITopiaVariant(
//			affiliation = "RWTH - PADS",
//			author = "Marvin Porsil",
//			email = "marvin.porsil@rwth-aachen.de",
//			uiLabel = "Object-Centric Local Process Model Discovery given place set and OCEL with desired case notions."
//	)
//	@PluginVariant(
//			variantLabel = "Object-Centric Local Process Model Discovery given place set and OCEL with desired case notions.",
//			requiredParameterLabels = {0,1,2,6}
//	)
//	public static OCLPMResult mineOCLPMs (
//			UIPluginContext context, OcelEventLog ocel, ArrayList<String> labels, TaggedPlaceSet placeSet
//			) {
//		
//		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);
//
//		// adjust for this case where new case notion already given
//		OCLPMDiscoveryWizard wizard = OCLPMDiscoveryWizard.setUp(parameters, false, true);
//		
//		// show wizard
//		parameters = ProMWizardDisplay.show(context, wizard, parameters);
//
//		if (parameters == null)
//			return null;		
//
//		Main.setUp(context, parameters, false, true);
//		Object[] result = Main.run(ocel, parameters, placeSet, labels);
//		return (OCLPMResult) result[0];
//	}
	
	// just the post processing (only used this for testing)
//	@UITopiaVariant(
//			affiliation = "RWTH - PADS",
//			author = "Marvin Porsil",
//			email = "marvin.porsil@rwth-aachen.de",
//			uiLabel = "Repair OCLPMResult"
//	)
//	@PluginVariant(
//			variantLabel = "Repair OCLPMResult",
//			requiredParameterLabels = {8}
//	)
//	public static OCLPMResult mineOCLPMs(PluginContext context, OCLPMResult result) {
//		return (OCLPMResult) Main.postProcessing(null, result);
//	}
	
	//==============================================
	// All variants without UI
	//==============================================
	
	/**
	 * Complete OCLPM discovery
	 * @param parameters
	 * @param ocel
	 * @return
	 */
	public static OCLPMResult mineOCLPMs(OCLPMDiscoveryParameters parameters, OcelEventLog ocel) {
		Object[] result = Main.run(ocel, parameters); 
		return (OCLPMResult) result[0];
	}
	
	/**
	 * OCLPM discovery without place discovery.
	 * @param parameters
	 * @param ocel
	 * @param placeSet
	 * @return
	 */
	public static OCLPMResult mineOCLPMs(OCLPMDiscoveryParameters parameters, OcelEventLog ocel, TaggedPlaceSet placeSet) {
		Object[] result = Main.run(ocel, parameters, placeSet);
		return (OCLPMResult) result[0];
	}
	
	/**
	 * Object-Centric Local Process Model Discovery given place set and object graph. 
	 * @param parameters
	 * @param ocel
	 * @param placeSet
	 * @param graph
	 * @return
	 * @throws Exception
	 */
	public static OCLPMResult mineOCLPMs (
			OCLPMDiscoveryParameters parameters, OcelEventLog ocel, TaggedPlaceSet placeSet, Graph<String,DefaultEdge> graph
			) {
		
//		if (!(placeSet.getList().getElement(0) instanceof TaggedPlace)) {
//			throw new Exception("Given places aren't tagged with object types.");
//		}

		Main.setGraph(graph);
		Object[] result = Main.run(ocel, parameters, placeSet);
		return (OCLPMResult) result[0];
	}
	
	/**
	 * Object-Centric Local Process Model Discovery given LPMs with tagged places.
	 * @param parameters
	 * @param ocel
	 * @param lpms
	 * @param placeSet
	 * @return
	 * @throws Exception
	 */
	public static OCLPMResult mineOCLPMs(OCLPMDiscoveryParameters parameters, OcelEventLog ocel, LPMResultsTagged lpms, TaggedPlaceSet placeSet) throws Exception {
		return (OCLPMResult) Main.run(ocel, parameters, lpms, placeSet)[0];
	}
	
	/**
	 *  OCLPM discovery variant accepting enhanced ocel (ocel which already has case notions to be used for LPM discovery).
	 * @param parameters
	 * @param ocel
	 * @param labels Are the names of the columns of the provided case notions.
	 * @param placeSet
	 * @return
	 */
	public static OCLPMResult mineOCLPMs (
			OCLPMDiscoveryParameters parameters, OcelEventLog ocel, ArrayList<String> labels, TaggedPlaceSet placeSet
			) {
		Object[] result = Main.run(ocel, parameters, placeSet, labels);
		return (OCLPMResult) result[0];
	}
	
}
