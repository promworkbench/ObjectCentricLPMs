package org.processmining.OCLPMDiscovery.plugins.utils;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEventLog;

@Plugin(
		name = "OCEL helper methods from the OCLPMs package.", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"OCEL"},
		returnLabels = { "OCEL", "Flattened Log" },
		returnTypes = { OcelEventLog.class, XLog.class },
		help = "OCEL helper methods from the OCLPMs package."
)
public class OCELUtilsPlugin {

	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "OCEL add dummy case notion and flatten on it." // name shown in ProM
	)
	@PluginVariant(
			variantLabel = "OCEL add dummy case notion and flatten on it.",
			requiredParameterLabels = {0}
	)
	public static Object[] addDummyCaseNotion(PluginContext context, OcelEventLog ocel) {
		String dummyType = "DummyType";
		OcelEventLog newocel = OCELUtils.addDummyCaseNotion(ocel, dummyType, "DummyID");
		XLog flatLog = Flattening.flatten(newocel, dummyType);
		return new Object[] {newocel, flatLog};
	}
}
