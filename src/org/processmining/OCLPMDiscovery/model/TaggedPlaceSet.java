package org.processmining.OCLPMDiscovery.model;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.exporting.Exportable;
import org.processmining.placebasedlpmdiscovery.model.exporting.exporters.Exporter;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableSet;

public class TaggedPlaceSet extends SerializableSet<TaggedPlace> implements Exportable<TaggedPlaceSet> {

    private static final long serialVersionUID = 1645883969214312641L;
    private Map<String, Set<String>> startingActivities; // object type -> activities
    private Map<String, Set<String>> endingActivities;
    private Set<String> objectTypesPlaceNets; // object types for which the log is flattened and place nets are discovered

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
    
    public TaggedPlaceSet(TaggedPlaceSet placeSet) {
    	super();
    	for (TaggedPlace p : placeSet.getElements()) {
    		this.add(p);
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

	public Set<String> getTypes() {
		return this.objectTypesPlaceNets;
	}
	
	public void setTypes(Set<String> types) {
		this.objectTypesPlaceNets = types;
	}

	public void removeIsomorphic() {
        Set<TaggedPlace> uniqueRepresentations = new HashSet<>();
        Set<TaggedPlace> toRemove = new HashSet<>();

        for (TaggedPlace current : this.getElements()) {
            boolean isDuplicate = false;

            for (TaggedPlace unique : uniqueRepresentations) {
                if (current.isIsomorphic(unique)) {
                    toRemove.add(current);
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                uniqueRepresentations.add(current);
            }
        }

        for (TaggedPlace tp : toRemove) {
        	this.remove(tp);
        }
    }

}
