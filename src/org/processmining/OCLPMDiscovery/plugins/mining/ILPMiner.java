package org.processmining.OCLPMDiscovery.plugins.mining;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.hybridilpminer.algorithms.miners.LPMiner;
import org.processmining.hybridilpminer.algorithms.miners.LPMinerFactory;
import org.processmining.hybridilpminer.plugins.HybridILPMinerPlugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public class ILPMiner {
	public static Petrinet minePetrinet(XLog log, OCLPMDiscoveryParameters parameters) {
		Petrinet petrinet;
		LPMiner miner = LPMinerFactory.createLPMiner(parameters.getIlpParameters());
		boolean removeRedundant = true; //TODO do or don't?
		Object[] result = HybridILPMinerPlugin.discover(miner, log, parameters.getIlpParameters(), "ArtificialStart", "ArtificialEnd", removeRedundant);
		petrinet = (Petrinet) result[0];
		// TODO post processing (remove source, sink and silent transitions?)
		return petrinet;
	}
}
