package org.processmining.OCLPMDiscovery.visualization.components;

import java.io.Serializable;

import org.processmining.OCLPMDiscovery.visualization.components.tables.TableListener;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.Transition;

public interface WeirdComponentController<T extends Serializable> extends TableListener<T>, ComponentListener {

    void placeSelected(Place p);

    void transitionSelected(Transition t);
}
