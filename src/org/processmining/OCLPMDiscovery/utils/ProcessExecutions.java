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
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;
import org.processmining.ocel.ocelobjects.OcelObjectType;

public class ProcessExecutions {

	/**
	 * Enhances the ocel with process executions discovered with the "connected component strategy"
	 * @param ocel
	 * @param otName Name of the new object type.
	 * @return ocel with a new object type for the process executions.
	 */
	public static OcelEventLog enhanceConnectedComponent (OcelEventLog ocel, String otName, Graph<String,DefaultEdge> graph, Set<String> objectTypesCaseNotion) {
//		System.out.println("Starting process execution enhancement using connected components.");
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, otName);
		ocel.objectTypes.put(otName, ot);
		
		// detect connected components
		ConnectivityInspector<String,DefaultEdge> inspector = new ConnectivityInspector<String,DefaultEdge>((UndirectedGraph) graph);
		List<Set<String>> components = inspector.connectedSets();
		System.out.println("Discovered "+components.size()+" connected components.");
		HashMap<String,String> mapComponent = new HashMap<>(); // maps object identifier to corresponding connected component
		String objectID = new String();
		for (int i_component = 0; i_component<components.size(); i_component++) {
			objectID = "ConnectedComponent-"+i_component;
			for (String o : components.get(i_component)) {
				mapComponent.put(o, objectID);				
			}
			if (components.get(i_component).size()>1) {
				System.out.println("Component "+i_component+" consists of "+components.get(i_component).size()+" objects.");
			}
			// create object for the component
			OcelObject curObject = new OcelObject(ocel);
			curObject.objectType = ot;
			curObject.id = objectID;
			ocel.objects.put(curObject.id,curObject);
		}
		System.out.println("Components not printed have size 1.");
		
		// assign each event an object corresponding to the connected component it is in
		String currentComponent;
		for (OcelEvent event : ocel.getEvents().values()) {
			for (OcelObject o : event.relatedObjects) {
				// get one of the considered objects
				if (objectTypesCaseNotion.contains(o.objectType.name)) {
					currentComponent = mapComponent.get(o.id);
					event.relatedObjectsIdentifiers.add(currentComponent);
					break;
				}
			}
		}
		
		ocel.register();
		
//		System.out.println("Finished ocel enhancement using connected components.");
		return ocel;
	}
	
	/**
	 * Process Execution "Leading Type" strategy alteration by assigning each object to the closest objects of leading type.
	 * !Exploding memory consumption when more "abstract" objects are used which are close to many objects of leading type.
	 * @param ocel
	 * @param newTypeLabel
	 * @param leadingType
	 * @param graph
	 * @return
	 */
	public static OcelEventLog enhanceLeadingTypeRelaxed (OcelEventLog ocel, String newTypeLabel, String leadingType, Graph<String,DefaultEdge> graph, Set<String> objectTypesCaseNotion) {
//		System.out.println("Starting ocel enhancement using the leading type relaxed strategy for type "+leadingType+".");
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, newTypeLabel);
		ocel.objectTypes.put(newTypeLabel, ot);
		
		// for each object of leading type, create a new object which links to the new object type.
		HashMap<String,String> idMap = new HashMap<String,String>(); // maps old object id to id of new object in the new type
		for (OcelObject o : ocel.objectTypes.get(leadingType).objects) {
			String newID = "PE_Leading_"+leadingType+"_"+o.id;
			idMap.put(o.id, newID);
			OcelObject newObject = new OcelObject(ocel);
			newObject.id = newID;
			newObject.objectType = ot;
			ocel.objects.put(newID, newObject);
		}
		
		// breadth first search on each object 
		// to build a hashmap mapping each object to its closest objects of type leading type
		HashMap<String,Set<String>> map = new HashMap<>(); // maps object identifier to
		for (String currentObject : graph.vertexSet()) {
			// current object already leading type?
			if (ocel.getObjects().get(currentObject).objectType.name.equals(leadingType)) { // TODO too slow?
				map.put(currentObject, Collections.singleton(idMap.get(currentObject)));
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
						foundSet.add(idMap.get(v));
					}
				}
				map.put(currentObject,foundSet);
			}
		}
		
		// assign each event the objects which result from putting all the objects 
		// from all the types of the event into the hashmap
		HashSet<String> tmpSet = new HashSet<String>();
		int numEventsAfter = 0;
		for (OcelEvent event : ocel.getEvents().values()) {
			// for each event have to check each object of each type
			for (OcelObject obj : event.relatedObjects) {
				if (!objectTypesCaseNotion.contains(obj.objectType.name)) { // object is of ignored type
					continue;
				}
				Set<String> mapGet = map.get(obj.id);
				if (mapGet != null) {
					tmpSet.addAll(map.get(obj.id)); // doesn't work without tmpSet because of concurrent modifications						
				}
			}
			event.relatedObjectsIdentifiers.addAll(tmpSet);
			numEventsAfter += tmpSet.size();
			tmpSet.clear();
		}
		
		ocel.register();
		
		int numEventsBefore = ocel.getEvents().size();
		System.out.println(
				"Flattening log for the new type \""+newTypeLabel+"\" lead to an increase of events "
						+ "by a factor of "+(double) numEventsAfter/numEventsBefore+"."
						);
