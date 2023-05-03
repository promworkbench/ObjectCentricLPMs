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
import org.processmining.specpp.config.parameters.ExecutionParameters;
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
public class SPECppMiner { //! not functional
//	public static Petrinet minePetrinet(UIPluginContext context, XLog log, SPECppParameters specppParameters) {
//		// configs
//		// TODO add artificial start and end transitions?
//		PreProcessingParameters preProcessingParameters = new PreProcessingParameters(new XEventNameClassifier(),false);
//		DataExtractionParameters dataExtractionParameters = DataExtractionParameters.getDefault();
//		InputProcessingConfig inputProcessingConfig = ConfigFactory.create(preProcessingParameters, dataExtractionParameters);
//		ProMConfig proMConfig = ProMConfig.getLightweight(); // TODO make own config
//		ProMSPECppConfig promSpecppConfig = new ProMSPECppConfig(inputProcessingConfig, proMConfig);
//		
//		// setup controllers
//		ConfiguredSPECppSession configuredSpecppSession = new ConfiguredSPECppSession(log, promSpecppConfig);
//		System.out.println("Created SPECppSession");
//		SpecppControllerNoUI specppController = new SpecppControllerNoUI(context, configuredSpecppSession);
//		System.out.println("Created SPECppController");
//		DiscoveryController discoveryController = new DiscoveryController(specppController);
//		System.out.println("Created DiscoveryController");
//		
//		// get result
//		Petrinet petrinet = discoveryController.getSpecpp().getPostProcessedResult().getNet();
//		System.out.println("Finished");
//		
//		return petrinet;
//	}
	
	// from specpp ProMLessSPECpp.java
	public static Petrinet minePetrinet(XLog log, SPECppParameters specppParameters) {
		InputDataBundle data = InputDataBundle.process(log, specppParameters.getCfg().getInputProcessingConfig());
		SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = SPECpp.build(specppParameters.getCfg(), data);

        ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution;
        try (ExecutionEnvironment ee = new ExecutionEnvironment(Runtime.getRuntime().availableProcessors())) {
            execution = ee.execute(specpp, ExecutionParameters.noTimeouts());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
		
		return execution.getSPECpp().getPostProcessedResult().getNet();
	}
//	
	// from specpp BasicSupervisedSPECpp.java
//	public static Petrinet minePetrinet(UIPluginContext context, XLog log, SPECppParameters specppParameters) {
//
//        SPECppConfigBundle cfg = specppParameters.getCfg();
//        InputDataBundle data = specppParameters.getData();
//        SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = null;
//        try (ExecutionEnvironment ee = new ExecutionEnvironment()) {
//            SPECppOutputtingUtils.preSetup(cfg, data, true);
//            specpp = SPECpp.build(cfg, data);
//            SPECppOutputtingUtils.postSetup(specpp, true);
//            ee.execute(specpp, ExecutionParameters.noTimeouts());
//            SPECppOutputtingUtils.duringExecution(specpp, true);
//        } catch (InterruptedException ignored) {
//        }
//        SPECppOutputtingUtils.postExecution(specpp, true, true, true);
//		
//		return specpp.getPostProcessedResult().getNet();
//	}
	
//	// from specpp CodeDefinedConfigurationSample.java
//	public static Petrinet minePetrinet(UIPluginContext context, XLog log, SPECppParameters specppParameters) {
//      SPECppConfigBundle cfg = specppParameters.getCfg();
//      InputDataBundle data = specppParameters.getData();
//	    try (ExecutionEnvironment ee = new ExecutionEnvironment()) {
//	        ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution = ee.execute(SPECpp.build(cfg, data), ExecutionParameters.noTimeouts());
//	        ee.addCompletionCallback(execution, ex -> {
//	            ProMPetrinetWrapper petrinetWrapper = execution.getSPECpp().getPostProcessedResult();
//	            VizUtils.showVisualization(PetrinetVisualization.of(petrinetWrapper));
//	        });
//	    } catch (InterruptedException ignored) {
//	    }
//	    return null;
//	}
	
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
//		specppParameters.set
		specppParameters.registerParameters();
		return minePetrinet(log, specppParameters);
	}
}
