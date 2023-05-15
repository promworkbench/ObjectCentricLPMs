package org.processmining.OCLPMDiscovery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.processmining.OCLPMDiscovery.model.additionalinfo.OCLPMAdditionalInfo;
import org.processmining.models.graphbased.NodeID;
import org.processmining.placebasedlpmdiscovery.model.Arc;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.TextDescribable;
import org.processmining.placebasedlpmdiscovery.model.Transition;
import org.processmining.placebasedlpmdiscovery.model.additionalinfo.LPMAdditionalInfo;

/**
 * The ObjectCentricLocalProcessModel class is used to represent the logic for object-centric local process models. It contains places,
 * transitions and arcs between them.
 * Copied from local process models and adjusted to use tagged places.
 */
public class ObjectCentricLocalProcessModel implements Serializable, TextDescribable {
	private static final long serialVersionUID = -1533299531712229854L;
	
	// save original LPM, e.g. for LPMAdditionalInfo
	private LocalProcessModel lpm;
	
	// LPM variables
	private String id;
    private final Set<TaggedPlace> places;
    private final Map<String, Transition> transitions; // label -> transition map
    private final Set<Arc> arcs;
    private OCLPMAdditionalInfo additionalInfo;
	
	// leading types for which this OCLPM has been discovered
	private final HashSet<String> discoveryTypes = new HashSet<String>();;
	
	public ObjectCentricLocalProcessModel() {
        // setup oclpm
		this.id = UUID.randomUUID().toString();
        this.places = new HashSet<>();
        this.transitions = new HashMap<>();
        this.arcs = new HashSet<>();
        // setup additional info
        this.additionalInfo = new OCLPMAdditionalInfo(this);
        
        // setup lpm
        this.lpm = new LocalProcessModel();
    }
	
	public ObjectCentricLocalProcessModel(LocalProcessModel lpm) {
        this();

        this.lpm = lpm;
        
        this.id = lpm.getId();
        //TODO change places to tagged places
        Set<TaggedPlace> tplaces = new HashSet<TaggedPlace>();
        for (Place p : lpm.getPlaces()) {
        	tplaces.add(new TaggedPlace(p));
        }
        this.addAllPlaces(tplaces); // adds also the transitions, places, arcs
        this.setAdditionalInfo(new OCLPMAdditionalInfo(lpm.getAdditionalInfo()));
    }

    public ObjectCentricLocalProcessModel(TaggedPlace place) {
        this();
        this.addPlace(place);
    }
    
	public ObjectCentricLocalProcessModel(LocalProcessModel lpm, String discoveryType) {
		this(lpm);
		this.discoveryTypes.add(discoveryType);
	}

	public HashSet<String> getDiscoveryTypes() {
		return discoveryTypes;
	}
	
	/**
     * Finds all transitions that don't have output arcs with any place in the LPM
     * @return transitions for which there is no place with output arc toward them
     */
    public List<Transition> getInputTransitions() {
        List<Transition> res = new ArrayList<>();
        for (Transition transition : transitions.values()) {
            boolean is_input = true;
            for (Place place : places) {
                if (place.isOutputTransition(transition))
                    is_input = false;
            }
            if (is_input)
                res.add(transition);
        }
        return res;
    }

    /**
     * Finds all transitions that don't have input arcs with any place in the LPM
     * @return transitions for which there is no place with input arc toward them
     */
    public List<Transition> getOutputTransitions() {
        List<Transition> res = new ArrayList<>();
        for (Transition transition : transitions.values()) {
            boolean is_output = true;
            for (Place place : places) {
                if (place.isInputTransition(transition))
                    is_output = false;
            }
            if (is_output)
                res.add(transition);
        }
        return res;
    }

    public LPMAdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(OCLPMAdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Set<Arc> getArcs() {
        return arcs;
    }

    public String getId() {
        return id;
    }

    public Set<TaggedPlace> getPlaces() {
        return places;
    }

    public void addPlace(TaggedPlace place) {
        if (place == null)
            throw new IllegalArgumentException("The place to be added should not be null: " + place);
        if (this.containsPlace(place))
            return;

        places.add(place);

        for (Transition transition : place.getInputTransitions()) {
            Arc arc = new Arc(place, transition, true);
            arcs.add(arc);
            if (!transitions.containsKey(transition.getLabel()))
                transitions.put(transition.getLabel(), transition);
        }

        for (Transition transition : place.getOutputTransitions()) {
            Arc arc = new Arc(place, transition, false);
            arcs.add(arc);
            if (!transitions.containsKey(transition.getLabel()))
                transitions.put(transition.getLabel(), transition);
        }

        this.additionalInfo.clearEvaluation();
    }

    public void addAllPlaces(Set<TaggedPlace> places) {
        for (TaggedPlace place : places)
            this.addPlace(place);
    }

    public void addOCLPM(ObjectCentricLocalProcessModel oclpm) {
        for (TaggedPlace place : oclpm.getPlaces())
            this.addPlace(place);
    }

    public boolean containsPlace(Place place) {
        return this.places.contains(place);
    }

    public boolean containsPlace(Set<String> possibleShortString) {
        for (Place place : this.places)
            if (possibleShortString.contains(place.getShortString()))
                return true;
        return false;
    }

    public boolean containsLPM(LocalProcessModel lpm) {
        for (Place place : lpm.getPlaces())
            if (!this.containsPlace(place))
                return false;

        return true;
    }

    /**
     * Checks whether the LPM and the place have common transitions that can be used in order to add the place
     * in the LPM.
     * @param place: The place for which we check if there are common transitions with the LPM
     * @return true if there are common transitions and false otherwise
     */
    public boolean hasCommonTransitions(Place place) {
        Set<Transition> resSet = new HashSet<>();
        resSet.addAll(place.getInputTransitions());
        resSet.addAll(place.getOutputTransitions());
        resSet.retainAll(this.transitions.values());
        return !resSet.isEmpty();
    }

    public Collection<Transition> getTransitions() {
        return transitions.values();
    }

    public Collection<Transition> getVisibleTransitions() {
        return transitions.values()
                .stream()
                .filter(t -> !t.isInvisible())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LPM: ").append(this.id).append("\n");
        for (Place place : this.places) {
            sb.append(place.getShortString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;

        ObjectCentricLocalProcessModel oclpm = (ObjectCentricLocalProcessModel) obj;
        return this.places.equals(oclpm.places);
    }

    @Override
    public int hashCode() {
        return Objects.hash(places);
    }

    @Override
    public String getShortString() {
        StringBuilder sb = new StringBuilder();
        for (Place place : places)
            sb.append(place.getShortString());
        return sb.toString();
    }

    public Place getPlace(NodeID id) {
        return null;
    }

	public LocalProcessModel getLpm() {
		return lpm;
	}

	public void setLpm(LocalProcessModel lpm) {
		this.lpm = lpm;
	}

}
