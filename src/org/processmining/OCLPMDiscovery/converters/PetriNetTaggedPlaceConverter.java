package org.processmining.OCLPMDiscovery.converters;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.Transition;
import org.processmining.placebasedlpmdiscovery.placediscovery.converters.place.AbstractPlaceConverter;
import org.processmining.placebasedlpmdiscovery.utils.TransitionUtils;


public class PetriNetTaggedPlaceConverter extends AbstractPlaceConverter<AcceptingPetriNet> {
	private String tag = "";
	
	public PetriNetTaggedPlaceConverter(String tag) {
		super();
		this.tag = tag;
	}
	
    @Override
    /**
     * Converts Petri net into set of places while tagging them by altering the place id (tag:randomid)
     * @param result Input Petri net.
     * @return set of places
     */
    public Set<Place> convert(AcceptingPetriNet result) {
    	Set<Place> discoveredPlaces = new HashSet<>();
        // create transitions
        Set<Transition> transitions = new HashSet<>();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Transition t : result.getNet().getTransitions()) {
            String label = t.getLabel();
            if (t.isInvisible())
                label = t.getLabel() + "-" + t.getId().toString();
            transitions.add(new Transition(label, t.isInvisible()));
        }
        Map<String, Transition> transitionMap = TransitionUtils.mapLabelsIntoTransitions(transitions);


        Collection<org.processmining.models.graphbased.directed.petrinet.elements.Place> pnPlaces = result.getNet().getPlaces();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Place pnPlace : pnPlaces) { // for every place
            Place place = new Place(tag+":"+UUID.randomUUID().toString()); // create a place
            // the UUID contains only numbers, letters and "-". Hence, ":" can be used for splitting later on

            // add all input transitions
            result.getNet().getInEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getSource())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addInputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addInputTransition(transitionMap.get(transition.getLabel()));
                    });

            // add all output transitions
            result.getNet().getOutEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getTarget())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addOutputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addOutputTransition(transitionMap.get(transition.getLabel()));
                    });
            discoveredPlaces.add(place);
        }
//        PlaceUtils.print(discoveredPlaces);
        return discoveredPlaces;
    }
    
    /**
     * Converts Petri net into set of places and returns a map saving for each place id the corresponding tag
     * @param result Input Petri net.
     * @return {Set<Place>, HashMap<String,String>}
     */
    public Object[] convertWithMap(AcceptingPetriNet result) {
    	Set<Place> discoveredPlaces = new HashSet<>();
    	HashMap<String,String> map = new HashMap<String,String>();
        // create transitions
        Set<Transition> transitions = new HashSet<>();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Transition t : result.getNet().getTransitions()) {
            String label = t.getLabel();
            if (t.isInvisible())
                label = t.getLabel() + "-" + t.getId().toString();
            transitions.add(new Transition(label, t.isInvisible()));
        }
        Map<String, Transition> transitionMap = TransitionUtils.mapLabelsIntoTransitions(transitions);


        Collection<org.processmining.models.graphbased.directed.petrinet.elements.Place> pnPlaces = result.getNet().getPlaces();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Place pnPlace : pnPlaces) { // for every place
            Place place = new Place(); // create a place
            map.put(place.getId(),this.tag);

            // add all input transitions
            result.getNet().getInEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getSource())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addInputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addInputTransition(transitionMap.get(transition.getLabel()));
                    });

            // add all output transitions
            result.getNet().getOutEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getTarget())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addOutputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addOutputTransition(transitionMap.get(transition.getLabel()));
                    });
            discoveredPlaces.add(place);
        }
