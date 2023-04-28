package org.processmining.OCLPMDiscovery.wizards.steps;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryDummyFinishStep extends ProMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "Finished Configuration";

    public OCLPMDiscoveryDummyFinishStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        //TODO maybe print the selected settings here
        ProMTextArea textArea = new ProMTextArea();
        textArea.setEditable(false);
        textArea.append(parameters.toString());
        addProperty("",textArea); //TODO add the text area without the name on the left
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
        return parameters;
    }

    @Override
    public boolean canApply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        return jComponent instanceof OCLPMDiscoveryDummyFinishStep;
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
