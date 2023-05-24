package org.processmining.OCLPMDiscovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.parameters.CaseNotionStrategy;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.utils.FlatLogProcessing;
import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.OCLPMDiscovery.utils.ProcessExecutions;
import org.processmining.OCLPMDiscovery.utils.ProvidingObjects;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.plugins.mining.PlaceBasedLPMDiscoveryPlugin;

public class Main {
	public static PluginContext Context;
	public static UIPluginContext UIContext;
	public static boolean UsingUI = false;
	public static boolean UsingContext = false;
	public static Graph<String,DefaultEdge> graph;
	public static boolean graphProvided = false;
	
	
	
	//===================================================================
	//		Variants
	//===================================================================
	/**
	 * Full OCLPMs discovery starting from ocel
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult, tlpms, placeSet, typeMap}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters) {
		
		// place discovery
		Object[] results = discoverPlaceSet(ocel,parameters);
		PlaceSet placeSet = (PlaceSet) results[0];
		ProvidingObjects.exportPlaceSet(placeSet);
		HashMap<String,String> typeMap = (HashMap<String,String>) results[1];
		ProvidingObjects.exportHashMap(typeMap);
		
		// LPM discovery
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet);
		ProvidingObjects.exportTlpms(tlpms);
		
		// OCLPM conversion
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms, typeMap);

        return new Object[] {oclpmResult, tlpms, placeSet, typeMap};
    }
	
	/**
	 * Full OCLPMs discovery starting from place nets
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult, tlpms}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet, HashMap<String,String> typeMap) {
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet);
		ProvidingObjects.exportTlpms(tlpms);
		
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms, typeMap);

        return new Object[] {oclpmResult, tlpms};
    }
	
	/**
	 * Full OCLPMs discovery starting from place nets with enhanced OCEL input
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult, tlpms}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet, HashMap<String,String> typeMap, ArrayList<String> labels) {
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet, labels);
		ProvidingObjects.exportTlpms(tlpms);
		
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms, typeMap);

        return new Object[] {oclpmResult, tlpms};
    }
	
	/**
	 * OCLPMs discovery starting from Tagged LPMs
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, HashMap<String,String> typeMap, LPMResultsTagged tlpms) {
        		
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms, typeMap);

        return new Object[] {oclpmResult};
    }
	
	/**
	 * OCLPMs discovery starting from LPMs of a single case notion
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult, tlpms}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, HashMap<String,String> typeMap, LPMResult lpms) {
        LPMResultsTagged tlpms = new LPMResultsTagged(lpms,"Single Case Notion");
        ProvidingObjects.exportTlpms(tlpms);
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms, typeMap);

        return new Object[] {oclpmResult, tlpms};
    }
	
	/**
	 * Discover only LPMs starting from ocel
	 * @param ocel
	 * @param parameters
	 * @return
	 */
	public static LPMResultsTagged runLPMDiscovery(OcelEventLog ocel, OCLPMDiscoveryParameters parameters) {
        
		Object[] result = discoverPlaceSet(ocel,parameters);
		PlaceSet placeSet = (PlaceSet) result[0];
		ProvidingObjects.exportPlaceSet(placeSet);
		ProvidingObjects.exportHashMap((HashMap<String,String>) result[1]);
		
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet);

