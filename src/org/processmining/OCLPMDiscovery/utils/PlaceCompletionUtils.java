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
import org.processmining.OCLPMDiscovery.model.TaggedPlaceSet;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.parameters.PlaceCompletion;
import org.processmining.placebasedlpmdiscovery.model.Transition;

public class PlaceCompletionUtils {
	/**
	 * For each place adds potentially adds isomorphic places of different object types, 
	 * depending on the PlaceCompletion strategy chosen.
	 * (Object flow = Tokens of a type entering a transition should also exit)
	 * @param parameters
	 * @param oclpmResult
	 * @return
	 */
	public static OCLPMResult completePlaces (OCLPMDiscoveryParameters parameters, OCLPMResult oclpmResult, TaggedPlaceSet placeSet) {
		return completePlaces(oclpmResult, parameters.getPlaceCompletion());
	}
	
	public static OCLPMResult completePlacesCopy(OCLPMResult result, PlaceCompletion placeCompletion) {
		OCLPMResult newResult = result.copyForPlaceCompletion();
		newResult = completePlaces(newResult, placeCompletion); 
		return newResult;
	}
	
	public static OCLPMResult completePlaces (OCLPMResult oclpmResult, PlaceCompletion placeCompletion) {
		if (placeCompletion == PlaceCompletion.NONE) {
			return oclpmResult;
		}
		
		Main.messageNormal("Starting place completion.");
		
		switch(placeCompletion) {
			
			case ALL:
				// add places of other types to model if: 
				// equal place already is in there
				oclpmResult = completeAll(oclpmResult);
				oclpmResult.deleteDuplicates();
				break;
			
			case BETTERFLOW:
				// add places of other types to model if both: 
				// equal place already is in there 
				// the other types are already present in the net
				oclpmResult = betterFlow2(oclpmResult);
				oclpmResult.deleteDuplicates();
				break;
			
			case FEWVARIABLE:
				// swaps existing places with the places that have the fewest variable arcs
				// delete isomorphic models beforehand as they would result in the same net afterwards
//				oclpmResult.deleteIsomorphic(); // not necessary anymore as we do this before
				oclpmResult = swapToLessThan2VariableArcs(oclpmResult);
				break;
			
			case FEWVARIABLE_BETTERFLOW:
//				oclpmResult.deleteIsomorphic(); // not necessary anymore
				oclpmResult = swapToLessThan2VariableArcs(oclpmResult);
				oclpmResult = betterFlow2(oclpmResult);
				break;
			
			default:
				return oclpmResult;
		}
		
		Main.updateProgress("Finished place completion.");
		oclpmResult.recalculateEvaluation();
		
		return oclpmResult;
	}
	
