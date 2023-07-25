package org.processmining.OCLPMDiscovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.parameters.CaseNotionStrategy;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.parameters.PlaceCompletion;
import org.processmining.OCLPMDiscovery.parameters.VariableArcIdentification;
import org.processmining.OCLPMDiscovery.utils.FlatLogProcessing;
import org.processmining.OCLPMDiscovery.utils.OCELUtils;
import org.processmining.OCLPMDiscovery.utils.PlaceCompletionUtils;
import org.processmining.OCLPMDiscovery.utils.ProcessExecutions;
import org.processmining.OCLPMDiscovery.utils.ProvidingObjects;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.ocel.flattening.Flattening;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.Transition;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableList;
import org.processmining.placebasedlpmdiscovery.plugins.mining.PlaceBasedLPMDiscoveryPlugin;

public class Main {
	public static PluginContext Context;
	public static UIPluginContext UIContext;
	public static boolean UsingUI = false;
	public static boolean UsingContext = false;
	public static Graph<String,DefaultEdge> graph;
	public static boolean graphProvided = false;
	public static long startTime = System.currentTimeMillis();
	
	
	//===================================================================
	//		Variants
	//===================================================================
	/**
	 * Full OCLPM discovery starting from ocel
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult, tlpms, placeSet}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters) {
		
		// place discovery
		PlaceSet placeSet = discoverPlaceSet(ocel,parameters);
		ProvidingObjects.exportPlaceSet(placeSet);
		
		// identify variable arcs
		placeSet = Main.identifyVariableArcs(parameters, placeSet);
		
		// LPM discovery
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet);
		ProvidingObjects.exportTlpms(tlpms);
		
		// OCLPM conversion
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms);
		
		// OCLPM place completion
		oclpmResult = PlaceCompletionUtils.completePlaces(parameters, oclpmResult, placeSet);
		
		// reevaluation of OCLPMs
		oclpmResult = Main.evaluateOCLPMs(parameters, oclpmResult);
		
		// post processing
		oclpmResult = Main.postProcessing(parameters, oclpmResult);
		
		Main.printExecutionTime();
        return new Object[] {oclpmResult, tlpms, placeSet};
    }
	
	/**
	 * Full OCLPM discovery starting from place nets
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult, tlpms}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet) {
		
		// remove duplicate places from placeSet
		if (parameters.doPlaceSetPostProcessing()) {
			placeSet = Main.postProcessPlaceSet(placeSet);
			ProvidingObjects.exportPlaceSet(placeSet);
		}
		
		// identify variable arcs
		placeSet = Main.identifyVariableArcs(parameters, placeSet);
		
		// discover LPMs
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet);
		ProvidingObjects.exportTlpms(tlpms);
		
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms);
		
		// OCLPM place completion
		oclpmResult = PlaceCompletionUtils.completePlaces(parameters, oclpmResult, placeSet);
		
		// reevaluation of OCLPMs
		oclpmResult = Main.evaluateOCLPMs(parameters, oclpmResult);
		
		// post processing
		oclpmResult = Main.postProcessing(parameters, oclpmResult);

		Main.printExecutionTime();
        return new Object[] {oclpmResult, tlpms};
    }
	
	/**
	 * Full OCLPM discovery starting from place nets with enhanced OCEL input
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult, tlpms}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, PlaceSet placeSet, ArrayList<String> labels) {
		// remove duplicate places from placeSet
		if (parameters.doPlaceSetPostProcessing()) {
			placeSet = Main.postProcessPlaceSet(placeSet);
			ProvidingObjects.exportPlaceSet(placeSet);
		}
		
		// identify variable arcs
		placeSet = Main.identifyVariableArcs(parameters, placeSet);
		
		// discover LPMs
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet, labels);
		ProvidingObjects.exportTlpms(tlpms);
		
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms);
		
		// OCLPM place completion
		oclpmResult = PlaceCompletionUtils.completePlaces(parameters, oclpmResult, placeSet);
		
		// reevaluation of OCLPMs
		oclpmResult = Main.evaluateOCLPMs(parameters, oclpmResult);
		
		// post processing
		oclpmResult = Main.postProcessing(parameters, oclpmResult);

		Main.printExecutionTime();
        return new Object[] {oclpmResult, tlpms};
    }
	
	/**
	 * OCLPM discovery starting from Tagged LPMs
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, LPMResultsTagged tlpms, PlaceSet placeSet) {
        		
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms);
		
		// identify variable arcs
		oclpmResult = Main.identifyVariableArcs(parameters, oclpmResult);
		placeSet = Main.identifyVariableArcs(parameters, placeSet);
		
		// OCLPM place completion
		oclpmResult = PlaceCompletionUtils.completePlaces(parameters, oclpmResult, placeSet);
		
		// reevaluation of OCLPMs
		oclpmResult = Main.evaluateOCLPMs(parameters, oclpmResult);
		
		// post processing
		oclpmResult = Main.postProcessing(parameters, oclpmResult);

		Main.printExecutionTime();
        return new Object[] {oclpmResult};
    }
	
	/**
	 * OCLPMs discovery starting from LPMs of a single case notion
	 * @param ocel
	 * @param parameters
	 * @return {oclpmResult, tlpms}
	 */
	public static Object[] run(OcelEventLog ocel, OCLPMDiscoveryParameters parameters, LPMResult lpms) {
        LPMResultsTagged tlpms = new LPMResultsTagged(lpms,"Single Case Notion");
        ProvidingObjects.exportTlpms(tlpms);
		OCLPMResult oclpmResult = convertLPMstoOCLPMs(parameters, tlpms);
		
		// OCLPM place completion not possible as the place nets aren't available.
		
		// identify variable arcs
		oclpmResult = Main.identifyVariableArcs(parameters, oclpmResult);
		
		// reevaluation of OCLPMs
		oclpmResult = Main.evaluateOCLPMs(parameters, oclpmResult);
		
		// post processing
		oclpmResult = Main.postProcessing(parameters, oclpmResult);

		Main.printExecutionTime();
        return new Object[] {oclpmResult, tlpms};
    }
	