//		System.out.println("Finished ocel enhancement using the leading type relaxed strategy for type "+leadingType+".");
		
		return ocel;
	}
	
	/**
	 * Process Execution "Leading Type" strategy.
	 * !Exploding memory consumption when more "abstract" objects are used which are close to many objects of leading type.
	 * @param ocel
	 * @param newTypeLabel
	 * @param leadingType
	 * @param graph
	 * @return
	 */
	public static OcelEventLog enhanceLeadingType (OcelEventLog ocel, String newTypeLabel, String leadingType, Graph<String,DefaultEdge> graph, Set<String> objectTypesCaseNotion) {

//		System.out.println("Starting ocel enhancement using the leading type strategy for type "+leadingType+".");
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, newTypeLabel);
		ocel.objectTypes.put(newTypeLabel, ot);
		
		// for each object of leading type), create a new object which links to the new object type.
		HashMap<String,String> idMap = new HashMap<String,String>(); // maps old object id to id of new object in the new type
		for (OcelObject o : ocel.objectTypes.get(leadingType).objects) {
			String newID = "PE_Leading_"+leadingType+"_"+o.id;
			idMap.put(o.id, newID);
			OcelObject newObject = new OcelObject(ocel);
			newObject.id = newID;
			newObject.objectType = ot;
			ocel.objects.put(newID, newObject);
		}
		
		// breadth first search on each object of leading type
		// to build a hashmap mapping each object to its closest objects of type leading type
		HashMap<String,Set<String>> map = new HashMap<>(); // maps object identifier to new object id of leading type/s object it has been assigned to
		String currentType, vType;
		int vDepth; 
		int lastDepth = 0;
		for (String currentObject : graph.vertexSet()) {
			currentType = ocel.getObjects().get(currentObject).objectType.name; // TODO too slow?
			// current object not leading type -> skip
			if (!currentType.equals(leadingType)) { 
				continue;
			}
			else { // on object of leading type
				BreadthFirstIteratorWithLevel<String,DefaultEdge> bfIterator = new BreadthFirstIteratorWithLevel<String,DefaultEdge>(graph, currentObject);
				String v;
				// for each object type remember if it has been found and at which depth
				HashMap<String, Integer> foundMap = new HashMap<String, Integer>();
				foundMap.put(currentType, 0); // leading type has been found at depth 0
				map.put(currentObject, Collections.singleton(idMap.get(currentObject))); // leading type object will be in its own process execution
				while (bfIterator.hasNext()) {
					v = bfIterator.next();
					vType = ocel.getObjects().get(v).objectType.name;
					vDepth = bfIterator.getDepth(v);
					// if all types have been found and new depth is entered quit
					if (foundMap.keySet().size() == ocel.getObjectTypes().size() && vDepth > lastDepth) {
						break;
					}
					// if node of this type has already been added with a shorter distance discard it
					if (foundMap.containsKey(vType) && vDepth > foundMap.get(vType)) {
						continue;
					}
					// object of this type already found but this has the same distance
					else if (foundMap.containsKey(vType)) {
						if (map.containsKey(v)) { // object is also closest to another of leading type
							HashSet<String> tmpSet = new HashSet<String>(); 
							tmpSet.addAll(map.get(v));
							tmpSet.add(idMap.get(currentObject));
							map.put(v,tmpSet);
						}
						else {
							map.put(v, Collections.singleton(idMap.get(currentObject)));
						}
					}
					// object of this type is new
					else {
						foundMap.put(vType, vDepth);
						lastDepth = vDepth;
						if (map.containsKey(v)) { // object is also closest to another of leading type
							HashSet<String> tmpSet = new HashSet<String>(); 
							tmpSet.addAll(map.get(v));
							tmpSet.add(idMap.get(currentObject));
							map.put(v,tmpSet);
						}
						else {
							map.put(v, Collections.singleton(idMap.get(currentObject)));
						}
					}
				}
			}
		}
		
		// assign each event the objects which result from putting all the objects 
		// from all the types of the event into the hashmap
		// some objects hash to nothing, which is ok. 
		// If an event has only objects mapping to nothing it will be discarded when flattening.
		HashSet<String> tmpSet = new HashSet<String>();
		int numEventsAfter = 0;
		for (OcelEvent event : ocel.getEvents().values()) {
			// for each event have to check each object of each type
			for (OcelObject obj : event.relatedObjects) {
				if (!objectTypesCaseNotion.contains(obj.objectType.name)) { // object is of ignored type
					continue;
				}
				Set<String> mapGet = map.get(obj.id);
				if (mapGet != null) {
					tmpSet.addAll(map.get(obj.id)); // doesn't work without tmpSet because of concurrent modifications						
				}
			}
			event.relatedObjectsIdentifiers.addAll(tmpSet); 
			numEventsAfter += tmpSet.size();
			tmpSet.clear();
		}
		
		ocel.register();
		
		int numEventsBefore = ocel.getEvents().size();
		System.out.println(
				"Flattening log for the new type \""+newTypeLabel+"\" lead to an increase of events "
						+ "by a factor of "+(double) numEventsAfter/numEventsBefore+"."
						);