//        PlaceUtils.print(discoveredPlaces);
        return new Object[] {discoveredPlaces, map};
    }
    
    //! this (currently) doesn't work, casting the subclass TaggedPlace to the superclass Place isn't accepted by the LPM Miner
    // It wouldn't work with the current LPM miner anyway because it creates new places and copies the attributes. Hence, the subclass attributes would get lost.
    /**
     * Converts Petri net to places and also tags the places but casts to superclass Place
     * @param result input petri net
     * @return
     */
    public Set<Place> convertCasted(AcceptingPetriNet result) {
        Set<Place> discoveredPlaces = new HashSet<>();
        // create transitions
        Set<Transition> transitions = new HashSet<>();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Transition t : result.getNet().getTransitions()) {
            String label = t.getLabel();
            if (t.isInvisible())
                label = t.getLabel() + "-" + t.getId().toString();
            transitions.add(new Transition(label, t.isInvisible()));
        }
        Map<String, Transition> transitionMap = TransitionUtils.mapLabelsIntoTransitions(transitions);


        Collection<org.processmining.models.graphbased.directed.petrinet.elements.Place> pnPlaces = result.getNet().getPlaces();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Place pnPlace : pnPlaces) { // for every place
            Place place = new TaggedPlace(this.tag); // create a place
//            Place place = new Place(); // create a place

            // add all input transitions
            result.getNet().getInEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getSource())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addInputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addInputTransition(transitionMap.get(transition.getLabel()));
                    });

            // add all output transitions
            result.getNet().getOutEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getTarget())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addOutputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addOutputTransition(transitionMap.get(transition.getLabel()));
                    });
            discoveredPlaces.add(place);
        }
//        PlaceUtils.print(discoveredPlaces);
        return discoveredPlaces;
    }
    
    /**
     * Converts Petri net to places without tagging
     * @param result input petri net
     * @return
     */
    public Set<Place> convertNoTagging(AcceptingPetriNet result) {
        Set<Place> discoveredPlaces = new HashSet<>();
        // create transitions
        Set<Transition> transitions = new HashSet<>();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Transition t : result.getNet().getTransitions()) {
            String label = t.getLabel();
            if (t.isInvisible())
                label = t.getLabel() + "-" + t.getId().toString();
            transitions.add(new Transition(label, t.isInvisible()));
        }
        Map<String, Transition> transitionMap = TransitionUtils.mapLabelsIntoTransitions(transitions);


        Collection<org.processmining.models.graphbased.directed.petrinet.elements.Place> pnPlaces = result.getNet().getPlaces();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Place pnPlace : pnPlaces) { // for every place
            Place place = new Place(); // create a place

            // add all input transitions
            result.getNet().getInEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getSource())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addInputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addInputTransition(transitionMap.get(transition.getLabel()));
                    });

            // add all output transitions
            result.getNet().getOutEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getTarget())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addOutputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addOutputTransition(transitionMap.get(transition.getLabel()));
                    });
            discoveredPlaces.add(place);
        }
        return discoveredPlaces;
    }
    
    public Set<TaggedPlace> convertToTagged(AcceptingPetriNet result) {
        Set<TaggedPlace> discoveredTaggedPlaces = new HashSet<>();
        Set<Place> discoveredPlaces = new HashSet<>();
        // create transitions
        Set<Transition> transitions = new HashSet<>();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Transition t : result.getNet().getTransitions()) {
            String label = t.getLabel();
            if (t.isInvisible())
                label = t.getLabel() + "-" + t.getId().toString();
            transitions.add(new Transition(label, t.isInvisible()));
        }
        Map<String, Transition> transitionMap = TransitionUtils.mapLabelsIntoTransitions(transitions);


        Collection<org.processmining.models.graphbased.directed.petrinet.elements.Place> pnPlaces = result.getNet().getPlaces();
        for (org.processmining.models.graphbased.directed.petrinet.elements.Place pnPlace : pnPlaces) { // for every place
            TaggedPlace place = new TaggedPlace(this.tag); // create a place

            // add all input transitions
            result.getNet().getInEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getSource())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addInputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addInputTransition(transitionMap.get(transition.getLabel()));
                    });

            // add all output transitions
            result.getNet().getOutEdges(pnPlace)
                    .stream()
                    .map(edge -> (org.processmining.models.graphbased.directed.petrinet.elements.Transition) edge.getTarget())
                    .forEach(transition -> {
                        if (transition.isInvisible())
                            place.addOutputTransition(transitionMap.get(transition.getLabel() + "-" + transition.getId().toString()));
                        else
                            place.addOutputTransition(transitionMap.get(transition.getLabel()));
                    });
            discoveredTaggedPlaces.add(place);
            discoveredPlaces.add(place);
        }
//        PlaceUtils.print(discoveredPlaces);
        return discoveredTaggedPlaces;
    }
    
    public Set<Place> convert(AcceptingPetriNet result, String tag){
    	this.setTag(tag);
    	return this.convert(result);
    }

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
