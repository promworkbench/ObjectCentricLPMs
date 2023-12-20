package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.HashSet;

import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.model.TaggedPlaceSet;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.utils.FlatLogProcessing;
import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.OCLPMDiscovery.wizards.OCLPMDiscoveryWizard;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.ocel.ocelobjects.OcelEventLog;

@Plugin(
		name = "Discovery of Place Set on OCEL", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"OCEL", "Parameters", "Petri Net"},
		returnLabels = { "Place Set"},
		returnTypes = { TaggedPlaceSet.class},
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
	public static TaggedPlaceSet minePlaceSet (UIPluginContext context, OcelEventLog ocelOriginal) {
		
		// copy ocel so that we do not alter the input ocel when adding a new case notion
			// this removes empty object types from the log 
		OcelEventLog ocel = OCELUtils.deepCopy(ocelOriginal);
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);

		OCLPMDiscoveryWizard wizard = OCLPMDiscoveryWizard.setUp(parameters, true, false);
		
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
		
		Main.setUp(context, parameters, true, false);
		TaggedPlaceSet placeSet = Main.discoverPlaceSet(ocel, parameters);
		
		Main.messageNormal("Obtained "+placeSet.size()+" place nets for LPM discovery.");
		
		TaggedPlaceSet placeSetTrimmed = new TaggedPlaceSet(placeSet);
		placeSetTrimmed.removeIsomorphic();
		placeSet.setNumberOfUniquePlaces(placeSetTrimmed.size());
		Main.messageNormal("LPM discovery will start with "+placeSetTrimmed.size()+" unique place nets.");
		
		return placeSet;
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
	public static TaggedPlaceSet convertToPlaceSet (UIPluginContext context, Petrinet petriNet) {
		Main.setUp(context);
		
		HashSet<TaggedPlace> places = FlatLogProcessing.convertPetriNetToTaggedPlaceNets(context, petriNet, "");
		TaggedPlaceSet placeSet = new TaggedPlaceSet(places);

		return placeSet;
	}
}
