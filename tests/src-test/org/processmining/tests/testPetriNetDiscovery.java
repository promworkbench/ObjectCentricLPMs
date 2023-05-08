package org.processmining.tests;

import java.util.Collection;
import java.util.HashSet;

import org.deckfour.uitopia.api.event.TaskListener;
import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.parameters.Miner;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.plugins.mining.ILPMiner;
import org.processmining.OCLPMDiscovery.utils.FlatLogProcessing;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.hybridilpminer.connections.XLogHybridILPMinerParametersConnection;
import org.processmining.hybridilpminer.dialogs.ConnectionsClassifierEngineAndDefaultConfigurationDialogImpl;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.hybridilpminer.plugins.HybridILPMinerPlugin;
import org.processmining.hybridilpminer.utils.XLogUtils;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.widgets.wizard.Dialog;
import org.processmining.widgets.wizard.Wizard;
import org.processmining.widgets.wizard.WizardResult;

@Plugin(
		name = "Testing Petri Net Miners for OCLPMs", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"Log", "Set of Places", "Petri Net", "Parameters", "Object Type"},
		returnLabels = { "Petri Net" },
		returnTypes = { Petrinet.class },
		help = "Testing Miners which can be used for discovering palce nets for the OCLPM discovery."
)
public class testPetriNetDiscovery {

	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "OCLPM test ILP" // name shown in ProM
	)
	@PluginVariant(
			variantLabel = "OCLPM test", // this isn't shown anywhere?
			requiredParameterLabels = {0}
	)
	public static Petrinet discoverPetriNet(UIPluginContext context, XLog log) {
		
		Miner placeDiscoveryAlgorithm = Miner.ILP;
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(null);
		parameters.setPlaceDiscoveryAlgorithm(placeDiscoveryAlgorithm);
		
		switch (parameters.getPlaceDiscoveryAlgorithm()) {
			case ILP:
				XLogHybridILPMinerParametersImpl ilpParameters = ILPMiner.startILPWizard(context, parameters, log);
				parameters.setIlpParameters(ilpParameters);
				break;
			default:
		}
		
		
		return FlatLogProcessing.discoverPetriNet(context, log, parameters);
	}
	
//	@UITopiaVariant(
//			affiliation = "RWTH - PADS",
//			author = "Marvin Porsil",
//			email = "marvin.porsil@rwth-aachen.de",
//			uiLabel = "OCLPM test ILP" // name shown in ProM
//	)
//	@PluginVariant(
//			variantLabel = "OCLPM test", // this isn't shown anywhere?
//			requiredParameterLabels = {0}
//	)
	public static Petrinet testILP(UIPluginContext context, XLog log) {

		// ilp wizard
		Collection<XLogHybridILPMinerParametersConnection> connections = new HashSet<>();
		try {
			connections = context.getConnectionManager().getConnections(XLogHybridILPMinerParametersConnection.class,
					context, log);
		} catch (ConnectionCannotBeObtained e) {
		}
		final String startLabel = "[start>@" + System.currentTimeMillis();
		final String endLabel = "[end]@" + System.currentTimeMillis();
		XLog artifLog = XLogUtils.addArtificialStartAndEnd(log, startLabel, endLabel);
		Dialog<XLogHybridILPMinerParametersImpl> firstDialog = new ConnectionsClassifierEngineAndDefaultConfigurationDialogImpl(
				context, null, artifLog, connections);
		WizardResult<XLogHybridILPMinerParametersImpl> wizardResult = Wizard.show(context, firstDialog);
		XLogHybridILPMinerParametersImpl ilpParameters;
		if (wizardResult.getInteractionResult().equals(TaskListener.InteractionResult.FINISHED)) {
			ilpParameters = wizardResult.getParameters();
		}
		else {
			ilpParameters = null;
		}
		
		// disover net
		// clone ilp parameters because the log is fetched from here
//		XLogHybridILPMinerParametersImpl ilpParameters = deepCopyILPParameters (parameters.getIlpParameters());
		ilpParameters.setLog(artifLog); //! needs to be the artificial log!
		
		// artificial start and end
//		final String startLabel = "ArtificialStart";
//		final String endLabel = "ArtificialEnd";
//		XLog artifLog = XLogUtils.addArtificialStartAndEnd(log, startLabel, endLabel);
		
		Petrinet petrinet;
		
//		LPMiner miner = LPMinerFactory.createLPMiner(ilpParameters);
//		boolean removeRedundant = true; //TODO do or don't?
//		Object[] result = HybridILPMinerPlugin.discover(miner, artifLog, ilpParameters, startLabel, endLabel, removeRedundant);
		
		Object[] result = HybridILPMinerPlugin.discoverWithArtificialStartEnd(context, log, artifLog, ilpParameters);
		
		petrinet = (Petrinet) result[0];
		
		return petrinet;
	}
}
