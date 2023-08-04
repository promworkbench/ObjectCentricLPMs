package org.processmining.OCLPMDiscovery.visualization.components;

import java.io.Serializable;

import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.Transition;
import org.processmining.placebasedlpmdiscovery.prom.plugins.visualization.components.tables.TableListener;

public interface WeirdComponentController<T extends Serializable> extends TableListener<T>, ComponentListener {

    void placeSelected(Place p);

    void transitionSelected(Transition t);
}