	/**
	 * Discover only LPMs starting from ocel
	 * @param ocel
	 * @param parameters
	 * @return
	 */
	public static LPMResultsTagged runLPMDiscovery(OcelEventLog ocel, OCLPMDiscoveryParameters parameters) {
        
		PlaceSet placeSet = discoverPlaceSet(ocel,parameters);
		ProvidingObjects.exportPlaceSet(placeSet);
		
		LPMResultsTagged tlpms = discoverLPMs(ocel, parameters, placeSet);

		Main.printExecutionTime();
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

		Main.printExecutionTime();
        return tlpms;
    }
	
	//===================================================================
	//		logic
	//===================================================================
	/**
	 * Returns a PlaceSet and a map which maps each Place.id to the ObjectType of the flat log on which it has been discovered
	 * @param ocel
	 * @param parameters
	 * @return PlaceSet
	 */
	public static PlaceSet discoverPlaceSet(OcelEventLog ocel, OCLPMDiscoveryParameters parameters) {
		Set<Place> placeNetsUnion = new HashSet<>();
		HashMap<String,String> typeMap = new HashMap<String,String>();
		
		// place net discovery
		for (String currentType : parameters.getObjectTypesPlaceNets()) {
			Main.messageNormal("Starting flattening and place net discovery for type "+currentType+".");
			// flatten ocel
			XLog flatLog = Main.flattenOCEL(ocel, currentType);
			Main.messageNormal("Flattened ocel for type "+currentType);
			
			// discover petri net using est-miner (use specpp)
			// split petri net into place nets
			// tag places with current object type
			Set<Place> placeNets = FlatLogProcessing.processFlatLogHiddenTagging(Context, flatLog, currentType, parameters); //TODO what happens if Context==null?
			// testing if it does work when not creating places from casted taggedPlaces: Yes it works! :(
//			Set<Place> placeNets = FlatLogProcessing.processFlatLogNoTagging(Context, flatLog, currentType, parameters);
			Main.updateProgress("Finished discovery of place nets for object type "+currentType+".");

			// unite place nets
			placeNetsUnion.addAll(placeNets);
		}
		
		// convert set of places to PlaceSet
		PlaceSet placeSet = new PlaceSet(placeNetsUnion);
		
		// remove duplicate place nets
		placeSet = postProcessPlaceSet(placeSet);
		
		return placeSet;
	}

