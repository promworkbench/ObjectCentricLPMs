package org.processmining.OCLPMDiscovery.plugins.mining;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
		name = "Discovery of Object-Centric Local Process Models", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"Log", "Set of Places", "Petri Net", "Parameters"},
		returnLabels = { "Hello world string" }, 
		returnTypes = { String.class },
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
}
