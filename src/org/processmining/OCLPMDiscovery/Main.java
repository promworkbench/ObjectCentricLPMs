package org.processmining.OCLPMDiscovery;

import java.util.ArrayList;
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
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.plugins.mining.PlaceBasedLPMDiscoveryPlugin;
import org.processmining.plugins.utils.ProvidedObjectHelper;

public class Main {
	private static PluginContext Context;
	private static UIPluginContext UIContext;
	private static boolean UsingUI = false;
	private static boolean UsingContext = false;
	private static Graph<String,DefaultEdge> graph;
	private static boolean graphProvided = false;
	
	
	
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
		Main.exportPlaceSet(placeSet);
		HashMap<String,String> typeMap = (HashMap<String,String>) results[1];
		Main.exportHashMap(typeMap);
		
		// LPM discovery
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet);
		Main.exportTlpms(tlpms);
		
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
		Main.exportTlpms(tlpms);
		
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
        Main.exportTlpms(tlpms);
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
		Main.exportPlaceSet(placeSet);
		Main.exportHashMap((HashMap<String,String>) result[1]);
		
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
			Main.updateProgress("Finished discovery of place nets for object type "+currentType+".");

			// unite place nets
			placeNetsUnion.addAll(placeNets);
			typeMap.putAll(newMap);
		}
		
		// convert set of places to PlaceSet
		PlaceSet placeSet = new PlaceSet(placeNetsUnion);
		
		return new Object[] {placeSet, typeMap};
	}

	public static LPMResultsTagged discoverLPMs(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet) {

		LPMResultsTagged lpmsTagged = new LPMResultsTagged();
		XLog log;
		Object[] lpmResults;
		LPMResult lpmResult;
		Graph<String,DefaultEdge> graph;
		ArrayList<String> newTypeLabels;
		
		switch (parameters.getCaseNotionStrategy()) {
		
			case PE_LEADING:
				newTypeLabels = new ArrayList<String>(parameters.getObjectTypesLeadingTypes().size());
				graph = buildObjectGraph(ocel);
				// LPM discovery for each new case notion
				for (String currentType : parameters.getObjectTypesLeadingTypes()) {
					messageNormal("Starting ocel enhancement using the leading type strategy for type "+currentType+".");
					
					// enhance log by process executions as case notions
					String newTypeLabel = "PE_"+currentType;
					newTypeLabels.add(newTypeLabel);
					ocel = ProcessExecutions.enhanceLeadingType(ocel, newTypeLabel, currentType, graph);				
					
					// flatten ocel
					log = Flattening.flatten(ocel, newTypeLabel);
				
					// discover LPMs (name of the currentType column needs concept:name, which the flattening does)
					System.out.println("Starting LPM discovery using leading type "+newTypeLabel+" as case notion.");
					lpmResults = runLPMPlugin(log, placeSet, parameters);
					assert(lpmResults[0] instanceof LPMResult);
					lpmResult = (LPMResult) lpmResults[0];
					lpmsTagged.put(lpmResult, currentType);
					
					updateProgress("Finished ocel enhancement using the leading type strategy for type "+currentType+".");
				}
				exportOcel(ocel, newTypeLabels);
				break;
				
			case PE_LEADING_RELAXED:
				newTypeLabels = new ArrayList<String>(parameters.getObjectTypesLeadingTypes().size());
				graph = buildObjectGraph(ocel);
				// LPM discovery for each new case notion
				for (String currentType : parameters.getObjectTypesLeadingTypes()) {
					messageNormal("Starting ocel enhancement using the leading type relaxed strategy for type "+currentType+".");
					
					// enhance log by process executions as case notions
					String newTypeLabel = "PE_"+currentType;
					newTypeLabels.add(newTypeLabel);
					ocel = ProcessExecutions.enhanceLeadingTypeRelaxed(ocel, newTypeLabel, currentType, graph);				
					
					// flatten ocel
					log = Flattening.flatten(ocel, newTypeLabel);
				
					// discover LPMs (name of the currentType column needs concept:name, which the flattening does)
					System.out.println("Starting LPM discovery using leading type relaxed "+newTypeLabel+" as case notion.");
					lpmResults = runLPMPlugin(log, placeSet, parameters);
					assert(lpmResults[0] instanceof LPMResult);
					lpmResult = (LPMResult) lpmResults[0];
					lpmsTagged.put(lpmResult, currentType);
					
					updateProgress("Finished ocel enhancement using the leading type relaxed strategy for type "+currentType+".");
				}
				exportOcel(ocel, newTypeLabels);
				break;
				
			case PE_CONNECTED:
				graph = buildObjectGraph(ocel);
				String ot = "ConnectedComponent";
				ocel = ProcessExecutions.enhanceConnectedComponent(ocel, ot, graph);
				messageNormal("Discovered "+ocel.objectTypes.get(ot).objects.size()+" connected components.");
				log = Flattening.flatten(ocel, ot);
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
				log = Flattening.flatten(dummyOcel,dummyType);
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
			exportObjectGraph(graph);
			updateProgress("Contructed object graph with "+graph.vertexSet().size()+" vertices and "+graph.edgeSet().size()+" edges.");
		}
		else {
			updateProgress("Object graph provided with "+graph.vertexSet().size()+" vertices and "+graph.edgeSet().size()+" edges.");
		}
		return graph;
	}
	
	public static void exportPlaceSet(PlaceSet placeSet) {
		// Add the places as a provided object
		if (UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: United Place Set"
	        		, placeSet, PlaceSet.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), placeSet);
		}
	}
	

	private static void exportHashMap(HashMap<String, String> typeMap) {
		if (UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: HashMap for Places to Object Types"
	        		, typeMap, HashMap.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), typeMap);
		}
	}
	
	private static void exportTlpms(LPMResultsTagged tlpms) {
		if (UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: LPMResults for all Case Notions"
	        		, tlpms, LPMResultsTagged.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), tlpms);
		}
	}
	
	private static void exportObjectGraph(Graph<String,DefaultEdge> graph) {
		if (UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: Object Graph"
	        		, graph, Graph.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), graph);
		}
	}
	
	private static void exportOcel(OcelEventLog ocel) { //TODO this doesn't work
		if (UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: OCEL"
	        		, ocel, OcelEventLog.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), ocel);
		}
	}
	
	private static void exportOcel(OcelEventLog ocel, ArrayList<String> labels) {
		exportOcel(ocel);
		if (UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: new Type Labels"
	        		, labels, ArrayList.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), labels);
		}
	}

	public static Graph<String,DefaultEdge> getGraph() {
		return graph;
	}

	public static void setGraph(Graph<String,DefaultEdge> graph) {
		Main.graph = graph;
		Main.graphProvided = true;
	}
}
