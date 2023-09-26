package org.processmining.OCLPMDiscovery.utils;

import java.util.HashSet;
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
	
	public static HashSet<TaggedPlace> processFlatLog (PluginContext context, XLog log, String objectType, OCLPMDiscoveryParameters parameters){
		Petrinet petrinet = discoverPetriNet (context, log, parameters);
		HashSet<TaggedPlace> result = convertPetriNetToTaggedPlaceNets (context, petrinet, objectType);
		return result;
	}
	
	/**
	 * Discover Petri net, convert to tagged places but then cast to places
	 * @param context
	 * @param log
	 * @param objectType
	 * @param parameters
	 * @return
	 */
	public static Set<Place> processFlatLogHiddenTagging (PluginContext context, XLog log, String objectType, OCLPMDiscoveryParameters parameters){
		Petrinet petrinet = discoverPetriNet (context, log, parameters);
		Set<Place> result = convertPetriNetToPlaceNetsHiddenTagging (context, petrinet, objectType);
		return result;
	}
	
	public static Set<Place> processFlatLogNoTagging (PluginContext context, XLog log, String objectType, OCLPMDiscoveryParameters parameters){
		Petrinet petrinet = discoverPetriNet (context, log, parameters);
		Set<Place> result = convertPetriNetToPlaceNetsNoTagging (context, petrinet, objectType);
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
	public static Set<Place> convertPetriNetToPlaceNetsHiddenTagging (PluginContext context, Petrinet petrinet, String objectType){
		AcceptingPetriNet acceptingPetriNet = new AcceptingPetriNetImpl(petrinet);
		PetriNetTaggedPlaceConverter converter = new PetriNetTaggedPlaceConverter(objectType);
		return converter.convertCasted(acceptingPetriNet);
	}
	
	public static HashSet<TaggedPlace> convertPetriNetToTaggedPlaceNets (PluginContext context, Petrinet petrinet, String objectType){
		AcceptingPetriNet acceptingPetriNet = new AcceptingPetriNetImpl(petrinet);
		PetriNetTaggedPlaceConverter converter = new PetriNetTaggedPlaceConverter(objectType);
		return converter.convertToTagged(acceptingPetriNet);
	}
	
	public static Set<Place> convertPetriNetToPlaceNetsNoTagging (PluginContext context, Petrinet petrinet, String objectType){
		AcceptingPetriNet acceptingPetriNet = new AcceptingPetriNetImpl(petrinet);
		PetriNetTaggedPlaceConverter converter = new PetriNetTaggedPlaceConverter(objectType);
		return converter.convertNoTagging(acceptingPetriNet);
	}
}
