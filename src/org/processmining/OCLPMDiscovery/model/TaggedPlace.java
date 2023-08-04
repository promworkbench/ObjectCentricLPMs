package org.processmining.OCLPMDiscovery.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.Transition;

public class TaggedPlace extends Place{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3783671545908492239L;
	private String objectType;
	
	public TaggedPlace(String objectType) {
		super();
		this.setObjectType(objectType);
	}
	
	public TaggedPlace(String type, String id) {
		super(id);
		this.setObjectType(type);
	}
	
	public TaggedPlace() {
		super();
	}
	
	// convert place to tagged place
	public TaggedPlace(Place place) {
		super(place.getId());
		for (Transition t : place.getInputTransitions()) {
			this.addInputTransition(t);
		}
		for (Transition t : place.getOutputTransitions()) {
			this.addOutputTransition(t);
		}
		this.setNumTokens(place.getNumTokens());
		this.setAdditionalInfo(place.getAdditionalInfo());
		this.setFinal(place.isFinal());
	}

	/**
	 * Checks if all the transition are equal (doesn't check object-type or variable arcs)
	 * @param tp
	 * @return
	 */
	public Boolean isIsomorphic(TaggedPlace other) {
		if (
				this.getInputTransitions().size() != other.getInputTransitions().size()
				|| this.getOutputTransitions().size() != other.getOutputTransitions().size()
			) {
			return false; // number of transitions doesn't even match
		}
		// check if input transitions are equal
		Boolean found_this = false;
		for (Transition transition_this : this.getInputTransitions()) {
			for (Transition transition_other : other.getInputTransitions()) {
				if (transition_this.equals(transition_other)) {
					found_this = true;
					break; // transition is in both places
				}
			}
			if (!found_this) {
				return false; // found a transition which is not in the other net
			}
			found_this = false;
		}
		/*
		 * At this point all input transitions of this place have been found in the other place.
		 * Here both places have the same number of input transitions.
		 * If we assume that there are no duplicate transitions in this place then the two sets are identical.
		 */
		// check if output transitions are equal
		for (Transition transition_this : this.getOutputTransitions()) {
			for (Transition transition_other : other.getOutputTransitions()) {
				if (transition_this.equals(transition_other)) {
					found_this = true;
					break; // transition is in both places
				}
			}
			if (!found_this) {
				return false; // found a transition which is not in the other net
			}
			found_this = false;
		}
		
		return true;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * Checks if all the transition are equal (doesn't check variable arcs)
	 * @param tp
	 * @return
	 */
	public boolean isEqual(TaggedPlace other) {
		if (
				this.getInputTransitions().size() != other.getInputTransitions().size()
				|| this.getOutputTransitions().size() != other.getOutputTransitions().size()
				|| !(this.getObjectType().equals(other.getObjectType()))
			) {
			return false; // number of transitions doesn't even match or different object type
		}
		// check if input transitions are equal
		Boolean found_this = false;
		for (Transition transition_this : this.getInputTransitions()) {
			for (Transition transition_other : other.getInputTransitions()) {
				if (transition_this.equals(transition_other)) {
					found_this = true;
					break; // transition is in both places
				}
			}
			if (!found_this) {
				return false; // found a transition which is not in the other net
			}
			found_this = false;
		}
		/*
		 * At this point all input transitions of this place have been found in the other place.
		 * Here both places have the same number of input transitions.
		 * If we assume that there are no duplicate transitions in this place then the two sets are identical.
		 */
		// check if output transitions are equal
		for (Transition transition_this : this.getOutputTransitions()) {
			for (Transition transition_other : other.getOutputTransitions()) {
				if (transition_this.equals(transition_other)) {
					found_this = true;
					break; // transition is in both places
				}
			}
			if (!found_this) {
				return false; // found a transition which is not in the other net
			}
			found_this = false;
		}
		
		return true;
	}

	/**
	 * Returns of the given activities only those to which this place is connected.
	 * @param givenActivities
	 * @return
	 */
	public Set<String> getConnectedActivitiesOf(HashSet<String> givenActivities) {
		
		// union of transitions connected to this place
		Set<Transition> connectedTransitions = Stream.concat(this.getInputTransitions().stream(),this.getOutputTransitions().stream()).collect(Collectors.toSet());
		Set<String> connectedActivities = new HashSet<String>();
		
		for (Transition t : connectedTransitions) {
			connectedActivities.add(t.getLabel());
		}
		
		Set<String> intersection = connectedActivities.stream().filter(givenActivities::contains).collect(Collectors.toSet());
		return intersection;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaggedPlace place = (TaggedPlace) o;
        return this.isEqual(place);
    }
}
