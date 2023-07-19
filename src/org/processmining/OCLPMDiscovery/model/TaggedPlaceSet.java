package org.processmining.OCLPMDiscovery.model;

import java.io.OutputStream;
import java.util.Set;

import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.exporting.Exportable;
import org.processmining.placebasedlpmdiscovery.model.exporting.exporters.Exporter;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableSet;

/**
 * This class is just used to store tagged place sets.
 * Usual PlaceSets are converted to TaggedPlaceSets when exporting.
 * When importing, the TaggedPlaceSets are imported and then converted to PlaceSets.
 * @author Marvin
 *
 */
public class TaggedPlaceSet extends SerializableSet<TaggedPlace> implements Exportable<TaggedPlaceSet> {

    private static final long serialVersionUID = 1645883969214312641L;

    public TaggedPlaceSet() {

    }

    public TaggedPlaceSet(Set<TaggedPlace> places) {
        super(places);
    }
    
    public TaggedPlaceSet(PlaceSet placeSet) {
    	super();
    	for (Place p : placeSet.getElements()) {
    		this.add((TaggedPlace)p);
    	}
    }

    @Override
    public void export(Exporter<TaggedPlaceSet> exporter, OutputStream os) {
        exporter.export(this, os);
    }
}
