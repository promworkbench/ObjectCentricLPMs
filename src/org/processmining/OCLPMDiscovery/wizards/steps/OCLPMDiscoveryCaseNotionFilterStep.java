package org.processmining.OCLPMDiscovery.wizards.steps;

import java.util.HashSet;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMList;
import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryCaseNotionFilterStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "Case Notion Object Type Filter";
	
	// UI components
	OCLPMList<String> list_ObjectTypes;

    public OCLPMDiscoveryCaseNotionFilterStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        
        // scrollable list with all object types where each can be ticked off or on
        this.list_ObjectTypes = new OCLPMList<String>("Select Object Types to consider for new case notions",parameters.getObjectTypesList()); 
        addProperty("Object Types", this.list_ObjectTypes);
        
        // set initial state to be all selected
        int start = 0;
        int end = this.list_ObjectTypes.getList().getModel().getSize() - 1;
        if (end >= 0) {
        	this.list_ObjectTypes.getList().setSelectionInterval(start, end);
        }
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
        parameters.setObjectTypesCaseNotion(new HashSet<String>(list_ObjectTypes.getSelectedValuesList()));
        return parameters;
    }

    @Override
    public boolean canApply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        return jComponent instanceof OCLPMDiscoveryCaseNotionFilterStep;
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
