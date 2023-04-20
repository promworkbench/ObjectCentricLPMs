package org.processmining.OCLPMDiscovery.plugins.mining.wizards.steps;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.plugins.mining.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoverySettingsStep extends ProMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "OCLPM Discovery Settings";

    public OCLPMDiscoverySettingsStep() {
        super(TITLE);
        //TODO scrollable list with all object types where each can be ticked off or on
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
        return jComponent instanceof OCLPMDiscoverySettingsStep;
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
