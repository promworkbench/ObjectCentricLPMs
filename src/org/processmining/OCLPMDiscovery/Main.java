package org.processmining.OCLPMDiscovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.utils.FlatLogProcessing;
import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.plugins.mining.PlaceBasedLPMDiscoveryPlugin;

public class Main {
	private static PluginContext Context;
	
	public static void setUp(PluginContext context) {
        Main.Context = context;
    }
	
	public static PluginContext getContext() {
        return Context;
    }
	
	//===================================================================
	//		Variants
	//===================================================================
	/**
	 * Full OCLPMs discovery starting from ocel
	 * @param ocel
	 * @param parameters
	 * @return
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters) {
        //TODO print out progress in ProM and show progression bar
		Object[] results = discoverPlaceSet(ocel,parameters);
		PlaceSet placeSet = (PlaceSet) results[0];
		HashMap<String,String> typeMap = (HashMap<String,String>) results[1];
		
		LPMResult lpmResult = discoverLPMs(ocel, parameters, placeSet);
		
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, lpmResult, typeMap);

        return new Object[] {oclpmResult};
    }
	
	/**
	 * Full OCLPMs discovery starting from place nets
	 * @param ocel
	 * @param parameters
	 * @return
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet, HashMap<String,String> typeMap) {
        LPMResult lpmResult = discoverLPMs(ocel, parameters, placeSet);
		
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, lpmResult, typeMap);

        return new Object[] {oclpmResult};
    }
	
	/**
	 * Discover only LPMs starting from ocel
	 * @param ocel
	 * @param parameters
	 * @return
	 */
	public static LPMResult runLPMDiscovery(OcelEventLog ocel, OCLPMDiscoveryParameters parameters) {
        
		PlaceSet placeSet = (PlaceSet) discoverPlaceSet(ocel,parameters)[0];
		
		LPMResult lpmResult = discoverLPMs(ocel, parameters, placeSet);

        return lpmResult;
    }
	
	/**
	 * Discover only LPMs starting with place set
	 * @param ocel
	 * @param parameters
	 * @param placeSet
	 * @return
	 */
	public static LPMResult runLPMDiscovery(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet) {
        	
		LPMResult lpmResult = discoverLPMs(ocel, parameters, placeSet);

        return lpmResult;
    }
	
	//===================================================================
	//		logic
	//===================================================================
	/**
	 * Returns a PlaceSet and a map which maps each Place.id to the ObjectType of the flat log on which it has been discovered
	 * @param ocel
	 * @param parameters
	 * @return {PlaceSet, HashMap<String,String>}
	 */
	public static Object[] discoverPlaceSet(OcelEventLog ocel, OCLPMDiscoveryParameters parameters) {
		Set<Place> placeNetsUnion = new HashSet<>();
		HashMap<String,String> typeMap = new HashMap<String,String>();
		
		// place net discovery
		for (String currentType : parameters.getObjectTypesPlaceNets()) {
			// flatten ocel
			XLog flatLog = Flattening.flatten(ocel, currentType);
			System.out.println("Flattened ocel for type "+currentType);
			
			// discover petri net using est-miner (use specpp)
			// split petri net into place nets
			// tag places with current object type
			Object[] results = FlatLogProcessing.processFlatLog(Context, flatLog, currentType, parameters); //TODO what happens if Context==null?
			assert(results[0] instanceof Set);
			Set<Place> placeNets = (Set<Place>) results[0];
			assert(results[1] instanceof HashMap);
			HashMap<String,String> newMap = (HashMap<String,String>) results[1];
			System.out.println("Finished discovery of place nets for object type "+currentType);

			// unite place nets
			placeNetsUnion.addAll(placeNets);
			typeMap.putAll(newMap);
		}
		
		// convert set of places to PlaceSet
		PlaceSet placeSet = new PlaceSet(placeNetsUnion);
		
		return new Object[] {placeSet, typeMap};
	}
	
	public static LPMResult discoverLPMs(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet) {
		// initialize LPMResult
		LPMResult lpmResult = new LPMResult();
		
		switch (parameters.getCaseNotionStrategy()) {
		
			case PE_LEADING:
				// TODO enhance log by process executions as case notions
				// LPM discovery for each new case notion
				for (String currentType : parameters.getObjectTypesLeadingTypes()) {
				
					// TODO flatten ocel
				
					// TODO discover LPMs (name of the currentType column needs concept:name, which the flattening does)
				
				}
				break;
				
			case PE_CONNECTED:
				break;
				
			case DUMMY:
			default:
				String dummyType = "DummyType";
				OcelEventLog dummyOcel = OCELUtils.addDummyCaseNotion(ocel,"DummyType","42");
				System.out.println("Added dummy type to ocel.");
				XLog log = Flattening.flatten(dummyOcel,dummyType);
				System.out.println("Flattened on dummy type.");
				System.out.println("Starting LPM discovery using a dummy type as case notion.");
				Object[] lpmResults = PlaceBasedLPMDiscoveryPlugin.mineLPMs(Context, log, placeSet, parameters.getPBLPMDiscoveryParameters()); //TODO what happens if Context==null?
				assert(lpmResults[0] instanceof LPMResult);
				lpmResult = (LPMResult) lpmResults[0];
		}
		System.out.println("Finished LPM discovery.");
		System.out.println("LPMResult stores "+lpmResult.size()+" LPMs.");
		
		return lpmResult;
	}
	
	public static OCLPMResult convertLPMstoOCLPMs (OCLPMDiscoveryParameters parameters, LPMResult lpmResult, HashMap<String,String> typeMap) {
		//TODO make OCLPMResult object
		OCLPMResult oclpmResult = new OCLPMResult(parameters, lpmResult, typeMap);
		
		// TODO assign places to objects
		
		// TODO identify variable arcs
		
		return oclpmResult;
	}
}
