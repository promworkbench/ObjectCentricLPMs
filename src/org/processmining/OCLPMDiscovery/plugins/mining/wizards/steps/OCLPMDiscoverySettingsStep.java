package org.processmining.OCLPMDiscovery.plugins.mining.wizards.steps;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.plugins.mining.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoverySettingsStep extends ProMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "OCLPM Discovery Settings";
	
	ProMList<String> list_TypesPlaceNets;
	DefaultListModel<String> objectTypesList = new DefaultListModel<String>();
	JCheckBox testCheckBox;

    public OCLPMDiscoverySettingsStep() {
        super(TITLE);
        
        //TODO scrollable list with all object types where each can be ticked off or on
        
        objectTypesList.addElement("Testety test test");
		
        this.list_TypesPlaceNets = addProperty("Object Types for Place Nets", new ProMList<String>("",objectTypesList));
        
        this.testCheckBox = addCheckBox("Testbox");
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
