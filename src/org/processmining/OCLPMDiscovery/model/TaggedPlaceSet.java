package org.processmining.OCLPMDiscovery.model;

import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.exporting.Exportable;
import org.processmining.placebasedlpmdiscovery.model.exporting.exporters.Exporter;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableSet;

public class TaggedPlaceSet extends SerializableSet<TaggedPlace> implements Exportable<TaggedPlaceSet> {

    private static final long serialVersionUID = 1645883969214312641L;
    private Map<String, Set<String>> startingActivities;
    private Map<String, Set<String>> endingActivities;

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
    
    public PlaceSet asPlaceSet() {
    	PlaceSet placeSet = new PlaceSet();
    	for (TaggedPlace tp : this.getElements()) {
    		placeSet.add(tp);
    	}
    	return placeSet;
    }

	public Map<String, Set<String>> getStartingActivities() {
		return startingActivities;
	}

	public void setStartingActivities(Map<String, Set<String>> startingActivities) {
		this.startingActivities = startingActivities;
	}

	public Map<String, Set<String>> getEndingActivities() {
		return endingActivities;
	}

	public void setEndingActivities(Map<String, Set<String>> endingActivities) {
		this.endingActivities = endingActivities;
	}

}
