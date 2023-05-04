package org.processmining.OCLPMDiscovery.wizards.steps;

import java.util.List;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.Miner;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;

public class OCLPMDiscoveryILPStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "Hybrid ILP Miner Settings";
	
	ProMComboBox<String> box_IlpMiners;
	List<Miner> minersListEnums;

    public OCLPMDiscoveryILPStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        PluginContext context = null; //TODO does this work?
        XLogHybridILPMinerParametersImpl ilpParameters = new XLogHybridILPMinerParametersImpl(context);
        parameters.setIlpParameters(ilpParameters);
        XEventNameClassifier classifier = new XEventNameClassifier();
		classifier.setName("concept:name");
		parameters.getIlpParameters().setEventClassifier(classifier);

        //TODO set default parameters
        
        //TODO create UI elements
        
        //TODO set default selections
        
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
//        parameters.getIlpParameters().;//TODO apply parameter settings
        return parameters;
    }

    @Override
    public boolean canApply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        return jComponent instanceof OCLPMDiscoveryILPStep;
    }

    @Override
    public JComponent getComponent(OCLPMDiscoveryParameters parameters) {
        
        return this;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
