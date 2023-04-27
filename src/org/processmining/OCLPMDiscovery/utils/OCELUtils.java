package org.processmining.OCLPMDiscovery.utils;
import java.util.HashSet;

import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;
import org.processmining.ocel.ocelobjects.OcelObjectType;

public class OCELUtils {
	/**
	 * Adds new object type with the same object for each event.
	 * @param ocel input ocel
	 * @param typeName name of the new object type
	 * @return OCEL including the dummy object type
	 */
	public static OcelEventLog addDummyCaseNotion(OcelEventLog ocel, String typeName, String objectID) {
		OcelEventLog modifiedOcel = ocel; //TODO do a deep copy?
		
//		// Trying a deep copy:
//		OcelEventLog modifiedOcel = ocel.cloneEmpty(); // sets the "global" stuff
//		// copy all events (also calls clone object())
//		for (OcelEvent eve : ocel.events.values()) {
//			modifiedOcel.cloneEvent(eve);
//		}
//		modifiedOcel.register();

		// prepare new type and object
		OcelObjectType dummyType = new OcelObjectType(modifiedOcel, typeName);
		OcelObject dummyObject = new OcelObject(modifiedOcel);
		dummyObject.objectType = dummyType;
		dummyObject.id = objectID;
		
		// relate object to all events
		dummyObject.relatedEvents = new HashSet<OcelEvent>(modifiedOcel.getEvents().values());
		
		// enter new object and type into ocel
		modifiedOcel.objects.put(dummyObject.id,dummyObject);
		modifiedOcel.objectTypes.put(typeName, dummyType);
		
		// enter object id into every event
		for (String eve : modifiedOcel.events.keySet()) {
			modifiedOcel.events.get(eve).relatedObjectsIdentifiers.add(dummyObject.id);
		}
		
		// OcelEventLog.register does:
		// 1. for each event:
		//		get the related object ids
		//		get the corresponding object from the event log
		//		add object into the event
		//		add event into the object
		// 2. for each object:
		//		add object to object type
		//		sort events
		modifiedOcel.register();
		
		// Hence, to add a new object with new type to every event I need to:
		// - enter object id into each event
		// - enter object with id into ocel
		// - enter object type in object
		// - enter object type into ocel
		
		return modifiedOcel;
	}
}