//		System.out.println("Finished ocel enhancement using the leading type strategy for type "+leadingType+".");
		
		return ocel;
	}
	
	/**
	 * Process Execution "Leading Type" strategy but assigning events with leading type objects only to those PEs.
	 * Leads to fewer replications, but memory consumption might still explode.
	 * !Exploding memory consumption when more "abstract" objects are used which are close to many objects of leading type.
	 * @param ocel
	 * @param newTypeLabel
	 * @param leadingType
	 * @param graph
	 * @return
	 */
	public static OcelEventLog enhanceLeadingTypeOptimized1 (OcelEventLog ocel, String newTypeLabel, String leadingType, Graph<String,DefaultEdge> graph, Set<String> objectTypesCaseNotion) {
		
//		System.out.println("Starting ocel enhancement using the leading type optimization 1 strategy for type "+leadingType+".");
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, newTypeLabel);
		ocel.objectTypes.put(newTypeLabel, ot);
		
		// for each object of leading type), create a new object which links to the new object type.
		HashMap<String,String> idMap = new HashMap<String,String>(); // maps old object id to id of new object in the new type
		for (OcelObject o : ocel.objectTypes.get(leadingType).objects) {
			String newID = "PE_Leading_"+leadingType+"_"+o.id;
			idMap.put(o.id, newID);
			OcelObject newObject = new OcelObject(ocel);
			newObject.id = newID;
			newObject.objectType = ot;
			ocel.objects.put(newID, newObject);
		}
		
		// breadth first search on each object of leading type
		// to build a hashmap mapping each object to its closest objects of type leading type
		HashMap<String,Set<String>> map = new HashMap<>(); // maps object identifier to new object id of leading type/s object it has been assigned to
		String currentType, vType;
		int vDepth; 
		int lastDepth = 0;
		for (String currentObject : graph.vertexSet()) {
			currentType = ocel.getObjects().get(currentObject).objectType.name; // TODO too slow?
			// current object not leading type -> skip
			if (!currentType.equals(leadingType)) { 
				continue;
			}
			else { // on object of leading type
				BreadthFirstIteratorWithLevel<String,DefaultEdge> bfIterator = new BreadthFirstIteratorWithLevel<String,DefaultEdge>(graph, currentObject);
				String v;
				// for each object type remember if it has been found and at which depth
				HashMap<String, Integer> foundMap = new HashMap<String, Integer>();
				foundMap.put(currentType, 0); // leading type has been found at depth 0
				map.put(currentObject, Collections.singleton(idMap.get(currentObject))); // leading type object will be in its own process execution
				while (bfIterator.hasNext()) {
					v = bfIterator.next();
					vType = ocel.getObjects().get(v).objectType.name;
					vDepth = bfIterator.getDepth(v);
					// if all types have been found and new depth is entered quit
					if (foundMap.keySet().size() == ocel.getObjectTypes().size() && vDepth > lastDepth) {
						break;
					}
					// if node of this type has already been added with a shorter distance discard it
					if (foundMap.containsKey(vType) && vDepth > foundMap.get(vType)) {
						continue;
					}
					// object of this type already found but this has the same distance
					else if (foundMap.containsKey(vType)) {
						if (map.containsKey(v)) { // object is also closest to another of leading type
							HashSet<String> tmpSet = new HashSet<String>(); 
							tmpSet.addAll(map.get(v));
							tmpSet.add(idMap.get(currentObject));
							map.put(v,tmpSet);
						}
						else {
							map.put(v, Collections.singleton(idMap.get(currentObject)));
						}
					}
					// object of this type is new
					else {
						foundMap.put(vType, vDepth);
						lastDepth = vDepth;
						if (map.containsKey(v)) { // object is also closest to another of leading type
							HashSet<String> tmpSet = new HashSet<String>(); 
							tmpSet.addAll(map.get(v));
							tmpSet.add(idMap.get(currentObject));
							map.put(v,tmpSet);
						}
						else {
							map.put(v, Collections.singleton(idMap.get(currentObject)));
						}
					}
				}
			}
		}
		
		// assign each event the objects which result from putting all the objects 
		// from all the types of the event into the hashmap
		// some objects hash to nothing, which is ok. 
		// If an event has only objects mapping to nothing it will be discarded when flattening.
		HashSet<String> tmpSet = new HashSet<String>();
		boolean discoveredLeadingObject;
		int numEventsAfter = 0;
		for (OcelEvent event : ocel.getEvents().values()) {
			discoveredLeadingObject = false;
			// for each event have to check each object of each type
			for (OcelObject obj : event.relatedObjects) {
				if (!objectTypesCaseNotion.contains(obj.objectType.name)) { // object is of ignored type
					continue;
				}
				// if current event has objects of leading type ignore the others
				if (ocel.getObjects().get(obj.id).objectType.name.equals(leadingType)) {
					if (!discoveredLeadingObject) {
						tmpSet.clear();
						discoveredLeadingObject = true;						
					}
					tmpSet.addAll(map.get(obj.id));
					continue;
				}
				if (!discoveredLeadingObject) {
					Set<String> mapGet = map.get(obj.id);
					if (mapGet != null) {
						tmpSet.addAll(map.get(obj.id)); // doesn't work without tmpSet because of concurrent modifications						
					}
				}
			}
			event.relatedObjectsIdentifiers.addAll(tmpSet); 
			numEventsAfter += tmpSet.size();
			tmpSet.clear();
		}
		
		ocel.register();
		
		int numEventsBefore = ocel.getEvents().size();
		System.out.println(
				"Flattening log for the new type \""+newTypeLabel+"\" lead to an increase of events "
						+ "by a factor of "+(double) numEventsAfter/numEventsBefore+"."
						);
