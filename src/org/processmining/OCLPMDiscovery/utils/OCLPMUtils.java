package org.processmining.OCLPMDiscovery.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetImpl;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.placebasedlpmdiscovery.model.Arc;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.Transition;

public class OCLPMUtils {

	public static AcceptingPetriNet getAcceptingPetriNetRepresentation(ObjectCentricLocalProcessModel oclpm) {

        Petrinet net = OCLPMUtils.getPetriNetRepresentation(oclpm);

        Marking initial = new Marking();
        Marking finalM = new Marking();

        Map<String, org.processmining.models.graphbased.directed.petrinet.elements.Place>
                placeMap = net.getPlaces().stream().collect(Collectors.toMap(AbstractGraphElement::getLabel, x -> x));

        for (Place place : oclpm.getPlaces()) {
            if (place.getNumTokens() > 0)
                initial.add(placeMap.get(place.getId()), place.getNumTokens());
        }

        return new AcceptingPetriNetImpl(net, initial, finalM);
    }

    public static Petrinet getPetriNetRepresentation(ObjectCentricLocalProcessModel oclpm) {
        Petrinet net = PetrinetFactory.newPetrinet(oclpm.getId());

        for (TaggedPlace p : oclpm.getPlaces())
            net.addPlace(p.getId());

        for (Transition t : oclpm.getTransitions()) {
            org.processmining.models.graphbased.directed.petrinet.elements.Transition netTransition = net.addTransition(t.getLabel());
            netTransition.setInvisible(t.isInvisible());
        }

        Map<String, org.processmining.models.graphbased.directed.petrinet.elements.Place>
                placeMap = new HashMap<>();

        Map<String, org.processmining.models.graphbased.directed.petrinet.elements.Transition>
                transitionMap = new HashMap<>();

        try {
//            Map<String, org.processmining.models.graphbased.directed.petrinet.elements.Place>
            placeMap = net.getPlaces().stream().collect(Collectors.toMap(AbstractGraphElement::getLabel, x -> x));
//            Map<String, org.processmining.models.graphbased.directed.petrinet.elements.Transition>
            transitionMap = net.getTransitions().stream().collect(Collectors.toMap(AbstractGraphElement::getLabel, x -> x));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Arc arc : oclpm.getArcs()) {
            if (arc.isInput())
                net.addArc(transitionMap.get(arc.getTransition().getLabel()),
                        placeMap.get(arc.getPlace().getId()));
            else
                net.addArc(placeMap.get(arc.getPlace().getId()),
                        transitionMap.get(arc.getTransition().getLabel()));
        }

        return net;
    }
}
