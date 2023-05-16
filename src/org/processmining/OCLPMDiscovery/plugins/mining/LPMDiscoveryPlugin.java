package org.processmining.OCLPMDiscovery.plugins.mining;

import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.wizards.OCLPMDiscoveryWizard;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;

@Plugin(
		name = "Discovery of Local Process Models", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"Log", "Set of Places", "Petri Net", "Parameters"},
		returnLabels = { "LPM set" },
		returnTypes = { LPMResult.class },
		help = "Discovers Local Process Models on an object-centric event log (OCEL standard)."
				+ "In case a case notion strategy using multiple case notions is chosen "
				+ "the resulting LPMResults are merged togeather for all notions."
)
public class LPMDiscoveryPlugin {
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Local Process Model Discovery given OCEL"
	)
	@PluginVariant(
			variantLabel = "Local Process Model Discovery",
			requiredParameterLabels = {0}
	)
	public static LPMResult mineLPMs(UIPluginContext context, OcelEventLog ocel) {
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);

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
		LPMResultsTagged tlpms = Main.runLPMDiscovery(ocel, parameters);
		return mergeLPMResults(tlpms);
	}
	
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "Local Process Model Discovery given OCEL and PlaceSet"
	)
	@PluginVariant(
			variantLabel = "Local Process Model Discovery given OCEL and PlaceSet",
			requiredParameterLabels = {0,1}
	)
	public static LPMResult mineLPMs(UIPluginContext context, OcelEventLog ocel, PlaceSet placeSet) {
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);

		OCLPMDiscoveryWizard wizard = OCLPMDiscoveryWizard.setUp(parameters, false, true);
		
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
		Main.setUp(context, parameters, false, true);
		LPMResultsTagged tlpms = Main.runLPMDiscovery(ocel, parameters, placeSet);
		return mergeLPMResults(tlpms);
	}
	
	public static LPMResult mergeLPMResults(LPMResultsTagged tlpms) {
		LPMResult lpms = new LPMResult();
		for (LPMResult res : tlpms.getTypeMap().keySet()) {
			lpms.addAll(res.getElements());
		}
		return lpms;
	}
}
