package org.processmining.OCLPMDiscovery.plugins.mining;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.parameters.SPECppParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.ExecutionEnvironment;
import org.processmining.specpp.preprocessing.InputDataBundle;

@Plugin(
		name = "SPECpp miner", // not shown anywhere anymore because overwritten by uiLabel?
		parameterLabels = {"Log"},
		returnLabels = { "PetriNet" },
		returnTypes = { Petrinet.class },
		help = "OCEL helper methods from the OCLPMs package."
)
public class SPECppMiner {
	
	// from specpp ProMLessSPECpp.java
	public static Petrinet minePetrinet(XLog log, SPECppParameters specppParameters) {
		InputDataBundle data = InputDataBundle.process(log, specppParameters.getCfg().getInputProcessingConfig());
		SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = SPECpp.build(specppParameters.getCfg(), data);

        ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution;
        try (ExecutionEnvironment ee = new ExecutionEnvironment(Runtime.getRuntime().availableProcessors())) {
            execution = ee.execute(specpp, specppParameters.getExecutionParameters());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
		
		return execution.getSPECpp().getPostProcessedResult().getNet();
	}
	
	@UITopiaVariant(
			affiliation = "RWTH - PADS",
			author = "Marvin Porsil",
			email = "marvin.porsil@rwth-aachen.de",
			uiLabel = "SPECpp mine Petri net from flat log" // name shown in ProM
	)
	@PluginVariant(
			variantLabel = "SPECpp mine Petri net from flat log",
			requiredParameterLabels = {0}
	)
	public static Petrinet minePetrinetPlugin(UIPluginContext context, XLog log) {
		SPECppParameters specppParameters = new SPECppParameters();
		specppParameters.setTau(0.9);
		specppParameters.registerParameters();
		return minePetrinet(log, specppParameters);
	}
}
