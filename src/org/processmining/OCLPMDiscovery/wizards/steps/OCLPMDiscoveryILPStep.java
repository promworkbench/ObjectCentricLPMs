package org.processmining.OCLPMDiscovery.wizards.steps;

import java.util.List;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.Miner;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryILPStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "Hybrid ILP Miner Settings";
	
	ProMTextArea textArea;
	
	ProMComboBox<String> box_IlpMiners;
	List<Miner> minersListEnums;

    public OCLPMDiscoveryILPStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
//        PluginContext context = null; //TODO does this work?
//        XLogHybridILPMinerParametersImpl ilpParameters = new XLogHybridILPMinerParametersImpl(context);
//        parameters.setIlpParameters(ilpParameters);
//        XEventNameClassifier classifier = new XEventNameClassifier();
//		classifier.setName("concept:name");
//		parameters.getIlpParameters().setEventClassifier(classifier);
        
        this.textArea = new ProMTextArea();
        textArea.setEditable(false);
        textArea.append("ILP Miner settings follow in the next wizard after this one."); //TODO integrate settings into this if necessary
        addProperty("",textArea,0);

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
