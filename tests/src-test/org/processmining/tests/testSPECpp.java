package org.processmining.tests;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.plugins.mining.FlatLogProcessing;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

@Plugin(
		name = "Testing SPECpp for OCLPMs", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"Log", "Set of Places", "Petri Net", "Parameters", "Object Type"},
		returnLabels = { "Petri Net" },
		returnTypes = { Petrinet.class },
		help = "Discovers Object-Centric Local Process Models on an object-centric event log (OCEL standard)."
)
public class testSPECpp {

	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "OCLPM test SPECpp" // name shown in ProM
	)
	@PluginVariant(
			variantLabel = "OCLPM test", // this isn't shown anywhere?
			requiredParameterLabels = {0}
	)
	public static Petrinet specpptesting(UIPluginContext context, XLog log) {
		return FlatLogProcessing.discoverPetriNet(context, log);
	}
}
