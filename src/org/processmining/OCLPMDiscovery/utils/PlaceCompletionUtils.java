package org.processmining.OCLPMDiscovery.utils;

import java.util.HashSet;
import java.util.Set;

import org.processmining.OCLPMDiscovery.Main;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.parameters.PlaceCompletion;
import org.processmining.placebasedlpmdiscovery.model.Place;
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
				break;
			
			case BETTERFLOW:
				// add places of other types to model if both: 
				// equal place already is in there 
				// the other types are already present in the net
				oclpmResult = betterFlow(oclpmResult, placeSet);
				break;
			
			case FEWVARIABLE:
				// swaps existing places with the places that have the fewest variable arcs
				oclpmResult = swapToFewestVariableArcs(oclpmResult,placeSet);
				break;
			
			case FEWVARIABLE_BETTERFLOW:
				oclpmResult = swapToFewestVariableArcs(oclpmResult,placeSet);
				oclpmResult = betterFlow(oclpmResult, placeSet);
				break;
			
			default:
				return oclpmResult;
		}
		
		oclpmResult.deleteDuplicates();
		
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
		// TODO delete isomorphic models beforehand as they would result in the same net afterwards
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
		//TODO Improve this such that places are only added if they are necessary to make the places of that object type connected
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
}
