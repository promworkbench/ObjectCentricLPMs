package org.processmining.OCLPMDiscovery.utils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;
import org.processmining.ocel.ocelobjects.OcelObjectType;
import org.processmining.ocel.utils.TypeFromValue;

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
	
	/**
	 * Flattens OCEL and stores for all object type columns how many objects they had.
	 * @param ocel
	 * @param caseNotion
	 * @return
	 */
	public static XLog flattenCounting(OcelEventLog ocel, String caseNotion) {
		//TODO test if this works
		XAttributeMap logAttributes = new XAttributeMapImpl();
		XLog log = new XLogImpl(logAttributes);
		for (OcelObject ocelObject : ocel.objectTypes.get(caseNotion).objects) {
			XAttributeMap traceAttributes = new XAttributeMapImpl();
			XAttribute caseId = new XAttributeLiteralImpl("concept:name", ocelObject.id);
			traceAttributes.put("concept:name", caseId);
			XTrace trace = new XTraceImpl(traceAttributes);
			for (OcelEvent ocelEvent : ocelObject.sortedRelatedEvents) {
				XAttributeMap eventAttributes = new XAttributeMapImpl();
				XAttribute eventId = new XAttributeLiteralImpl("event_id", ocelEvent.id);
				eventAttributes.put("event_id", eventId);
				XAttribute conceptName = new XAttributeLiteralImpl("concept:name", ocelEvent.activity);
				eventAttributes.put("concept:name", conceptName);
				XAttribute timeTimestamp = new XAttributeTimestampImpl("time:timestamp", ocelEvent.timestamp);
				eventAttributes.put("time:timestamp", timeTimestamp);
				// Create variables to count objects
				HashMap<String,Long> objectsCounter = new HashMap<>();
				for (OcelObjectType typeObject : ocel.objectTypes.values()) {
					if (typeObject.name.equals(caseNotion)) {
						continue;
					}
					objectsCounter.put(typeObject.name, 0l);
				}
				
				// Count objects
				for (OcelObject object : ocelEvent.relatedObjects) {
					if (object.objectType.name.equals(caseNotion)) {
						continue;
					}
					objectsCounter.merge(object.objectType.name, 1l, Long::sum);
				}
				
				// Store counts in the flattened event
				for (OcelObjectType typeObject : ocel.objectTypes.values()) {
					if (typeObject.name.equals(caseNotion)) {
						continue;
					}
					XAttribute na = new XAttributeDiscreteImpl(typeObject.name, objectsCounter.get(typeObject.name));
					eventAttributes.put(typeObject.name, na);
				}
				for (String attribute : ocelEvent.attributes.keySet()) {
					Object attributeValue = ocelEvent.attributes.get(attribute);
					XAttribute xatt = TypeFromValue.getAttributeForValue(attribute, attributeValue);
					eventAttributes.put(attribute, xatt);
				}
				XEvent event = new XEventImpl(eventAttributes);
				trace.add(event);
			}
			log.add(trace);
		}
		return log;
	}
}