//		System.out.println("Finished ocel enhancement for type "+leadingType+".");
		
		return ocel;
	}
	
	public static OcelEventLog enhanceLeadingTypeRelaxedOptimized1 (OcelEventLog ocel, String newTypeLabel, String leadingType, Graph<String,DefaultEdge> graph, Set<String> objectTypesCaseNotion) {
//		System.out.println("Starting ocel enhancement using the leading type relaxed optimized 1 strategy for type "+leadingType+".");
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, newTypeLabel);
		ocel.objectTypes.put(newTypeLabel, ot);
		
		// for each object of leading type, create a new object which links to the new object type.
		HashMap<String,String> idMap = new HashMap<String,String>(); // maps old object id to id of new object in the new type
		for (OcelObject o : ocel.objectTypes.get(leadingType).objects) {
			String newID = "PE_Leading_"+leadingType+"_"+o.id;
			idMap.put(o.id, newID);
			OcelObject newObject = new OcelObject(ocel);
			newObject.id = newID;
			newObject.objectType = ot;
			ocel.objects.put(newID, newObject);
		}
		
		// breadth first search on each object 
		// to build a hashmap mapping each object to its closest objects of type leading type
		HashMap<String,Set<String>> map = new HashMap<>(); // maps object identifier to
		for (String currentObject : graph.vertexSet()) {
			// current object already leading type?
			if (ocel.getObjects().get(currentObject).objectType.name.equals(leadingType)) { // TODO too slow?
				map.put(currentObject, Collections.singleton(idMap.get(currentObject)));
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
						foundSet.add(idMap.get(v));
					}
				}
				map.put(currentObject,foundSet);
			}
		}
		
		// assign each event the objects which result from putting all the objects 
		// from all the types of the event into the hashmap
		HashSet<String> tmpSet = new HashSet<String>();
		boolean discoveredLeadingObject;
		int numEventsAfter = 0;
		for (OcelEvent event : ocel.getEvents().values()) {
			discoveredLeadingObject = false;
			// for each event have to check each object of each type
			for (OcelObject obj : event.relatedObjects) {
				if (!objectTypesCaseNotion.contains(obj.objectType.name)) { // object is of ignored type
					continue;
				}
				// if current event has objects of leading type ignore the others
				if (ocel.getObjects().get(obj.id).objectType.name.equals(leadingType)) {
					if (!discoveredLeadingObject) {
						tmpSet.clear();
						discoveredLeadingObject = true;						
					}
					tmpSet.addAll(map.get(obj.id));
					continue;
				}
				if (!discoveredLeadingObject) {
					Set<String> mapGet = map.get(obj.id);
					if (mapGet != null) {
						tmpSet.addAll(map.get(obj.id)); // doesn't work without tmpSet because of concurrent modifications						
					}
				}
			}
			event.relatedObjectsIdentifiers.addAll(tmpSet);
			numEventsAfter += tmpSet.size();
			tmpSet.clear();
		}
		
		ocel.register();
		
		int numEventsBefore = ocel.getEvents().size();
		System.out.println(
				"Flattening log for the new type \""+newTypeLabel+"\" lead to an increase of events "
						+ "by a factor of "+(double) numEventsAfter/numEventsBefore+"."
						);
		
