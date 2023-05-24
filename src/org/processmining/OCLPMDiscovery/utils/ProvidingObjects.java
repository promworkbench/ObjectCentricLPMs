package org.processmining.OCLPMDiscovery.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.plugins.utils.ProvidedObjectHelper;

public class ProvidingObjects {

	public static void exportPlaceSet(PlaceSet placeSet) {
		// Add the places as a provided object
		if (Main.UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: United Place Set"
	        		, placeSet, PlaceSet.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), placeSet);
		}
	}
	

	public static void exportHashMap(HashMap<String, String> typeMap) {
		if (Main.UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: HashMap for Places to Object Types"
	        		, typeMap, HashMap.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), typeMap);
		}
	}
	
	public static void exportTlpms(LPMResultsTagged tlpms) {
		if (Main.UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: LPMResults for all Case Notions"
	        		, tlpms, LPMResultsTagged.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), tlpms);
		}
	}
	
	public static void exportObjectGraph(Graph<String,DefaultEdge> graph) {
		if (Main.UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: Object Graph"
	        		, graph, Graph.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), graph);
		}
	}
	
	public static void exportOcel(OcelEventLog ocel) {
		OcelEventLog ocelCopy = OCELUtils.deepCopy(ocel);
		if (Main.UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: OCEL"
	        		, ocelCopy, OcelEventLog.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), ocelCopy);
		}
	}
	
	public static void exportOcel(OcelEventLog ocel, ArrayList<String> labels) {
		exportOcel(ocel);
		if (Main.UsingContext) {
	        Main.getContext().getProvidedObjectManager().createProvidedObject(
	        		"OCLPM Discovery: new Type Labels"
	        		, labels, ArrayList.class, Main.getContext());
	        ProvidedObjectHelper.setFavorite(Main.getContext(), labels);
		}
	}
}
