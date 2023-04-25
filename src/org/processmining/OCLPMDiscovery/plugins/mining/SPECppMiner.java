package org.processmining.OCLPMDiscovery.plugins.mining;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.specpp.SpecppControllerNoUI;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.specpp.config.ConfigFactory;
import org.processmining.specpp.config.DataExtractionParameters;
import org.processmining.specpp.config.InputProcessingConfig;
import org.processmining.specpp.config.PreProcessingParameters;
import org.processmining.specpp.prom.mvc.config.ProMConfig;
import org.processmining.specpp.prom.mvc.discovery.DiscoveryController;
import org.processmining.specpp.prom.plugins.ConfiguredSPECppSession;
import org.processmining.specpp.prom.plugins.ProMSPECppConfig;

public class SPECppMiner { //! not functional
	public static Petrinet minePetrinet(UIPluginContext context, XLog log) {
		// configs
		// TODO add artificial start and end transitions?
		PreProcessingParameters preProcessingParameters = new PreProcessingParameters(new XEventNameClassifier(),false);
		DataExtractionParameters dataExtractionParameters = DataExtractionParameters.getDefault();
		InputProcessingConfig inputProcessingConfig = ConfigFactory.create(preProcessingParameters, dataExtractionParameters);
		ProMConfig proMConfig = ProMConfig.getLightweight(); // TODO make own config
		ProMSPECppConfig promSpecppConfig = new ProMSPECppConfig(inputProcessingConfig, proMConfig);
		
		// setup controllers
		ConfiguredSPECppSession configuredSpecppSession = new ConfiguredSPECppSession(log, promSpecppConfig);
		System.out.println("Created SPECppSession");
		SpecppControllerNoUI specppController = new SpecppControllerNoUI(context, configuredSpecppSession);
		System.out.println("Created SPECppController");
		DiscoveryController discoveryController = new DiscoveryController(specppController);
		System.out.println("Created DiscoveryController");
		
		// get result
		Petrinet petrinet = discoveryController.getSpecpp().getPostProcessedResult().getNet();
		System.out.println("Finished");
		
		return petrinet;
	}
}