	/**
	 * swaps existing places with the places that have the fewest variable arcs
	 * @param oclpmResult
	 * @param placeSet
	 * @return
	 */
	public static OCLPMResult swapToFewestVariableArcs(OCLPMResult oclpmResult) {
		for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
			Set<TaggedPlace> deletePlaces = new HashSet<>(oclpm.getPlaces().size());
			Set<TaggedPlace> addPlaces = new HashSet<>(oclpm.getPlaces().size());
			int leastVariable;
			for (TaggedPlace tp : oclpm.getPlaces()) {
				Set<TaggedPlace> tmpPlaceSet = new HashSet<>(oclpm.getPlaces().size());
				leastVariable = oclpm.getVariableArcActivities(tp).size();
				for (TaggedPlace pNet : oclpm.getIsomorphicPlaces()) {
					if (
						!tp.getObjectType().equals(pNet.getObjectType()) // different type
						&& tp.isIsomorphic(pNet) // exactly the same transitions
						&& oclpm.getVariableArcActivities(pNet).size() <= leastVariable // fewer variable arcs
							) {
						if (oclpm.getVariableArcActivities(pNet).size() < leastVariable) {
							leastVariable = oclpm.getVariableArcActivities(pNet).size();
							tmpPlaceSet.clear(); // only add the places with the fewest variable arcs
							deletePlaces.add(tp); // delete place as a "better" one has been found
						}
						// check if place already is in the oclpm
						boolean alreadyInThere = false;
						for (TaggedPlace p2 : oclpm.getPlaces()) {
							if (p2.equals(pNet)) {
								alreadyInThere = true;
								break;
							}
						}
						if (!alreadyInThere) {
							tmpPlaceSet.add(pNet);
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
	 * swaps existing places with the places that have only one or fewer variable arcs
	 * @param oclpmResult
	 * @param placeSet
	 * @return
	 */
	public static OCLPMResult swapToLessThan2VariableArcs(OCLPMResult oclpmResult) { 
		for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
			Set<TaggedPlace> deletePlaces = new HashSet<>(oclpm.getPlaces().size());
			Set<TaggedPlace> addPlaces = new HashSet<>(oclpm.getPlaces().size());
			int leastVariable;
			for (TaggedPlace tp : oclpm.getPlaces()) {
				Set<TaggedPlace> tmpPlaceSet = new HashSet<>(oclpm.getPlaces().size());
				leastVariable = oclpm.getVariableArcActivities(tp).size();
				for (TaggedPlace pNet : oclpm.getIsomorphicPlaces()) {
					if (
						!tp.getObjectType().equals(pNet.getObjectType()) // different type
						&& tp.isIsomorphic(pNet) // exactly the same transitions (the oclpm.isomorphicPlaces contains places isomorphic to any of the places in the oclpm)
						&& (oclpm.getVariableArcActivities(pNet).size() <= leastVariable // fewer variable arcs
						|| oclpm.getVariableArcActivities(pNet).size() <= 1) // only one variable arc
							) {
						if (oclpm.getVariableArcActivities(pNet).size() < leastVariable && leastVariable > 1) {
							leastVariable = oclpm.getVariableArcActivities(pNet).size();
							tmpPlaceSet.clear(); // only add the places with the fewest variable arcs
							deletePlaces.add(tp); // delete place as a "better" one has been found
						}
						// check if place already is in the oclpm
						boolean alreadyInThere = false;
						for (TaggedPlace p2 : oclpm.getPlaces()) {
							if (p2.equals(pNet)) {
								alreadyInThere = true;
								break;
							}
						}
						if (!alreadyInThere) {
							tmpPlaceSet.add(pNet);
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
	public static OCLPMResult betterFlow (OCLPMResult oclpmResult) {
		for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
			Set<TaggedPlace> tmpPlaceSet = new HashSet<>(oclpm.getPlaces().size());
			Set<String> placeTypes = oclpm.getPlaceTypes();
			for (TaggedPlace tp : oclpm.getPlaces()) {
				for (TaggedPlace pNet : oclpm.getIsomorphicPlaces()) {
					if (
						!tp.getObjectType().equals(pNet.getObjectType()) // different type
						&& placeTypes.contains(pNet.getObjectType()) // type already occurs in the oclpm
						&& tp.isIsomorphic(pNet) // exactly the same transitions
							) {
						// check if place already is in the oclpm
						boolean alreadyInThere = false;
						for (TaggedPlace p2 : oclpm.getPlaces()) {
							if (p2.equals(pNet)) {
								alreadyInThere = true;
								break;
							}
						}
						if (!alreadyInThere) {
							tmpPlaceSet.add(pNet);
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
	public static OCLPMResult betterFlow2 (OCLPMResult oclpmResult) {
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
						for (TaggedPlace tmp_place : oclpm.getIsomorphicPlaces()) {
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

	/**
	 * add places of other types to model if: 
	 * equal place already is in there
	 * @param oclpmResult
	 * @param placeSet
	 * @return
	 */
	public static OCLPMResult completeAll (OCLPMResult oclpmResult) {
		for (ObjectCentricLocalProcessModel oclpm : oclpmResult.getElements()) {
			Set<TaggedPlace> tmpPlaceSet = new HashSet<>(oclpm.getPlaces().size());
			// check all isomorphic places from the place set
			for (TaggedPlace pNet : oclpm.getPlaceMapIsomorphic().values()) {
				// if not already in there, add it
				boolean alreadyInThere = false;
				for (TaggedPlace p2 : oclpm.getPlaces()) {
					if (p2.getId().equals(pNet.getId())) { // only works because lpm to oclpm conversion makes sure that ids are equal
						alreadyInThere = true;
						break;
					}
				}
				if (!alreadyInThere) {
					tmpPlaceSet.add(pNet);
				}
			}
			oclpm.addAllPlaces(tmpPlaceSet);
		}
		return oclpmResult;
	}
}
