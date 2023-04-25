package org.processmining.OCLPMDiscovery.specpp;

import java.util.Set;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.specpp.config.InputProcessingConfig;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.prom.plugins.SPECppSession;

public class SpecppControllerNoUI extends SPECppController{

	private InputDataBundle dataBundle;
	private InputProcessingConfig inputProcessingConfig;
	private Pair<Set<Activity>> activitySelection;
	
	public SpecppControllerNoUI(UIPluginContext context, SPECppSession specppSession) {
		super(context, specppSession);
	}
	
	public SpecppControllerNoUI(UIPluginContext context, SPECppSession specppSession, 
			InputProcessingConfig inputProcessingConfig, 
			Pair<Set<Activity>> activitySelection, 
			InputDataBundle bundle ) {
		super(context, specppSession);
		this.preprocessingCompleted(inputProcessingConfig, activitySelection, bundle);
	}
	
	public void preprocessingCompleted(InputProcessingConfig inputProcessingConfig, Pair<Set<Activity>> activitySelection, InputDataBundle bundle) {
        this.inputProcessingConfig = inputProcessingConfig;
        this.activitySelection = activitySelection;
        this.dataBundle = bundle;
    }

}
