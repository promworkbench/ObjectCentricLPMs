package org.processmining.OCLPMDiscovery.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.parameters.PlaceCompletion;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.Transition;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;

public class PlaceCompletionUtils {
	/**
	 * For each place adds potentially adds isomorphic places of different object types, 
	 * depending on the PlaceCompletion strategy chosen.
	 * (Object flow = Tokens of a type entering a transition should also exit)
	 * @param parameters
	 * @param oclpmResult
	 * @return
	 */
	public static OCLPMResult completePlaces (OCLPMDiscoveryParameters parameters, OCLPMResult oclpmResult, PlaceSet placeSet) {
		if (parameters.getPlaceCompletion() == PlaceCompletion.NONE) {
			return oclpmResult;
		}
		
		Main.messageNormal("Starting place completion.");
		
		switch(parameters.getPlaceCompletion()) {
			
			case ALL:
				// add places of other types to model if: 
				// equal place already is in there
				for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
					Set<TaggedPlace> tmpPlaceSet = new HashSet<>(oclpm.getPlaces().size());
					Set<String> placeTypes = oclpm.getPlaceTypes();
					for (TaggedPlace tp : oclpm.getPlaces()) {
						for (Place pNet : placeSet.getElements()) {
							if (
								!tp.getObjectType().equals(((TaggedPlace) pNet).getObjectType()) // different type
								&& tp.isIsomorphic((TaggedPlace)pNet) // exactly the same transitions
									) {
								// check if place already is in the oclpm
								boolean alreadyInThere = false;
								for (TaggedPlace p2 : oclpm.getPlaces()) {
									if (p2.equals((TaggedPlace)pNet)) {
										alreadyInThere = true;
										break;
									}
								}
								if (!alreadyInThere) {
									tmpPlaceSet.add((TaggedPlace)pNet);
								}
							}
						}
					}
					oclpm.addAllPlaces(tmpPlaceSet);
				}
				
				oclpmResult.deleteDuplicates();
				break;
			
			case BETTERFLOW:
				// add places of other types to model if both: 
				// equal place already is in there 
				// the other types are already present in the net
				oclpmResult = betterFlow2(oclpmResult, placeSet);
				oclpmResult.deleteDuplicates();
				break;
			
			case FEWVARIABLE:
				// swaps existing places with the places that have the fewest variable arcs
				// delete isomorphic models beforehand as they would result in the same net afterwards
				oclpmResult.deleteIsomorphic();
				oclpmResult = swapToFewestVariableArcs(oclpmResult,placeSet);
				break;
			
			case FEWVARIABLE_BETTERFLOW:
				oclpmResult.deleteIsomorphic();
				oclpmResult = swapToFewestVariableArcs(oclpmResult,placeSet);
				oclpmResult = betterFlow2(oclpmResult, placeSet);
				break;
			
			default:
				return oclpmResult;
		}
		
		Main.updateProgress("Finished place completion.");
		