//		System.out.println("Finished ocel enhancement using the leading type relaxed strategy for type "+leadingType+".");
		
		return ocel;
	}
	
	/**
	 * Process Execution "Leading Type" strategy with optimization 1 and for events 
	 * without leading type object, assigning them only to the PEs of the type belonging to the fewest PEs.
	 * Leads to even fewer replications, and makes exploding memory consumption very unlikely.
	 * @param ocel
	 * @param newTypeLabel
	 * @param leadingType
	 * @param graph
	 * @return
	 */
	public static OcelEventLog enhanceLeadingTypeOptimized2 (OcelEventLog ocel, String newTypeLabel, String leadingType, Graph<String,DefaultEdge> graph, Set<String> objectTypesCaseNotion) {
		
//		System.out.println("Starting ocel enhancement using the leading type optimization 1 strategy for type "+leadingType+".");
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, newTypeLabel);
		ocel.objectTypes.put(newTypeLabel, ot);
		
		// for each object of leading type), create a new object which links to the new object type.
		HashMap<String,String> idMap = new HashMap<String,String>(); // maps old object id to id of new object in the new type
		for (OcelObject o : ocel.objectTypes.get(leadingType).objects) {
			String newID = "PE_Leading_"+leadingType+"_"+o.id;
			idMap.put(o.id, newID);
			OcelObject newObject = new OcelObject(ocel);
			newObject.id = newID;
			newObject.objectType = ot;
			ocel.objects.put(newID, newObject);
		}
		
		// breadth first search on each object of leading type
		// to build a hashmap mapping each object to its closest objects of type leading type
		HashMap<String,Set<String>> map = new HashMap<>(); // maps object identifier to new object id of leading type/s object it has been assigned to
		String currentType, vType;
		int vDepth; 
		int lastDepth = 0;
		for (String currentObject : graph.vertexSet()) {
			currentType = ocel.getObjects().get(currentObject).objectType.name; // TODO too slow?
			// current object not leading type -> skip
			if (!currentType.equals(leadingType)) { 
				continue;
			}
			else { // on object of leading type
				BreadthFirstIteratorWithLevel<String,DefaultEdge> bfIterator = new BreadthFirstIteratorWithLevel<String,DefaultEdge>(graph, currentObject);
				String v;
				// for each object type remember if it has been found and at which depth
				HashMap<String, Integer> foundMap = new HashMap<String, Integer>();
				foundMap.put(currentType, 0); // leading type has been found at depth 0
				map.put(currentObject, Collections.singleton(idMap.get(currentObject))); // leading type object will be in its own process execution
				while (bfIterator.hasNext()) {
					v = bfIterator.next();
					vType = ocel.getObjects().get(v).objectType.name;
					vDepth = bfIterator.getDepth(v);
					// if all types have been found and new depth is entered quit
					if (foundMap.keySet().size() == ocel.getObjectTypes().size() && vDepth > lastDepth) {
						break;
					}
					// if node of this type has already been added with a shorter distance discard it
					if (foundMap.containsKey(vType) && vDepth > foundMap.get(vType)) {
						continue;
					}
					// object of this type already found but this has the same distance
					else if (foundMap.containsKey(vType)) {
						if (map.containsKey(v)) { // object is also closest to another of leading type
							HashSet<String> tmpSet = new HashSet<String>(); 
							tmpSet.addAll(map.get(v));
							tmpSet.add(idMap.get(currentObject));
							map.put(v,tmpSet);
						}
						else {
							map.put(v, Collections.singleton(idMap.get(currentObject)));
						}
					}
					// object of this type is new
					else {
						foundMap.put(vType, vDepth);
						lastDepth = vDepth;
						if (map.containsKey(v)) { // object is also closest to another of leading type
							HashSet<String> tmpSet = new HashSet<String>(); 
							tmpSet.addAll(map.get(v));
							tmpSet.add(idMap.get(currentObject));
							map.put(v,tmpSet);
						}
						else {
							map.put(v, Collections.singleton(idMap.get(currentObject)));
						}
					}
				}
			}
		}
		
		// assign each event the objects which result from putting all the objects 
		// from all the types of the event into the hashmap
		// some objects hash to nothing, which is ok. 
		// If an event has only objects mapping to nothing it will be discarded when flattening.
		HashSet<String> tmpSet = new HashSet<String>();
		int minTmpSet = -1;
		int numEventsAfter = 0;
		HashMap<String,Set<String>> typeToPEs = new HashMap<String,Set<String>>();
		String curType;
		for (OcelEvent event : ocel.getEvents().values()) {
			// construct map which maps each object type to the process executions of all the objects of that type occurring in this event
			typeToPEs.clear();
			for (OcelObject obj : event.relatedObjects) {
				if (!objectTypesCaseNotion.contains(obj.objectType.name)) { // object is of ignored type
					continue;
				}
				curType = obj.objectType.name;
				HashSet<String> mapGet = new HashSet<String>(); 
						map.get(obj.id);
				if (map.get(obj.id) == null) {
					continue;
				}
				mapGet.addAll(map.get(obj.id));
				if (typeToPEs.containsKey(curType)) {
					mapGet.addAll(typeToPEs.get(curType));
				}
				typeToPEs.put(curType, mapGet);
			}
			
			// Optimization 1: if leading type object was present, just add the PEs corresponding to those
			if (typeToPEs.get(leadingType) != null) {
				tmpSet.addAll(typeToPEs.get(leadingType));
			}
			// Optimization 2: otherwise choose the type with the fewest PEs and add only those
			else {
				Object[] PeArray = typeToPEs.values().toArray();
				minTmpSet = -1;
				int indexMinTmpSet = -1;
				for (int i = 0; i < PeArray.length; i++) {
					if (PeArray[i]!=null && (((Set<String>)PeArray[i]).size() < minTmpSet || minTmpSet < 0)) {
						minTmpSet = ((Set<String>)PeArray[i]).size();
						indexMinTmpSet = i;
					}
				}
				tmpSet.addAll((Set<String>)PeArray[indexMinTmpSet]);
			}
			
			event.relatedObjectsIdentifiers.addAll(tmpSet); 
			numEventsAfter += tmpSet.size();
			tmpSet.clear();
		}
		
		ocel.register();
		
		int numEventsBefore = ocel.getEvents().size();
		System.out.println(
				"Flattening log for the new type \""+newTypeLabel+"\" lead to an increase of events "
						+ "by a factor of "+(double) numEventsAfter/numEventsBefore+"."
						);
