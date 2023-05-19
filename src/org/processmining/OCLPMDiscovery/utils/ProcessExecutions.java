package org.processmining.OCLPMDiscovery.utils;

import java.util.HashMap;
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
//		ocel = OCELUtils.addDummyCaseNotion(ocel, otName, "42");
		
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
	
	public static OcelEventLog enhanceLeadingTypeRelaxed (OcelEventLog ocel, String otName, String leadingType) {

		// build object graph
		
		// breadth first search on leading type 
		// to build a hashmap mapping each object to its closest objects of type leading type
		
		// add new object type
		
		// assign each event the objects which result from putting all the objects 
		// from all the types of the event into the hasmap
		
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
