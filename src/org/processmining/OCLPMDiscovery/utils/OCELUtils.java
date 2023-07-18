package org.processmining.OCLPMDiscovery.utils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;
import org.processmining.ocel.ocelobjects.OcelObjectType;

public class OCELUtils {
	
	/**
	 * Do a deep copy of the given OCEL.
	 * @param ocel
	 * @return
	 */
	public static OcelEventLog deepCopy (OcelEventLog ocel) {
		OcelEventLog newOcel = ocel.cloneEmpty(); // sets the "global" stuff
		// copy all events (also calls clone object())
		for (OcelEvent eve : ocel.events.values()) {
			newOcel.cloneEvent(eve);
		}
		
		// workaround for a fault in the OCEL standard which pointed the new events to the old log
		for (OcelEvent eve : newOcel.events.values()) {
			eve.eventLog = newOcel;
		}
				
		newOcel.register();
		
		return newOcel;
	}
	
	/**
	 * Adds new object type with the same object for each event.
	 * @param ocel input ocel
	 * @param typeName name of the new object type
	 * @return OCEL including the dummy object type
	 */
	public static OcelEventLog addDummyCaseNotion(OcelEventLog ocel, String typeName, String objectID) {
//		OcelEventLog modifiedOcel = ocel;
		
		// Trying a deep copy:
		OcelEventLog modifiedOcel = ocel.cloneEmpty(); // sets the "global" stuff
		// copy all events (also calls clone object())
		for (OcelEvent eve : ocel.events.values()) {
			modifiedOcel.cloneEvent(eve);
		} 		
		
		/* Why does cloning all events to the new log lead to a Nullpointer exception?
			* Cloning an event sets the log of the events to the old log, not the new one. 
			* Is that a problem? 
			* Yes, if a new object is added to the modified log the ocel.register calls 
			* event.register for each event and then the event fetches the old log, 
			* which doesn't have the new object, resulting in a nullpointer exception
		*/
		
		// workaround: manually set the event.log to the correct one
		for (OcelEvent eve : modifiedOcel.events.values()) {
			eve.eventLog = modifiedOcel;
		}
				
		modifiedOcel.register();
		
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
	
	public static Set<String> getActivities(OcelEventLog ocel) {
		Set<String> activities = new HashSet<String>();
		for (OcelEvent eve : ocel.events.values()) {
			activities.add(eve.activity);
		}
		return activities;
	}
	
	/**
	 * Returns map which maps [Activity,ObjectType] to score (#events of act and |OT|=1 / #events of act)
	 * @param ocel
	 * @param objectTypes There might be types for which we don't want to compute the score
	 * @return
	 */
	public static HashMap<List<String>,Double> computeScore(OcelEventLog ocel, Set<String> objectTypes){
		HashMap<List<String>,Integer> scoreCountingSingles = new  HashMap<>(); // maps [Activity,ObjectType] to #events of activity and |OT|=1
		HashMap<List<String>,Integer> scoreCountingAll = new  HashMap<>(); // maps [Activity,ObjectType] to #events of activity
		HashMap<List<String>,Double> score = new HashMap<>(); // maps [Activity,ObjectType] to score (#events of act and |OT|=1 / #events of act)
		HashMap<String,Integer> scoreCountingTmp = new HashMap<>(); // maps ObjectType to #objects in current event with this type
		String curOT, curAct;
		for (OcelEvent event : ocel.getEvents().values()) {
			curAct = event.activity;
			// count objects present in event
			for (OcelObject object : event.relatedObjects) {
				curOT = object.objectType.name;
				scoreCountingTmp.merge(curOT, 1, Integer::sum); // puts 1 if curOT not present, else adds 1 to it
			}
			for (String type : objectTypes) {
				if (scoreCountingTmp.containsKey(type) && scoreCountingTmp.get(type) == 1) {
					scoreCountingSingles.merge(Arrays.asList(curAct, type), 1, Integer::sum);
				}
				scoreCountingAll.merge(Arrays.asList(curAct, type), 1, Integer::sum);
			}
			scoreCountingTmp.clear();
		}
		
		// compute score from counting
		scoreCountingAll.forEach((key,value) -> {
			if (scoreCountingSingles.containsKey(key)) {
				score.put(key, scoreCountingSingles.get(key)/(double)value);				
			}
			else {
				score.put(key, 0.0);
			}
		});
		return score;
	}
	
	/**
	 * Returns map which maps [Activity,ObjectType] to score (#events of act and |OT|=1 / #events of act)
	 * @param ocel
	 * @param objectType
	 * @return
	 */
	public static HashMap<List<String>,Double> computeScore(OcelEventLog ocel, String objectType){
		return computeScore(ocel, new HashSet<String>(Arrays.asList(objectType)));
	}
}
