package org.processmining.OCLPMDiscovery.utils;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.ocel.ocelobjects.OcelEventLog;

@Plugin(
		name = "OCEL helper methods from the OCLPMs package.", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"OCEL"},
		returnLabels = { "OCEL" },
		returnTypes = { OcelEventLog.class },
		help = "OCEL helper methods from the OCLPMs package."
)
public class OCELUtilsPlugin {

	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "OCEL add dummy case notion" // name shown in ProM
	)
	@PluginVariant(
			variantLabel = "OCEL add dummy case notion",
			requiredParameterLabels = {0}
	)
	public static OcelEventLog addDummyCaseNotion(PluginContext context, OcelEventLog ocel) {
		return OCELUtils.addDummyCaseNotion(ocel, "DummyType", "DummyID");
	}
}