//		System.out.println("Finished ocel enhancement for type "+leadingType+".");
		
		return ocel;
	}
	
	/**
	 * Process Execution "Leading Type Relaxed" strategy with optimization 1 and for events 
	 * without leading type object, assigning them only to the PEs of the type belonging to the fewest PEs.
	 * Leads to even fewer replications, and makes exploding memory consumption very unlikely.
	 * @param ocel
	 * @param newTypeLabel
	 * @param leadingType
	 * @param graph
	 * @return
	 */
	public static OcelEventLog enhanceLeadingTypeRelaxedOptimized2 (OcelEventLog ocel, String newTypeLabel, String leadingType, Graph<String,DefaultEdge> graph, Set<String> objectTypesCaseNotion) {
//		System.out.println("Starting ocel enhancement using the leading type relaxed optimized 1 strategy for type "+leadingType+".");
		
		// add new object type
		OcelObjectType ot = new OcelObjectType(ocel, newTypeLabel);
		ocel.objectTypes.put(newTypeLabel, ot);
		
		// for each object of leading type, create a new object which links to the new object type.
		HashMap<String,String> idMap = new HashMap<String,String>(); // maps old object id to id of new object in the new type
		for (OcelObject o : ocel.objectTypes.get(leadingType).objects) {
			String newID = "PE_Leading_"+leadingType+"_"+o.id;
			idMap.put(o.id, newID);
			OcelObject newObject = new OcelObject(ocel);
			newObject.id = newID;
			newObject.objectType = ot;
			ocel.objects.put(newID, newObject);
		}
		
		// breadth first search on each object 
		// to build a hashmap mapping each object to its closest objects of type leading type
		HashMap<String,Set<String>> map = new HashMap<>(); // maps object identifier to
		for (String currentObject : graph.vertexSet()) {
			// current object already leading type?
			if (ocel.getObjects().get(currentObject).objectType.name.equals(leadingType)) { // TODO too slow?
				map.put(currentObject, Collections.singleton(idMap.get(currentObject)));
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
						foundSet.add(idMap.get(v));
					}
				}
				map.put(currentObject,foundSet);
			}
		}
		
		// assign each event the objects which result from putting all the objects 
		// from all the types of the event into the hashmap
		HashSet<String> tmpSet = new HashSet<String>();
		int numEventsAfter = 0;		
		HashMap<String,Set<String>> typeToPEs = new HashMap<String,Set<String>>();
		String curType;
		int minTmpSet = -1;
		for (OcelEvent event : ocel.getEvents().values()) {
			// construct map which maps each object type to the process executions of all the objects of that type occurring in this event
			typeToPEs.clear();
			for (OcelObject obj : event.relatedObjects) {
				if (!objectTypesCaseNotion.contains(obj.objectType.name)) { // object is of ignored type
					continue;
				}
				curType = obj.objectType.name;
				HashSet<String> mapGet = new HashSet<String>(); 
						map.get(obj.id);
				if (map.get(obj.id) == null) {
					continue;
				}
				mapGet.addAll(map.get(obj.id));
				if (typeToPEs.containsKey(curType)) {
					mapGet.addAll(typeToPEs.get(curType));
				}
				typeToPEs.put(curType, mapGet);
			}
			
			// Optimization 1: if leading type object was present, just add the PEs corresponding to those
			if (typeToPEs.get(leadingType) != null) {
				tmpSet.addAll(typeToPEs.get(leadingType));
			}
			// Optimization 2: otherwise choose the type with the fewest PEs and add only those
			else {
				Object[] PeArray = typeToPEs.values().toArray();
				minTmpSet = -1;
				int indexMinTmpSet = -1;
				for (int i = 0; i < PeArray.length; i++) {
					if (PeArray[i]!=null && (((Set<String>)PeArray[i]).size() < minTmpSet || minTmpSet < 0)) {
						minTmpSet = ((Set<String>)PeArray[i]).size();
						indexMinTmpSet = i;
					}
				}
				tmpSet.addAll((Set<String>)PeArray[indexMinTmpSet]);
			}
			
			event.relatedObjectsIdentifiers.addAll(tmpSet); 
			numEventsAfter += tmpSet.size();
			tmpSet.clear();
		}
		
		ocel.register();
		
		int numEventsBefore = ocel.getEvents().size();
		System.out.println(
				"Flattening log for the new type \""+newTypeLabel+"\" lead to an increase of events "
						+ "by a factor of "+(double) numEventsAfter/numEventsBefore+"."
						);
		
