package org.processmining.OCLPMDiscovery.plugins.mining;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.hybridilpminer.algorithms.miners.LPMiner;
import org.processmining.hybridilpminer.algorithms.miners.LPMinerFactory;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.hybridilpminer.plugins.HybridILPMinerPlugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public class ILPMiner {
	public static Petrinet minePetrinet(XLog log, OCLPMDiscoveryParameters parameters) {
		// clone ilp parameters because the log is fetched from here
		XLogHybridILPMinerParametersImpl ilpParameters = deepCopyILPParameters (parameters.getIlpParameters());
		ilpParameters.setLog(log);
		
		Petrinet petrinet;
		LPMiner miner = LPMinerFactory.createLPMiner(ilpParameters);
		boolean removeRedundant = true; //TODO do or don't?
		Object[] result = HybridILPMinerPlugin.discover(miner, log, ilpParameters, "ArtificialStart", "ArtificialEnd", removeRedundant);
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
}
