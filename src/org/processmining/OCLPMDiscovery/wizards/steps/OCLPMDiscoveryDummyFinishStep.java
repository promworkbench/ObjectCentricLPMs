package org.processmining.OCLPMDiscovery.wizards.steps;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryDummyFinishStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "Finished Configuration";
	ProMTextArea textArea;

    public OCLPMDiscoveryDummyFinishStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        //TODO print more selected parameters
        this.textArea = new ProMTextArea();
        textArea.setEditable(false);
        textArea.append(parameters.toString());
        textArea.append("\n Yes, this currently only shows the default values, not the selected parameters..."); //TODO fix that
        addProperty("",textArea,0);
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