//		System.out.println("Finished ocel enhancement using the leading type relaxed strategy for type "+leadingType+".");
		
		return ocel;
	}
	
	public static Graph<String,DefaultEdge> buildObjectGraph (OcelEventLog ocel, OCLPMDiscoveryParameters parameters){
		Graph<String,DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		
		for (String type : parameters.getObjectTypesCaseNotion()) {
			for (OcelObject o : ocel.objectTypes.get(type).objects) {
				graph.addVertex(o.id);
			}
		}
		
		for (OcelEvent event : ocel.getEvents().values()) {
			for (OcelObject o1 : event.relatedObjects) {
				// check if type should be ignored
				if (!parameters.getObjectTypesCaseNotion().contains(o1.objectType.name)) {
					continue;
				}
				for (OcelObject o2 : event.relatedObjects) {
					// check if type should be ignored
					if (!parameters.getObjectTypesCaseNotion().contains(o2.objectType.name)) {
						continue;
					}
					// simple graph cannot contain graph loops or multi-edges 
					// so we don't need to check this here
					// uff, actually have to check because otherwise it stops throwing an exception
					if (!o1.id.equals(o2.id)) {
						graph.addEdge(o1.id, o2.id);						
					}
				}
			}
		}
		System.out.println("Contructed object graph with "+graph.vertexSet().size()+" vertices and "+graph.edgeSet().size()+" edges.");
		return graph;
	}
}