	public static PlaceSet postProcessPlaceSet(PlaceSet placeSet) {
		Main.messageNormal("Starting place set post processing.");
		// Identify equal OCLPMs (ignoring variable arcs)
		HashSet<Integer> deletionSet = new HashSet<Integer>(placeSet.size());
		SerializableList<Place> placeList = placeSet.getList();
		for (int i1 = 0; i1<placeSet.size()-1; i1++) {
			if (deletionSet.contains(i1)) {
				continue; // place is already tagged to be deleted
			}
			for (int i2 = i1+1; i2<placeSet.size(); i2++) {
				if (deletionSet.contains(i2)) {
					continue; // place is already tagged to be deleted
				}
				TaggedPlace tp1 = (TaggedPlace) placeList.getElement(i1);
				TaggedPlace tp2 = (TaggedPlace) placeList.getElement(i2);
				if (tp1.isEqual(tp2)) {
					deletionSet.add(i2);
				}
			}
		}
		// delete equal places
		HashSet<Place> deletePlaces = new HashSet<Place>(deletionSet.size());
		for (int i : deletionSet) {
			deletePlaces.add(placeList.getElement(i));
		}
		for (Place p : deletePlaces) {
			placeSet.remove(p);			
		}
		Main.messageNormal("Completed place set post processing.");
		return placeSet;
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
			lpmsTagged.add(lpmResult, currentType);
		}
		System.out.println("Finished LPM discovery.");
		System.out.println("LPMResult stores "+lpmsTagged.size()+" LPMs.");
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

		messageNormal("Obtained "+placeSet.size()+" place nets for LPM discovery.");
		
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
			case PE_LEADING_O2:
			case PE_LEADING_RELAXED_O2:
				newTypeLabels = new ArrayList<String>(parameters.getObjectTypesLeadingTypes().size());
				graph = buildObjectGraph(ocel, parameters);
				// LPM discovery for each new case notion
				for (String currentType : parameters.getObjectTypesLeadingTypes()) {
					messageNormal("Starting ocel enhancement using the \""+parameters.getCaseNotionStrategy().getName()+"\" strategy for type \""+currentType+"\".");
					// enhance log by process executions as case notions
					String newTypeLabel = "PE_"+currentType;
					newTypeLabels.add(newTypeLabel);
					switch (parameters.getCaseNotionStrategy()) {
						case PE_LEADING:
							ocel = ProcessExecutions.enhanceLeadingType(ocel, newTypeLabel, currentType, graph, parameters.getObjectTypesCaseNotion());
							break;
						case PE_LEADING_RELAXED:
							ocel = ProcessExecutions.enhanceLeadingTypeRelaxed(ocel, newTypeLabel, currentType, graph, parameters.getObjectTypesCaseNotion());
							break;
						case PE_LEADING_O1:
							ocel = ProcessExecutions.enhanceLeadingTypeOptimized1(ocel, newTypeLabel, currentType, graph, parameters.getObjectTypesCaseNotion());
							break;
						case PE_LEADING_RELAXED_O1:
							ocel = ProcessExecutions.enhanceLeadingTypeRelaxedOptimized1(ocel, newTypeLabel, currentType, graph, parameters.getObjectTypesCaseNotion());
							break;
						case PE_LEADING_O2:
							ocel = ProcessExecutions.enhanceLeadingTypeOptimized2(ocel, newTypeLabel, currentType, graph, parameters.getObjectTypesCaseNotion());
							break;
						case PE_LEADING_RELAXED_O2:
							ocel = ProcessExecutions.enhanceLeadingTypeRelaxedOptimized2(ocel, newTypeLabel, currentType, graph, parameters.getObjectTypesCaseNotion());
							break;
						default:
							ocel = ProcessExecutions.enhanceLeadingTypeOptimized1(ocel, newTypeLabel, currentType, graph, parameters.getObjectTypesCaseNotion());
							break;
					}
					
					// flatten ocel
					log = Main.flattenOCEL(ocel, newTypeLabel);
				
					// discover LPMs (name of the currentType column needs concept:name, which the flattening does)
					System.out.println("Starting LPM discovery using leading type "+newTypeLabel+" as case notion.");
					lpmResults = runLPMPlugin(log, placeSet, parameters);
					assert(lpmResults[0] instanceof LPMResult);
					lpmResult = (LPMResult) lpmResults[0];
					lpmsTagged.add(lpmResult, currentType);
					
					updateProgress("Finished ocel enhancement using the \""+parameters.getCaseNotionStrategy().getName()+"\" strategy for type \""+currentType+"\".");
				}
				ProvidingObjects.exportOcel(ocel, newTypeLabels);
				break;
				
