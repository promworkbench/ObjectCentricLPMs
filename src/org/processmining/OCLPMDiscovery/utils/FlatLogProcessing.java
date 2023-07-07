package org.processmining.OCLPMDiscovery.utils;

import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.converters.PetriNetTaggedPlaceConverter;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.plugins.mining.ILPMiner;
import org.processmining.OCLPMDiscovery.plugins.mining.SPECppMiner;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetImpl;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.placebasedlpmdiscovery.model.Place;

public class FlatLogProcessing {
	
	/**
	 *  Discover Petri net, convert to place nets and tag them
	 * @param context
	 * @param log
	 * @param objectType
	 * @param parameters
	 * @return {Set<Place>, HashMap<String,String>}
	 */
//	public static Object[] processFlatLog (PluginContext context, XLog log, String objectType, OCLPMDiscoveryParameters parameters){
//		Petrinet petrinet = discoverPetriNet (context, log, parameters);
//		Object[] results = convertPetriNetToPlaceNetsWithMap (context, petrinet, objectType);
//		return results;
//	}
	
	public static Set<TaggedPlace> processFlatLog (PluginContext context, XLog log, String objectType, OCLPMDiscoveryParameters parameters){
		Petrinet petrinet = discoverPetriNet (context, log, parameters);
		Set<TaggedPlace> result = convertPetriNetToTaggedPlaceNets (context, petrinet, objectType);
		return result;
	}

	public static Petrinet discoverPetriNet (PluginContext context, XLog log, OCLPMDiscoveryParameters parameters) {
		Petrinet petrinet;
		switch(parameters.getPlaceDiscoveryAlgorithm()) {
			case ILP:
				petrinet = ILPMiner.minePetrinet(context, log, parameters.getIlpParameters());
				break;
			case SPECPP:
				petrinet = SPECppMiner.minePetrinet(log, parameters.getSpecppParameters());
				break;
			default:
				petrinet = ILPMiner.minePetrinet(context, log, parameters.getIlpParameters());
		}
		
		
		return petrinet;
	}
	
	/**
	 *  tags place nets by saving a map <Place.id,tag>
	 * @param context
	 * @param petrinet
	 * @param objectType
	 * @return {Set<Place>, HashMap<String,String>}
	 */
	public static Object[] convertPetriNetToPlaceNetsWithMap (PluginContext context, Petrinet petrinet, String objectType){
		AcceptingPetriNet acceptingPetriNet = new AcceptingPetriNetImpl(petrinet);
		PetriNetTaggedPlaceConverter converter = new PetriNetTaggedPlaceConverter(objectType);
		return converter.convertWithMap(acceptingPetriNet);
	}
	
	/**
	 *  tags place nets but casts from TaggedPlace to Place
	 * @param context
	 * @param petrinet
	 * @param objectType
	 * @return
	 */
	public static Set<Place> convertPetriNetToPlaceNets (PluginContext context, Petrinet petrinet, String objectType){
		AcceptingPetriNet acceptingPetriNet = new AcceptingPetriNetImpl(petrinet);
		PetriNetTaggedPlaceConverter converter = new PetriNetTaggedPlaceConverter(objectType);
		return converter.convertCasted(acceptingPetriNet);
	}
	
	public static Set<TaggedPlace> convertPetriNetToTaggedPlaceNets (PluginContext context, Petrinet petrinet, String objectType){
		AcceptingPetriNet acceptingPetriNet = new AcceptingPetriNetImpl(petrinet);
		PetriNetTaggedPlaceConverter converter = new PetriNetTaggedPlaceConverter(objectType);
		return converter.convertToTagged(acceptingPetriNet);
	}
}
