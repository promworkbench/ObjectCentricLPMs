package org.processmining.OCLPMDiscovery.wizards.steps;

import java.util.HashSet;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryLPMStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "LPM Discovery Case Notion Settings";
	
	ProMList<String> list_LeadingTypes;

    public OCLPMDiscoveryLPMStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        
        // scrollable list with all object types where each can be ticked off or on
        this.list_LeadingTypes = addProperty(
        		"Process Executions", 
        		new ProMList<String>("Select Object Types used as Leading Types",parameters.getObjectTypesList())
        		);
        
        // set initial state to be all selected
        int start = 0;
        int end = this.list_LeadingTypes.getList().getModel().getSize() - 1;
        if (end >= 0) {
        	this.list_LeadingTypes.getList().setSelectionInterval(start, end);
        }
        
        
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
        parameters.setObjectTypesLeadingTypes(new HashSet<String>(list_LeadingTypes.getSelectedValuesList()));
        return parameters;
    }

    @Override
    public boolean canApply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        return jComponent instanceof OCLPMDiscoveryLPMStep;
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
