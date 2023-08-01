package org.processmining.OCLPMDiscovery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.processmining.OCLPMDiscovery.model.additionalinfo.OCLPMAdditionalInfo;
import org.processmining.models.graphbased.NodeID;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.SimpleEvaluationResult;
import org.processmining.placebasedlpmdiscovery.model.Arc;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.TextDescribable;
import org.processmining.placebasedlpmdiscovery.model.Transition;

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
	private HashSet<String> discoveryTypes = new HashSet<String>();
	
	// the object types of places in the model 
	private Set<String> placeTypes = new HashSet<String>();
	
	// evaluation
	private Map<String,Double> evaluation = new HashMap<>();
	private Double combinedScore = -1.0;
	
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
        Set<TaggedPlace> tplaces = new HashSet<TaggedPlace>();
        for (Place p : lpm.getPlaces()) {
        	tplaces.add((TaggedPlace) p);
        }
        this.addAllPlaces(tplaces); // adds also the transitions, places, arcs
        this.setAdditionalInfo(new OCLPMAdditionalInfo(lpm.getAdditionalInfo()));
        
        // store evaluation score from the lpm into the evaluation map
        List<SimpleEvaluationResult> results = lpm.getAdditionalInfo().getEvaluationResult().getResults();
        for (SimpleEvaluationResult result : results) {
        	switch (result.getId()) {
        		case FittingWindowsEvaluationResult:
        			this.evaluation.put("Fitting Window Score",result.getNormalizedResult());
        			break;
        		case TransitionOverlappingEvaluationResult:
        			break;
        		case TransitionCoverageEvaluationResult:
        			this.evaluation.put("Transition Coverage Score",result.getNormalizedResult());
        			break;
        		case PassageCoverageEvaluationResult:
        			this.evaluation.put("Passage Coverage Score",result.getNormalizedResult());
        			break;
        		case PassageRepetitionEvaluationResult:
        			this.evaluation.put("Passage Repetition Score",result.getNormalizedResult());
        			break;
        		case TraceSupportEvaluationResult:
        			break;
        		default:
        			break;
        	}
        }
    }

    public ObjectCentricLocalProcessModel(TaggedPlace place) {
        this();
        this.addPlace(place);
    }
    
	public ObjectCentricLocalProcessModel(LocalProcessModel lpm, String discoveryType) {
		this(lpm);
		this.discoveryTypes.add(discoveryType);
	}
	
	/*
	 * Create copy of the given oclpm where the places can be swapped without changing the original.
	 */
	public ObjectCentricLocalProcessModel (ObjectCentricLocalProcessModel oclpm) {
		this();
		this.lpm = oclpm.getLpm();
		this.id = lpm.getId();
		this.addAllPlaces(oclpm.getPlaces());
		this.setAdditionalInfo(oclpm.getAdditionalInfo());
		this.setDiscoveryTypes(oclpm.getDiscoveryTypes());
		for (String key : oclpm.getEvaluation().keySet()) {
			this.evaluation.put(key, oclpm.getEvaluation().get(key));
		}
	}

	public HashSet<String> getDiscoveryTypes() {
		return discoveryTypes;
	}
	
	public void setDiscoveryTypes(HashSet<String> types) {
		this.discoveryTypes=types;
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
    
    /**
     * Finds all transitions that don't have output arcs with any place in the LPM
     * @return transitions for which there is no place with output arc toward them
     */
    public Set<String> getInputTransitionLabels() {
        Set<String> res = new HashSet<>();
        for (Transition transition : transitions.values()) {
            boolean is_input = true;
            for (Place place : places) {
                if (place.isOutputTransition(transition))
                    is_input = false;
            }
            if (is_input)
                res.add(transition.getLabel());
        }
        return res;
    }
    
    /**
     * Finds all transitions that don't have input arcs with any place in the LPM
     * @return transitions for which there is no place with input arc toward them
     */
    public Set<String> getOutputTransitionLabels() {
    	Set<String> res = new HashSet<>();
        for (Transition transition : transitions.values()) {
            boolean is_output = true;
            for (Place place : places) {
                if (place.isInputTransition(transition))
                    is_output = false;
            }
            if (is_output)
                res.add(transition.getLabel());
        }
        return res;
    }

    public OCLPMAdditionalInfo getAdditionalInfo() {
        return this.additionalInfo;
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
    	addPlace(place, false);
    }

    public void addPlace(TaggedPlace place, Boolean keepEvaluation) {
        if (place == null)
            throw new IllegalArgumentException("The place to be added should not be null: " + place);
        if (this.containsPlace(place))
            return;

        places.add(place);
        this.placeTypes.add(place.getObjectType());

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
        if (!keepEvaluation) {
	        this.additionalInfo.clearEvaluation();
        }

    }
    
    public void addAllPlaces(Set<TaggedPlace> places, Boolean keepEvaluation) {
        for (TaggedPlace place : places)
            this.addPlace(place, keepEvaluation);
    }

    public void addAllPlaces(Set<TaggedPlace> places) {
        for (TaggedPlace place : places)
            this.addPlace(place);
    }
    
    public void deletePlaces(Set<TaggedPlace> places) {
    	deletePlaces(places,false);
    }
    
    public void deletePlaces(Set<TaggedPlace> places, Boolean skipTransitionCheck) {
    	if (places == null) {
    		return;
    	}
    	if (places.isEmpty()) {
    		return;
    	}
    	
		for (TaggedPlace place : places) {
			// remove place
			this.places.remove(place);
			
			// remove arcs
			Set<Arc> deleteArcs = new HashSet<>();
			for (Arc arc : this.arcs) {
				if (arc.getPlace().equals(place)) {
					deleteArcs.add(arc);
				}
			}
			this.arcs.removeAll(deleteArcs);
		}
		
		// recalculate object types
		this.placeTypes.clear();
		for (TaggedPlace p : this.places) {
			this.placeTypes.add(p.getObjectType());
		}
		
		// recalculate transitions
		if (!skipTransitionCheck) {
		Set<String> deleteTransitions = new HashSet<>();
		for (Transition transition : this.transitions.values()) {
			boolean found = false;
			for (TaggedPlace place : this.places) {
				if (place.getInputTransitions().contains(transition)
						|| place.getOutputTransitions().contains(transition)) {
					found = true;
					break;
				}
			}
			if (!found) {
				deleteTransitions.add(transition.getLabel());
			}
		}
		for (String t : deleteTransitions) {
			this.transitions.remove(t);
		}
		}
		
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

	/**
	 * Check if the OCLPMs have equal places (ignoring object type of places and variable arcs)
	 * @param oclpm2
	 * @return
	 */
	public boolean isIsomorphic(ObjectCentricLocalProcessModel oclpm2) {
		if (this.getPlaces().size() != oclpm2.getPlaces().size()) {
			return false;
		}
		Boolean found = false;
		for (TaggedPlace p1 : this.getPlaces()) {
			for (TaggedPlace p2 : oclpm2.getPlaces()) {
				if (p1.isIsomorphic(p2)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
			found = false;
		}
		return true;
	}

	/**
	 *  the object types of places in the model
	 * @return
	 */
	public Set<String> getPlaceTypes() {
		return this.placeTypes;
	}

	/**
	 * Check if the OCLPMs have equal places (ignoring only variable arcs)
	 * @param oclpm2
	 * @return
	 */
	public boolean isEqual(ObjectCentricLocalProcessModel oclpm2) {
		if (this.getPlaces().size() != oclpm2.getPlaces().size()) {
			return false;
		}
		Boolean found = false;
		for (TaggedPlace p1 : this.getPlaces()) {
			for (TaggedPlace p2 : oclpm2.getPlaces()) {
				if (p1.isEqual(p2)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
			found = false;
		}
		return true;
	}

	public void trimVariableArcSet() {
		for (TaggedPlace tp : this.places) {
			tp.trimVariableArcSet();
		}
		
	}

	/**
	 * add special places for starting and ending transitions
	 * @param startingActivities
	 * @param endingActivities
	 */
	public void addExternalObjectFlow(Map<String, Set<String>> startingActivities, Map<String, Set<String>> endingActivities) {
		
		// check current flow situation
		HashMap<List<String>,Set<TaggedPlace>> transitionMap = new HashMap<>(); // maps transitionName,TypeName,"in"/"out" -> TaggedPlaces
		for (TaggedPlace tp : this.getPlaces()) {
			for (Transition t : tp.getInputTransitions()) {
				String[] key = new String[3];
				key[0] = t.getLabel();
				key[1] = tp.getObjectType();
				key[2] = "out"; // this transition is input to a place
				if (transitionMap.containsKey(Arrays.asList(key))) {
					transitionMap.get(Arrays.asList(key)).add(tp);
				}
				else {
					Set<TaggedPlace> value = new HashSet<>();
					value.add(tp);
					transitionMap.put(Arrays.asList(key),value);					
				}
			}
			for (Transition t : tp.getOutputTransitions()) {
				String[] key = new String[3];
				key[0] = t.getLabel();
				key[1] = tp.getObjectType();
				key[2] = "in"; // this transition is output to a place
				if (transitionMap.containsKey(Arrays.asList(key))) {
					transitionMap.get(Arrays.asList(key)).add(tp);
				}
				else {
					Set<TaggedPlace> value = new HashSet<>();
					value.add(tp);
					transitionMap.put(Arrays.asList(key),value);					
				}
			}
		}
		
		// add special places for starting transitions
		Set<TaggedPlace> newPlaces = new HashSet<>();
		for (String type : this.getPlaceTypes()) {
			for (String startingActivity : startingActivities.get(type)) {
				// if this starting activity is in the model and there is an arc of the type coming out 
				if(!transitionMap.get(Arrays.asList(new String[] {startingActivity,type,"out"})).isEmpty()) {
					// then add the special starting place
					// if there already is a place "StartingPlace:type" only add the new transitions
					boolean placeExists = false;
					for (TaggedPlace tmp_place : newPlaces) {
						if (tmp_place.getId().equals("StartingPlace:"+type)) {
							placeExists = true;
							tmp_place.addOutputTransition(this.transitions.get(startingActivity));
							break;
						}
					}
					if (!placeExists) {
						TaggedPlace p = new TaggedPlace("StartingPlace:"+type);
						p.setObjectType(type);
						p.addOutputTransition(this.transitions.get(startingActivity));
						newPlaces.add(p);
					}
				}
			}
			// same for ending activities
			for (String endingActivity : startingActivities.get(type)) {
				// if this starting activity is in the model and there is an arc of the type coming out 
				if(!transitionMap.get(Arrays.asList(new String[] {endingActivity,type,"in"})).isEmpty()) {
					// then add the special starting place
					// if there already is a place "EndingPlace:type" only add the new transitions
					boolean placeExists = false;
					for (TaggedPlace tmp_place : newPlaces) {
						if (tmp_place.getId().equals("EndingPlace:"+type)) {
							placeExists = true;
							tmp_place.addInputTransition(this.transitions.get(endingActivity));
							break;
						}
					}
					if (!placeExists) {
						TaggedPlace p = new TaggedPlace("EndingPlace:"+type);
						p.setObjectType(type);
						p.addInputTransition(this.transitions.get(endingActivity));
						newPlaces.add(p);
					}
				}
			}
		}
		// variable arcs of starting and ending places
		for (TaggedPlace newPlace : newPlaces) { // for all new places
			for (TaggedPlace p : this.getPlaces()) { // for all existing places
				if (!p.getObjectType().equals(newPlace.getObjectType())) break; // same type?
				for (Transition t : Stream.concat( // iterate over all transitions of the new place
						newPlace.getInputTransitions().stream(),
						newPlace.getOutputTransitions().stream())
							.collect(Collectors.toSet())) {
						if (p.getVariableArcActivities().contains(t.getLabel())) {
							newPlace.getVariableArcActivities().add(t.getLabel()); // add variable arc activity to the new place
						}
				}
			}
		}
		this.addAllPlaces(newPlaces);
		
	}

	public void removeExternalObjectFlow(Map<String, Set<String>> startingActivities, Map<String, Set<String>> endingActivities) {
		// Remove the special places for starting and ending transitions
		Set<TaggedPlace> deletePlaces = new HashSet<>();
		for (TaggedPlace p : this.getPlaces()) {
			if (p.getId().contains("StartingPlace") || p.getId().contains("EndingPlace")) {
				deletePlaces.add(p);
			}
		}
		this.deletePlaces(deletePlaces);
	}

	/**
	 * String consisting of all evaluation metrics to be displayed for this OCLPM
	 * @return
	 */
	public String getEvaluationString() {
		String evalString = "";
		for (String name : this.evaluation.keySet()) {
			Double score = Math.round(this.evaluation.get(name)*1000.0)/1000.0;
			evalString += name+": "+score+"\n";
		}
		evalString += "Combined Score: "+Math.round(this.combinedScore*1000.0)/1000.0+"\n";
		return evalString;
	}
	
	/**
	 * Calculates evaluation metric which are dependent on the exact places and variable arcs.
	 * Calculates the combined score as the average of all scores. 
	 */
	public void recalculateEvaluation() {
		// TODO calculate evaluation which is dependent on the placecompletion
		
		// calculate combined score
		Double combinedScore = 0.0;
		for (Double score : this.evaluation.values()) {
			combinedScore+=score;
		}
		combinedScore = combinedScore / this.evaluation.size();
		this.combinedScore = combinedScore;
	}

	public Map<String,Double> getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Map<String,Double> evaluation) {
		this.evaluation = evaluation;
	}

	public Double getCombinedScore() {
		return combinedScore;
	}

	public void setCombinedScore(Double combinedScore) {
		this.combinedScore = combinedScore;
	}

}