		return oclpmResult;
	}
	
	/**
	 * swaps existing places with the places that have the fewest variable arcs
	 * @param oclpmResult
	 * @param placeSet
	 * @return
	 */
	public static OCLPMResult swapToFewestVariableArcs(OCLPMResult oclpmResult, PlaceSet placeSet) {
		for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
			Set<TaggedPlace> deletePlaces = new HashSet<>(oclpm.getPlaces().size());
			Set<TaggedPlace> addPlaces = new HashSet<>(oclpm.getPlaces().size());
			Set<String> placeTypes = oclpm.getPlaceTypes();
			int leastVariable;
			for (TaggedPlace tp : oclpm.getPlaces()) {
				Set<TaggedPlace> tmpPlaceSet = new HashSet<>(oclpm.getPlaces().size());
				leastVariable = tp.getVariableArcActivities().size();
				for (Place pNet : placeSet.getElements()) {
					if (
						!tp.getObjectType().equals(((TaggedPlace) pNet).getObjectType()) // different type
						&& tp.isIsomorphic((TaggedPlace)pNet) // exactly the same transitions
						&& ((TaggedPlace) pNet).getVariableArcActivities().size() <= leastVariable // fewer variable arcs
							) {
						if (((TaggedPlace) pNet).getVariableArcActivities().size() < leastVariable) {
							leastVariable = ((TaggedPlace) pNet).getVariableArcActivities().size();
							tmpPlaceSet.clear(); // only add the places with the fewest variable arcs
							deletePlaces.add(tp); // delete place as a "better" one has been found
						}
						// check if place already is in the oclpm
						boolean alreadyInThere = false;
						for (TaggedPlace p2 : oclpm.getPlaces()) {
							if (p2.equals((TaggedPlace)pNet)) {
								alreadyInThere = true;
								break;
							}
						}
						if (!alreadyInThere) {
							tmpPlaceSet.add((TaggedPlace)pNet);
						}
					}
				}
				addPlaces.addAll(tmpPlaceSet);
			}
			oclpm.deletePlaces(deletePlaces, true);
			oclpm.addAllPlaces(addPlaces, true);
		}
		return oclpmResult;
	}
	
	/**
	 * add places of other types to model if both: 
	 * 		equal place already is in there
	 * 		the other types are already present in the net
	 * @param oclpmResult
	 * @param placeSet
	 * @return
	 */
	public static OCLPMResult betterFlow (OCLPMResult oclpmResult, PlaceSet placeSet) {
		for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
			Set<TaggedPlace> tmpPlaceSet = new HashSet<>(oclpm.getPlaces().size());
			Set<String> placeTypes = oclpm.getPlaceTypes();
			for (TaggedPlace tp : oclpm.getPlaces()) {
				for (Place pNet : placeSet.getElements()) {
					if (
						!tp.getObjectType().equals(((TaggedPlace) pNet).getObjectType()) // different type
						&& placeTypes.contains(((TaggedPlace) pNet).getObjectType()) // type already occurs in the oclpm
						&& tp.isIsomorphic((TaggedPlace)pNet) // exactly the same transitions
							) {
						// check if place already is in the oclpm
						boolean alreadyInThere = false;
						for (TaggedPlace p2 : oclpm.getPlaces()) {
							if (p2.equals((TaggedPlace)pNet)) {
								alreadyInThere = true;
								break;
							}
						}
						if (!alreadyInThere) {
							tmpPlaceSet.add((TaggedPlace)pNet);
						}
					}
				}
			}
			oclpm.addAllPlaces(tmpPlaceSet);
		}
		return oclpmResult;
	}
	
	/**
	 * add place of other types to model if:
	 * 		transition isn't source or sink
	 * 		transition has that type only as input or output
	 * Only adds places that are isomorphic to those already in the model
	 * Only checks if flow is completely broken (arcs go in nothing goes out),
	 * not if flow inbalanced (e.g., 2 in 1 out)
	 * @param oclpmResult
	 * @param placeSet
	 * @return
	 */
	public static OCLPMResult betterFlow2 (OCLPMResult oclpmResult, PlaceSet placeSet) {		
		for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
			Set<String> outputTransitionLabels = oclpm.getOutputTransitionLabels();
			Set<String> inputTransitionLabels = oclpm.getInputTransitionLabels();
			
			boolean addedSomething = true;
			
			while (addedSomething) {
				addedSomething = false;

				// check current flow situation
				HashMap<List<String>,Set<TaggedPlace>> transitionMap = new HashMap<>(); // maps transitionName,TypeName,"in"/"out" -> TaggedPlaces
				for (TaggedPlace tp : oclpm.getPlaces()) {
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
				
				for (List<String> keylist : transitionMap.keySet()) {
					String [] oppositeKey = new String[3];
					oppositeKey[0] = keylist.get(0);
					oppositeKey[1] = keylist.get(1);
					oppositeKey[2] = "out";
					List<String> oppositeKeyList = Arrays.asList(oppositeKey);
					if (keylist.get(2).equals("out")) {
						oppositeKey[2] = "in";
					}
					if (!transitionMap.containsKey(oppositeKeyList) // flow preservation unfulfilled
							&& !inputTransitionLabels.contains(keylist.get(0)) // transition not source
							&& !outputTransitionLabels.contains(keylist.get(0)) // transition not sink
							) { 
						// search for places in the oclpm that fulfill transition and direction but not type
						Set<TaggedPlace> wrongTypePlaces = new HashSet<>();
						for (String type : oclpm.getPlaceTypes()) {
							if (keylist.get(1).equals(type)) {
								continue; // correct type, which the net doesn't have
							}
							Set<TaggedPlace> tmp_places = transitionMap.get(
									Arrays.asList(new String []{keylist.get(0),type,oppositeKey[2]}));
							if (tmp_places == null || tmp_places.isEmpty()) {
								continue;
							}
							wrongTypePlaces.addAll(tmp_places);
						}
						
						if (wrongTypePlaces.isEmpty()) break;
						
						// search for an isomorphic place with the correct type in the PlaceSet
						for (Place p : placeSet.getElements()) {
							TaggedPlace tmp_place = (TaggedPlace) p;
							if (tmp_place.getObjectType().equals(keylist.get(1)) ){ // type correct
								// check if isomorphic to some place in wrongTypePlaces
								for (TaggedPlace wtp : wrongTypePlaces) {
									if (wtp.isIsomorphic(tmp_place)) { // found place that improves flow preservation
										oclpm.addPlace(tmp_place);
										addedSomething = true;
										break;
									}
								}
								if (addedSomething) {
									break;
								}
							}	
						}
					}
					if (addedSomething) {
						break;
					}
				}
			} // while addedSomething
		}
		return oclpmResult;
	}
}
