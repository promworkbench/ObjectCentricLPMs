package org.processmining.OCLPMDiscovery.visualization.components.tables;

import java.io.Serializable;

import org.processmining.OCLPMDiscovery.visualization.components.ComponentListener;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableCollection;

public interface TableListener<T extends Serializable> extends ComponentListener {

    void newSelection(T selectedObject);

    void export(SerializableCollection<T> collection);
}
