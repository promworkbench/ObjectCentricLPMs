package org.processmining.OCLPMDiscovery.parameters;

public enum VariableArcIdentification {
	WHOLE_LOG ("Score on whole log"), // Computes score[Activity,ObjectType] on all events
	PER_PLACE ("Score per place"), // Computes the score for each place separately such that only events are considered which might be replayed on the place
	NONE ("Skip discovery"), // skip variable arc identification (in case it has already been done or is not wanted)
	PER_LPM ("Score per LPM") // Future work
	;
	
	private final String name;
	
	VariableArcIdentification(String name){
		this.name=name;
	}

	public String getName() {
		return this.name;
	}
	
	/*
	 * Score on whole log:
	 * For each pair [Activity,ObjectType], so each arc in the Petri net
	 * compute the score using the original ocel with all events.
	 * The score(activity A, ObjectType OT) is basically the fraction of
	 * events with activity A that only have a single object of type OT.
	 * If this score is lower than the threshold (set in the parameters)
	 * then the arc is tagged as variable.
	 */
	
	/*
	 * Score per place:
	 * For each place net:
	 * 		get input activities
	 * 		get output activities
	 * 		get all events of input activities
	 * 		get all objects of the type of the current place which appear in events with input activities
	 * 		get all events of output activities with objects from the input activities
	 * 		get all objects actually occurring in those events of filtered output activities 
	 * 		only keep the input events which include those objects
	 * 		compute score on the filtered log (input events with output objects + output event which have input objects)
	 * 		tag the arcs as variable if score is lower than the threshold
	 * 
	 *	So per place identification considers only events which either 
	 *	produce tokens which can be consumed
	 *	or consume tokens which can be produced in the net.
	 */
}