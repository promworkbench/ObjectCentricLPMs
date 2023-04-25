package org.processmining.OCLPMDiscovery.plugins.mining;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

public class FlatLogProcessing {
	
//	// TODO Discover Petri net, convert to place nets and tag them
//	public static Set<Place> processFlatLog (UIPluginContext context, XLog log, String objectType, OCLPMDiscoveryParameters parameters){
//		Petrinet petrinet = discoverPetriNet (context, log, parameters);
//		Set<Place> placeNets = convertPetriNetToPlaceNets (petrinet);
//		Set<Place> taggedPlaceNets = tagPlaceNets (placeNets, objectType);
//		return taggedPlaceNets;
//	}

	public static Petrinet discoverPetriNet (UIPluginContext context, XLog log, OCLPMDiscoveryParameters parameters) {
		Petrinet petrinet;
		switch(parameters.getPlaceDiscoveryAlgorithm()) {
			case ILP:
				petrinet = ILPMiner.minePetrinet(log, parameters);
				break;
			case SPECPP: //! not functional
				petrinet = SPECppMiner.minePetrinet(context, log);
				break;
			default:
				petrinet = ILPMiner.minePetrinet(log, parameters);
		}
		
		
		return petrinet;
	}
//	
//	public static Set<Place> convertPetriNetToPlaceNets (Petrinet petrinet){ //TODO
//		
//	}
//	
//	/* rename the places such that they start with the object type 
//	 * used as a case notion for the log
//	 */
//	public static Set<Place> tagPlaceNets (Set<Place> placeNets, String objectType){ //TODO
//		
//	}
}
