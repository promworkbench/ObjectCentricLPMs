package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.Collection;
import java.util.HashSet;

import org.deckfour.uitopia.api.event.TaskListener;
import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.hybridilpminer.connections.XLogHybridILPMinerParametersConnection;
import org.processmining.hybridilpminer.dialogs.ConnectionsClassifierEngineAndDefaultConfigurationDialogImpl;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.hybridilpminer.plugins.HybridILPMinerPlugin;
import org.processmining.hybridilpminer.utils.XLogUtils;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.widgets.wizard.Dialog;
import org.processmining.widgets.wizard.Wizard;
import org.processmining.widgets.wizard.WizardResult;

public class ILPMiner {
//	public static Petrinet minePetrinet(XLog log, OCLPMDiscoveryParameters parameters) {
	public static Petrinet minePetrinet(PluginContext context, XLog log, OCLPMDiscoveryParameters parameters) {
		// clone ilp parameters because the log is fetched from here
		XLogHybridILPMinerParametersImpl ilpParameters = deepCopyILPParameters (parameters.getIlpParameters());
		
		// artificial start and end
		final String startLabel = "ArtificialStart";
		final String endLabel = "ArtificialEnd";
		XLog artifLog = XLogUtils.addArtificialStartAndEnd(log, startLabel, endLabel);
		ilpParameters.setLog(artifLog);
		
		Petrinet petrinet;
		
//		LPMiner miner = LPMinerFactory.createLPMiner(ilpParameters);
//		boolean removeRedundant = true; //TODO do or don't?
//		Object[] result = HybridILPMinerPlugin.discover(miner, artifLog, ilpParameters, startLabel, endLabel, removeRedundant);
		
		Object[] result = HybridILPMinerPlugin.discoverWithArtificialStartEnd(context, log, artifLog, ilpParameters);
		
		petrinet = (Petrinet) result[0];

		// TODO post processing (remove source, sink and silent transitions?)
		
		return petrinet;
	}
	
	public static XLogHybridILPMinerParametersImpl deepCopyILPParameters (XLogHybridILPMinerParametersImpl p1) {
		XLogHybridILPMinerParametersImpl p2 = new XLogHybridILPMinerParametersImpl(null);
		p2.setApplyStructuralRedundantPlaceRemoval(p1.isApplyStructuralRedundantPlaceRemoval());
		p2.setEventClassifier(p1.getEventClassifier());
		p2.setFindSink(p1.isFindSink());
		p2.setLPConstraintTypes(p1.getLPConstraintTypes());
		p2.setStaticConstraintTypes(p1.getStaticConstraintTypes());
		p2.setContext(p1.getContext());
		p2.setDiscoveryStrategy(p1.getDiscoveryStrategy());
		p2.setEngineType(p1.getEngine());
		p2.setFilter(p1.getFilter());
		p2.setILPOutputLocation(p1.getILPOutputLocation());
		p2.setNetClass(p1.getNetClass());
		p2.setObjectiveType(p1.getObjectiveType());
		p2.setIsSolve(p1.isSolve());
		p2.setVariableType(p1.getLPVaraibleType());
		return p2;		
	}
	
	public static XLogHybridILPMinerParametersImpl startILPWizard(UIPluginContext context, OCLPMDiscoveryParameters parameters, OcelEventLog ocel) {
		XLog log = Flattening.flatten(ocel, (String) parameters.getObjectTypesPlaceNets().toArray()[0]);
		return startILPWizard(context, parameters, log);
	}
	
	public static XLogHybridILPMinerParametersImpl startILPWizard(UIPluginContext context, OCLPMDiscoveryParameters parameters, XLog log) {
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
		XLogHybridILPMinerParametersImpl params;
		if (wizardResult.getInteractionResult().equals(TaskListener.InteractionResult.FINISHED)) {
			params = wizardResult.getParameters();
		}
		else {
			params = null;
		}
		return params;
	}
}
