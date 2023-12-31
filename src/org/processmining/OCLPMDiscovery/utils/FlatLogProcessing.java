package org.processmining.OCLPMDiscovery.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
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
	
	public static HashMap<String,String> computeCaseStatistics(XLog log) {
		int traces = log.size();
		
		// Calculating the average size
        double sum = 0;
        Iterator<XTrace> iterator = log.iterator();
        while (iterator.hasNext()) {
            sum += iterator.next().size();
        }
        double average = sum / log.size();
        
        // Calculating the median size
        List<Integer> sizes = new ArrayList<>();
        iterator = log.iterator();
        while (iterator.hasNext()) {
            sizes.add(iterator.next().size());
        }
        Collections.sort(sizes);
        double median;
        if (sizes.size() % 2 == 0) {
            median = (sizes.get(sizes.size() / 2) + sizes.get(sizes.size() / 2 - 1)) / 2.0;
        } else {
            median = sizes.get(sizes.size() / 2);
        }
        
        // Calculating min, max, first quartile, and third quartile
        double min = (double) Collections.min(sizes);
        double max = (double) Collections.max(sizes);

        int n = sizes.size();
        double firstQuartile = findFirstQuartile(sizes);
        double thirdQuartile = findThirdQuartile(sizes);
        
        HashMap<String,String> map = new HashMap<>();
        map.put("Cases",String.valueOf(traces));
        map.put("Avg",String.valueOf(average));
        map.put("Min",String.valueOf(min));
        map.put("1st Quartile",String.valueOf(firstQuartile));
        map.put("Median",String.valueOf(median));
        map.put("3rd Quartile",String.valueOf(thirdQuartile));
        map.put("Max",String.valueOf(max));
        
        System.out.println("Number of Cases = "+traces+", Events per Case: Average = "+average+", Median = "+median);
        System.out.println("Events per Case Statistics: Min="+min+", 1stQ="+firstQuartile+", Median="+median+", 3rdQ="+thirdQuartile+", Max="+max);
        
        return map;
	}
	
	public static double findFirstQuartile(List<Integer> sortedList) {
        int n = sortedList.size();
        if (n == 0) {
            return 0;
        } else if (n == 1) {
            return sortedList.get(0);
        } else if (n == 2) {
            return (sortedList.get(0) + sortedList.get(1)) / 2.0;
        } else {
            if (n % 2 != 0) {
                return sortedList.get(n / 4);
            } else {
                return (sortedList.get(n / 4 - 1) + sortedList.get(n / 4)) / 2.0;
            }
        }
    }
	
	public static double findThirdQuartile(List<Integer> sortedList) {
        int n = sortedList.size();
        if (n == 0) {
            return 0;
        } else if (n == 1) {
            return sortedList.get(0);
        } else if (n == 2) {
            return (sortedList.get(0) + sortedList.get(1)) / 2.0;
        } else {
            if (n % 2 != 0) {
                return sortedList.get((3 * n) / 4);
            } else {
                return (sortedList.get((3 * n) / 4 - 1) + sortedList.get((3 * n) / 4)) / 2.0;
            }
        }
    }
}