        return tlpms;
    }
	
	/**
	 * Discover only LPMs starting with place set
	 * @param ocel
	 * @param parameters
	 * @param placeSet
	 * @return
	 */
	public static LPMResultsTagged runLPMDiscovery(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet) {
        	
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet);

        return tlpms;
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
			Main.messageNormal("Starting flattening and place net discovery for type "+currentType+".");
			// flatten ocel
			XLog flatLog = Main.flattenOCEL(ocel, currentType);
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
			Main.updateProgress("Finished discovery of place nets for object type "+currentType+".");

			// unite place nets
			placeNetsUnion.addAll(placeNets);
			typeMap.putAll(newMap);
		}
		
		// convert set of places to PlaceSet
		PlaceSet placeSet = new PlaceSet(placeNetsUnion);
		
		return new Object[] {placeSet, typeMap};
	}
	
	/**
	 * LPM discovery with given case notions in the OCEL
	 * @param ocel
	 * @param parameters
	 * @param placeSet
	 * @param labels
	 * @return
	 */
	public static LPMResultsTagged discoverLPMs(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet, ArrayList<String> labels) {
		LPMResultsTagged lpmsTagged = new LPMResultsTagged();
		XLog log;
		Object[] lpmResults;
		LPMResult lpmResult;
		for (String currentType : labels) {
			// flatten ocel
			log = Main.flattenOCEL(ocel, currentType);
		
			// discover LPMs (name of the currentType column needs concept:name, which the flattening does)
			System.out.println("Starting LPM discovery using \""+currentType+"\" as case notion.");
			lpmResults = runLPMPlugin(log, placeSet, parameters);
			assert(lpmResults[0] instanceof LPMResult);
			lpmResult = (LPMResult) lpmResults[0];
			lpmsTagged.put(lpmResult, currentType);
		}
		System.out.println("Finished LPM discovery.");
		System.out.println("LPMResult stores "+lpmsTagged.totalLPMs()+" LPMs.");
		return lpmsTagged;
	}

	/**
	 * LPM discovery which first creates case notions to perform the LPM discovery on.
	 * @param ocel
	 * @param parameters
	 * @param placeSet
	 * @return
	 */
	public static LPMResultsTagged discoverLPMs(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet) {

		LPMResultsTagged lpmsTagged = new LPMResultsTagged();
		XLog log;
		Object[] lpmResults;
		LPMResult lpmResult;
		Graph<String,DefaultEdge> graph;
		ArrayList<String> newTypeLabels;
		
		switch (parameters.getCaseNotionStrategy()) {
				
			case PE_LEADING:
			case PE_LEADING_RELAXED:
			case PE_LEADING_O1:
			case PE_LEADING_RELAXED_O1:
				newTypeLabels = new ArrayList<String>(parameters.getObjectTypesLeadingTypes().size());
				graph = buildObjectGraph(ocel);
				// LPM discovery for each new case notion
				for (String currentType : parameters.getObjectTypesLeadingTypes()) {
					messageNormal("Starting ocel enhancement using the \""+parameters.getCaseNotionStrategy().getName()+"\" strategy for type \""+currentType+"\".");
					
					// enhance log by process executions as case notions
					String newTypeLabel = "PE_"+currentType;
					newTypeLabels.add(newTypeLabel);
					switch (parameters.getCaseNotionStrategy()) {
						case PE_LEADING:
							ocel = ProcessExecutions.enhanceLeadingType(ocel, newTypeLabel, currentType, graph);
							break;
						case PE_LEADING_RELAXED:
							ocel = ProcessExecutions.enhanceLeadingTypeRelaxed(ocel, newTypeLabel, currentType, graph);
							break;
						case PE_LEADING_O1:
							ocel = ProcessExecutions.enhanceLeadingTypeOptimized1(ocel, newTypeLabel, currentType, graph);
							break;
						case PE_LEADING_RELAXED_O1:
							ocel = ProcessExecutions.enhanceLeadingTypeRelaxedOptimized1(ocel, newTypeLabel, currentType, graph);
							break;
						default:
							ocel = ProcessExecutions.enhanceLeadingTypeOptimized1(ocel, newTypeLabel, currentType, graph);
							break;
					}
					
					// flatten ocel
					log = Main.flattenOCEL(ocel, newTypeLabel);
				
					// discover LPMs (name of the currentType column needs concept:name, which the flattening does)
					System.out.println("Starting LPM discovery using leading type "+newTypeLabel+" as case notion.");
					lpmResults = runLPMPlugin(log, placeSet, parameters);
					assert(lpmResults[0] instanceof LPMResult);
					lpmResult = (LPMResult) lpmResults[0];
					lpmsTagged.put(lpmResult, currentType);
					
					updateProgress("Finished ocel enhancement using the \""+parameters.getCaseNotionStrategy().getName()+"\" strategy for type \""+currentType+"\".");
				}
				ProvidingObjects.exportOcel(ocel, newTypeLabels);
				break;
				
			case PE_CONNECTED:
				graph = buildObjectGraph(ocel);
				String ot = "ConnectedComponent";
				ocel = ProcessExecutions.enhanceConnectedComponent(ocel, ot, graph);
				ProvidingObjects.exportOcel(ocel, new ArrayList<String>(Arrays.asList(ot)));
				messageNormal("Discovered "+ocel.objectTypes.get(ot).objects.size()+" connected components.");
				log = Main.flattenOCEL(ocel, ot);
				System.out.println("Starting LPM discovery using connected components as case notion.");
				lpmResults = runLPMPlugin(log, placeSet, parameters);
				assert(lpmResults[0] instanceof LPMResult);
				lpmResult = (LPMResult) lpmResults[0];
				lpmsTagged.put(lpmResult, ot);
				break;
				
			case DUMMY:
			default:
				String dummyType = "DummyType";
				OcelEventLog dummyOcel = OCELUtils.addDummyCaseNotion(ocel,"DummyType","42");
				System.out.println("Added dummy type to ocel.");
				log = Main.flattenOCEL(dummyOcel,dummyType);
				System.out.println("Flattened on dummy type.");
				System.out.println("Starting LPM discovery using a dummy type as case notion.");
				lpmResults = runLPMPlugin(log, placeSet, parameters);
				assert(lpmResults[0] instanceof LPMResult);
				lpmResult = (LPMResult) lpmResults[0];
				lpmsTagged.put(lpmResult, dummyType);
		}
		System.out.println("Finished LPM discovery.");
		System.out.println("LPMResult stores "+lpmsTagged.totalLPMs()+" LPMs.");
		
		return lpmsTagged;
	}
	
	public static OCLPMResult convertLPMstoOCLPMs (OCLPMDiscoveryParameters parameters, LPMResultsTagged tlpms, HashMap<String,String> typeMap) {

		OCLPMResult oclpmResult = new OCLPMResult(parameters, tlpms, typeMap);
		
		// TODO identify variable arcs
		
		return oclpmResult;
	}
	
	//===================================================================
	//		helpers / setup
	//===================================================================
	
	public static void setUp(PluginContext context) {
        Main.Context = context;
        if (context != null) {
        	Main.UsingContext = true;
        }
    }
	
	public static void setUp(PluginContext context, int min, int max) {
        Main.setUp(context);
        if (context instanceof UIPluginContext) {
        	Main.UIContext = (UIPluginContext) Main.Context;
        	Main.UsingUI = true;
        	Main.UIContext.getProgress().setMinimum(min);
        	Main.UIContext.getProgress().setMaximum(max);
        }
    }
	
	/**
	 * Calculates the steps shown in the UI progress indicator.
	 * @param context
	 * @param parameters
	 * @param placeDiscovery set true if discovery of place nets is performed
	 * @param lpmDiscovery set true if LPM discovery is performed
	 */
	public static void setUp (PluginContext context, OCLPMDiscoveryParameters parameters, boolean placeDiscovery, boolean lpmDiscovery) {
		int numSteps = 0;
		// progress indicators for: 
		// + each completed place discovery for each selected object type
		if (placeDiscovery) {
			numSteps += parameters.getObjectTypesPlaceNets().size();			
		}
		// + each completed LPM discovery for each selected case notion
		if (lpmDiscovery) {
			// +1 if object graph needs to be built
			if (CaseNotionStrategy.objectGraphNeeded.contains(parameters.getCaseNotionStrategy())) {
				numSteps +=1;
			}
			if (CaseNotionStrategy.typeSelectionNeeded.contains(parameters.getCaseNotionStrategy())) {
				// enhance ocel with case notion
				numSteps += parameters.getObjectTypesLeadingTypes().size();
				// lpm discovery
				numSteps += parameters.getObjectTypesLeadingTypes().size();
			}
			else {
				numSteps += 1;
			}
		}
		Main.setUp(context, 0, numSteps);
	}
	
	public static PluginContext getContext() {
        return Context;
    }
	
	public static void messageNormal(String message) {
		System.out.println(message);
		if (UsingContext) {
			Context.log(message, MessageLevel.NORMAL);
		}
	}
	
	public static void updateProgress() {
		if (UsingUI) {
			UIContext.getProgress().inc();
		}
	}
	
	public static void updateProgress(String message) {
		Main.messageNormal(message);
		if (UsingUI) {
			UIContext.getProgress().inc();
		}
	}
	
	public static Object[] runLPMPlugin(XLog log, PlaceSet placeSet, OCLPMDiscoveryParameters parameters) {
		messageNormal("Starting LPM discovery.");
		//TODO what happens if Context==null?
		Object[] lpmResults = PlaceBasedLPMDiscoveryPlugin.mineLPMs(Context, log, placeSet, parameters.getPBLPMDiscoveryParameters());
		updateProgress("Finished LPM discovery.");
		return lpmResults;
	}
	
	public static Graph<String,DefaultEdge> buildObjectGraph(OcelEventLog ocel){
		if (!graphProvided) {
			messageNormal("Starting object graph construction.");
			Main.graph = ProcessExecutions.buildObjectGraph(ocel);
			ProvidingObjects.exportObjectGraph(graph);
			updateProgress("Contructed object graph with "+graph.vertexSet().size()+" vertices and "+graph.edgeSet().size()+" edges.");
		}
		else {
			updateProgress("Object graph provided with "+graph.vertexSet().size()+" vertices and "+graph.edgeSet().size()+" edges.");
		}
		return graph;
	}

	public static Graph<String,DefaultEdge> getGraph() {
		return graph;
	}

	public static void setGraph(Graph<String,DefaultEdge> graph) {
		Main.graph = graph;
		Main.graphProvided = true;
	}
	
	public static XLog flattenOCEL(OcelEventLog ocel, String newTypeLabel) {
		XLog log = Flattening.flatten(ocel, newTypeLabel);
//		int numEventsBefore = ocel.getEvents().size();
//		int numEventsAfter = log.size();
//		System.out.println("Flattening log for the new type \""+newTypeLabel+"\" lead to an increase of events by a factor of "+(double) numEventsAfter/numEventsBefore+".");
		return log;
	}
}