			case PE_CONNECTED:
				graph = buildObjectGraph(ocel, parameters);
				String ot = "ConnectedComponent";
				ocel = ProcessExecutions.enhanceConnectedComponent(ocel, ot, graph, parameters.getObjectTypesCaseNotion());
				ProvidingObjects.exportOcel(ocel, new ArrayList<String>(Arrays.asList(ot)));
				messageNormal("Discovered "+ocel.objectTypes.get(ot).objects.size()+" connected components.");
				log = Main.flattenOCEL(ocel, ot);
				System.out.println("Starting LPM discovery using connected components as case notion.");
				lpmResults = runLPMPlugin(log, placeSet, parameters);
				assert(lpmResults[0] instanceof LPMResult);
				lpmResult = (LPMResult) lpmResults[0];
				lpmsTagged.add(lpmResult, ot);
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
				lpmsTagged.add(lpmResult, dummyType);
		}
		System.out.println("Finished LPM discovery.");
		System.out.println("LPMResult stores "+lpmsTagged.size()+" LPMs.");
		
		return lpmsTagged;
	}
	
	public static OCLPMResult convertLPMstoOCLPMs (OCLPMDiscoveryParameters parameters, LPMResultsTagged tlpms) {

		if (!(tlpms.getList().getElement(0).getElements().get(0).getPlaces().toArray()[0] instanceof TaggedPlace)
				|| ((TaggedPlace) tlpms.getList().getElement(0).getElements().get(0).getPlaces().toArray()[0]).getObjectType() == null 
				) {
			System.out.println("The given LPMs do not have tagged places. Therefore, there won't be any variable arcs.");
		}
		
		OCLPMResult oclpmResult = new OCLPMResult(parameters, tlpms);
		
		return oclpmResult;
	}
	
	
	
	/**
	 * Variable arc identification when place set is given.
	 * @param parameters
	 * @param oclpmResult
	 * @return
	 */
	public static PlaceSet identifyVariableArcs (OCLPMDiscoveryParameters parameters, PlaceSet placeSet) {
		switch (parameters.getVariableArcIdentification()) {
			case NONE:
				break;
			case PER_PLACE:
				Main.messageNormal("Starting variable arc identification per place.");
				String type;
				OcelEventLog ocel = parameters.getOcel();
				for (Place p : placeSet.getElements()) {
					TaggedPlace tp = (TaggedPlace) p;
					type = tp.getObjectType();
					
					// get input activities
					HashSet<String> inputActivities = new HashSet<>(tp.getInputTransitions().size());
					for (Transition t : tp.getInputTransitions()) {
						inputActivities.add(t.getLabel());
					}
					// get output activities
					HashSet<String> outputActivities = new HashSet<>(tp.getOutputTransitions().size());
					for (Transition t : tp.getOutputTransitions()) {
						if (!inputActivities.contains(t.getLabel())) {
							outputActivities.add(t.getLabel());
						}
					}
					
					// get all events of input activities
					OcelEventLog filtered = ocel.cloneEmpty();
					for (OcelEvent eve : ocel.events.values()) {
						if (inputActivities.contains(eve.activity)) {
							filtered.cloneEvent(eve);
						}
					}
//					filtered.register(); // registering would duplicate the objects because the cloning already adds the object, not just the identifier
					
					// get all objects of input activities
					Set<String> inputObjects = filtered.objects.keySet(); 
					
					// get all events of output activities with objects from the input activities
					for (OcelEvent eve : ocel.events.values()) {
						if (outputActivities.contains(eve.activity)) {
							for (OcelObject object : eve.relatedObjects) {
								if (	object.objectType.name.equals(type) // same type
										&& inputObjects.contains(object.id)) { 
									filtered.cloneEvent(eve);
									break;
								}
							}
						}
					}
//					filtered.register();
					
					// compute score on the filtered log
					HashMap<List<String>,Double> score = OCELUtils.computeScore(filtered, type);
					
					// tag arcs
					HashSet<String> variableActivities = new  HashSet<>(score.size());
					score.forEach((key,value) -> {
						if ( value < parameters.getVariableArcThreshold()) {
							variableActivities.add(key.get(0));				
						}
					});
					tp.setVariableArcActivities(variableActivities);
					
				}
				break;
			case WHOLE_LOG:
			default:
				Main.messageNormal("Starting variable arc identification using only the whole log.");
				HashMap<String,HashSet<String>> typeToActivities = computeVariableArcsWholeLog(parameters);
				
				// iterate through the models and tag variable arcs
				for (Place p : placeSet.getElements()) {
					((TaggedPlace)p).setVariableArcActivities(typeToActivities.get(((TaggedPlace)p).getObjectType()));
				}
				
				if (parameters.getPlaceCompletion().needsExactVariableArcs()) {
					// trim variable arc activities to fit the actual transitions of each place
					for (Place p : placeSet.getElements()) {
						((TaggedPlace)p).trimVariableArcSet();
					}
				}
		}
		Main.updateProgress("Completed variable arc identification.");
		return placeSet;
	}
	
	/**
	 * Variable arc identification in case the plugin is started after the LPM discovery
	 * @param parameters
	 * @param oclpmResult
	 * @return
	 */
	public static OCLPMResult identifyVariableArcs (OCLPMDiscoveryParameters parameters, OCLPMResult oclpmResult) {
		switch (parameters.getVariableArcIdentification()) {
			case NONE:
				break;
			default:
				Main.messageNormal("Starting variable arc identification using only the whole log.");
				parameters.setVariableArcIdentification(VariableArcIdentification.WHOLE_LOG);
				
				HashMap<String,HashSet<String>> typeToActivities = computeVariableArcsWholeLog(parameters);
				
				// iterate through the models and tag variable arcs
				for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
					for (TaggedPlace tp : oclpm.getPlaces()) {
						tp.setVariableArcActivities(typeToActivities.get(tp.getObjectType()));
					}
				}
				
				// in case the exact number of variable arcs of places is needed
				if (parameters.getPlaceCompletion().needsExactVariableArcs()) {
					for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
						oclpm.trimVariableArcSet();
					}
				}
				Main.updateProgress("Completed variable arc identification.");
		}
		return oclpmResult;
	}
	
	/**
	 * Computes a map which assigns each object type to all the activities with which the type has variable arcs
	 * @param parameters
	 * @return
	 */
	public static HashMap<String,HashSet<String>> computeVariableArcsWholeLog (OCLPMDiscoveryParameters parameters){
		HashSet<List<String>> variableArcSet = new HashSet<>(); // saves all variable arcs [Activity,ObjectType]
		
		// compute score for each activity, objectType pair
		HashMap<List<String>,Double> score = OCELUtils.computeScore(parameters.getOcel(), parameters.getObjectTypesAll()); // maps [Activity,ObjectType] to score (#events of act and |OT|=1 / #events of act)
		
		// Store variable arcs by comparing them to a threshold for the score
		score.forEach((key,value) -> {
			if ( value < parameters.getVariableArcThreshold()) {
				variableArcSet.add(key);				
			}
		});
		
		// for each object type store which activities have variable arcs
		HashMap<String,HashSet<String>> typeToActivities = new  HashMap<>();
		String curType, curActivity;
		for (List<String> activity_type : variableArcSet) {
			curType = activity_type.get(1);
			curActivity = activity_type.get(0);
			if (!typeToActivities.containsKey(curType)) {
				typeToActivities.put(curType, new HashSet<>(Arrays.asList(curActivity)));
			}
			else {
				typeToActivities.get(curType).add(curActivity);
			}
			
		}
		return typeToActivities;
	}
	
	public static OCLPMResult evaluateOCLPMs (OCLPMDiscoveryParameters parameters, OCLPMResult oclpmResult) {
		// TODO oclpm evaluation
		return oclpmResult;
	}
	
	public static OCLPMResult postProcessing (OCLPMDiscoveryParameters parameters, OCLPMResult oclpmResult) {
		
		// store place id -> object type map to use in visualizer
		oclpmResult.createTypeMap();
		
		// write variable arcs into result map to use in visualizer
		oclpmResult.storeVariableArcs();
		
		return oclpmResult;
	}
	
	//===================================================================
	//		helpers / setup
	//===================================================================
	
	public static void setUp(PluginContext context) {
		Main.startTime = System.currentTimeMillis();
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
		
		// place completion
		if (parameters.getPlaceCompletion() != PlaceCompletion.NONE) {
			numSteps += 1;
		}
		
		// variable arc identification
		if (parameters.getVariableArcIdentification() != VariableArcIdentification.NONE) {
			numSteps += 1;
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
	
	public static Graph<String,DefaultEdge> buildObjectGraph(OcelEventLog ocel, OCLPMDiscoveryParameters parameters){
		if (!graphProvided) {
			messageNormal("Starting object graph construction.");
			Main.graph = ProcessExecutions.buildObjectGraph(ocel, parameters);
			ProvidingObjects.exportObjectGraph(graph);
			updateProgress("Constructed object graph with "+graph.vertexSet().size()+" vertices and "+graph.edgeSet().size()+" edges.");
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
//		PrintStream out = System.out;
//		System.setOut(new PrintStream(new NullOutputStream()));
		XLog log = Flattening.flatten(ocel, newTypeLabel); // this pastes a lot of useless stuff in the console
//		System.setOut(out);
		
//		int numEventsBefore = ocel.getEvents().size();
//		int numEventsAfter = log.size();
//		System.out.println("Flattening log for the new type \""+newTypeLabel+"\" lead to an increase of events by a factor of "+(double) numEventsAfter/numEventsBefore+".");
		return log;
	}
	
	public static void printExecutionTime() {
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - Main.startTime;
		
		System.out.println("OCLPM Discovery Execution time: "+Math.round((elapsedTime/1000.0/60.0) * 1000.0)/1000.0+" Minutes");
	}
}
