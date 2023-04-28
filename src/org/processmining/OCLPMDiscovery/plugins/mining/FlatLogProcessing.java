package org.processmining.OCLPMDiscovery.plugins.mining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.converters.PetriNetTaggedPlaceConverter;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.placebasedlpmdiscovery.model.Place;

public class FlatLogProcessing {
	
	// Discover Petri net, convert to place nets and tag them
	public static Set<Place> processFlatLog (UIPluginContext context, XLog log, String objectType, OCLPMDiscoveryParameters parameters){
		Petrinet petrinet = discoverPetriNet (context, log, parameters);
		Set<Place> placeNets = convertPetriNetToPlaceNets (context, petrinet, objectType);
//		Set<TaggedPlace> taggedPlaceNets = tagPlaceNets (placeNets, objectType);
		return placeNets;
	}

	public static Petrinet discoverPetriNet (UIPluginContext context, XLog log, OCLPMDiscoveryParameters parameters) {
		Petrinet petrinet;
		switch(parameters.getPlaceDiscoveryAlgorithm()) {
			case ILP:
				petrinet = ILPMiner.minePetrinet(context, log, parameters.getIlpParameters());
				break;
			case SPECPP: //TODO not functional!
				petrinet = SPECppMiner.minePetrinet(context, log, parameters.getSpecppParameters());
				break;
			default:
				petrinet = ILPMiner.minePetrinet(context, log, parameters.getIlpParameters());
		}
		
		
		return petrinet;
	}
	
	// tags place nets but casts from TaggedPlace to Place
	public static Set<Place> convertPetriNetToPlaceNets (PluginContext context, Petrinet petrinet, String objectType){
		Marking initialMarking = null;
		List<Marking> finalMarkings = null;
		try {
			initialMarking = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, petrinet)
					.getObjectWithRole(InitialMarkingConnection.MARKING);
			finalMarkings = context.getConnectionManager().getConnections(FinalMarkingConnection.class, context, petrinet)
					.stream()
					.map(c -> (Marking) c.getObjectWithRole(FinalMarkingConnection.MARKING))
					.collect(Collectors.toList());
		} catch (ConnectionCannotBeObtained cannotBeObtained) {
			cannotBeObtained.printStackTrace();
		}
		AcceptingPetriNet acceptingPetriNet = new AcceptingPetriNetImpl(petrinet);
		PetriNetTaggedPlaceConverter converter = new PetriNetTaggedPlaceConverter(objectType);
		return converter.convert(acceptingPetriNet);
	}
	
	public static Set<TaggedPlace> convertPetriNetToTaggedPlaceNets (PluginContext context, Petrinet petrinet, String objectType){
		Marking initialMarking = null;
		List<Marking> finalMarkings = null;
		try {
			initialMarking = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, petrinet)
					.getObjectWithRole(InitialMarkingConnection.MARKING);
			finalMarkings = context.getConnectionManager().getConnections(FinalMarkingConnection.class, context, petrinet)
					.stream()
					.map(c -> (Marking) c.getObjectWithRole(FinalMarkingConnection.MARKING))
					.collect(Collectors.toList());
		} catch (ConnectionCannotBeObtained cannotBeObtained) {
			cannotBeObtained.printStackTrace();
		}
		AcceptingPetriNet acceptingPetriNet = new AcceptingPetriNetImpl(petrinet);
		PetriNetTaggedPlaceConverter converter = new PetriNetTaggedPlaceConverter(objectType);
		return converter.convertToTagged(acceptingPetriNet);
	}
}
