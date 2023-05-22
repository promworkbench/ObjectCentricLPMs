package org.processmining.OCLPMDiscovery.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;
import org.processmining.ocel.ocelobjects.OcelObjectType;

public class ProcessExecutions {

	//TODO
	/**
	 * Enhances the ocel with process executions discovered with the "connected component strategy"
	 * @param ocel
	 * @param otName Name of the new object type.
	 * @return ocel with a new object type for the process executions.
	 */
	public static OcelEventLog enhanceConnectedComponent (OcelEventLog ocel, String otName, Graph<String,DefaultEdge> graph) {
		System.out.println("Starting process execution enhancement using connected components.");

		// build object graph
			//  (object graph = objects form the nodes and are connected if they share an event)
//		Graph<String,DefaultEdge> graph = buildObjectGraph(ocel);
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, otName);
		ocel.objectTypes.put(otName, ot);
		
		// detect connected components
		ConnectivityInspector<String,DefaultEdge> inspector = new ConnectivityInspector((UndirectedGraph) graph);
		List<Set<String>> components = inspector.connectedSets();
		System.out.println("Discovered "+components.size()+" connected components.");
		HashMap<String,String> mapComponent = new HashMap<>(); // maps object identifier to corresponding connected component
		String objectID = new String();
		for (int i_component = 0; i_component<components.size(); i_component++) {
			objectID = "c"+i_component;
			for (String o : components.get(i_component)) {
				mapComponent.put(o, objectID);				
			}
			System.out.println("Component "+i_component+" consists of "+components.get(i_component).size()+" objects.");
			// create object for the component
			OcelObject curObject = new OcelObject(ocel);
			curObject.objectType = ot;
			curObject.id = objectID;
			ocel.objects.put(curObject.id,curObject);
		}
		
		// assign each event an object corresponding to the connected component it is in
		String currentComponent;
		for (OcelEvent event : ocel.getEvents().values()) {
			currentComponent = mapComponent.get(event.relatedObjectsIdentifiers.iterator().next());
			event.relatedObjectsIdentifiers.add(currentComponent);
		}
		
		ocel.register();
		
		System.out.println("Finished ocel enhancement using connected components.");
		return ocel;
	}
	
	public static OcelEventLog enhanceLeadingTypeRelaxed (OcelEventLog ocel, String newTypeLabel, String leadingType, Graph<String,DefaultEdge> graph) {
		System.out.println("Starting ocel enhancement using the leading type relaxed strategy for type "+leadingType+".");
		// breadth first search on each object 
		// to build a hashmap mapping each object to its closest objects of type leading type
		HashMap<String,Set<String>> map = new HashMap<>(); // maps object identifier to
		for (String currentObject : graph.vertexSet()) {
			// current object already leading type?
			if (ocel.getObjects().get(currentObject).objectType.name.equals(leadingType)) { // TODO too slow?
				map.put(currentObject, Collections.singleton(currentObject));
			}
			else {
				//! bfIterator.getDepth(v) isn't supported in the JGraphT version which ProM uses...
				BreadthFirstIteratorWithLevel<String,DefaultEdge> bfIterator = new BreadthFirstIteratorWithLevel<String,DefaultEdge>(graph, currentObject);
				String v;
				boolean foundOne = false; // set true if a vertex of leading type has been found
				int foundAt = 0; // level where it has been found
				HashSet<String> foundSet = new HashSet<String>();
				while (bfIterator.hasNext()) {
					v = bfIterator.next();
					// there might be more vertices with the same distance
					if (foundOne && foundAt < bfIterator.getDepth(v)) {
						break;
					}
					if (ocel.getObjects().get(v).objectType.name.equals(leadingType)) {
						if (!foundOne) {
							foundOne = true;
							foundAt = bfIterator.getDepth(v);							
						}
						foundSet.add(v);
					}
				}
				map.put(currentObject,foundSet);
			}
		}
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, newTypeLabel);
		ocel.objectTypes.put(newTypeLabel, ot);
		
		// assign each event the objects which result from putting all the objects 
		// from all the types of the event into the hasmap
		HashSet<String> tmpSet = new HashSet<String>();
		for (OcelEvent event : ocel.getEvents().values()) {
			// for each event have to check each object of each type
			for (String obj : event.relatedObjectsIdentifiers) {
				tmpSet.addAll(map.get(obj)); // doesn't work without tmpSet because of concurrent modifications
			}
			event.relatedObjectsIdentifiers.addAll(tmpSet);
			tmpSet.clear();
		}
		
		ocel.register();
		
		System.out.println("Finished ocel enhancement using the leading type relaxed strategy for type "+leadingType+".");
		
		return ocel;
	}
	
	public static OcelEventLog enhanceLeadingType (OcelEventLog ocel, String otName, String leadingType) {

		// TODO
		
		return ocel;
	}
	
	public static Graph<String,DefaultEdge> buildObjectGraph (OcelEventLog ocel){
		Graph<String,DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		
		for (String object : ocel.getObjects().keySet()) {
			graph.addVertex(object);
		}
		
		for (OcelEvent event : ocel.getEvents().values()) {
			for (String o1 : event.relatedObjectsIdentifiers) {
				for (String o2 : event.relatedObjectsIdentifiers) {
					// simple graph cannot contain graph loops or multi-edges 
					// so we don't need to check this here
					// uff, actually have to check because otherwise it stops throwing an exception
					if (!o1.equals(o2)) {
						graph.addEdge(o1, o2);						
					}
				}
			}
		}
		System.out.println("Contructed object graph with "+graph.vertexSet().size()+" vertices and "+graph.edgeSet().size()+" edges.");
		return graph;
	}
}
