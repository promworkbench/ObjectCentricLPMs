package org.processmining.OCLPMDiscovery.wizards.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionListener;

import org.processmining.OCLPMDiscovery.gui.OCLPMList;
import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.CaseNotionStrategy;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryLPMStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "LPM Discovery Case Notion Settings";
	
	// UI components
	ProMComboBox<String> box_LPMDiscoveryCaseNotionStrategy;
	OCLPMList<String> list_LeadingTypes;
	
	List<CaseNotionStrategy> caseNotionListEnums;
	ListSelectionListener caseNotionListener;

    public OCLPMDiscoveryLPMStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        
        // selection of strategy for artificial case notion
        this.caseNotionListEnums = Arrays.asList(CaseNotionStrategy.values());
        ArrayList<String> caseNotionListStrings = new ArrayList<String>();
        this.caseNotionListEnums.forEach(cur -> caseNotionListStrings.add(cur.getName()));
        this.box_LPMDiscoveryCaseNotionStrategy = addComboBox("LPM Case Notion", caseNotionListStrings);//TODO Add some description when hovering over the box?
        // set default selection
        this.box_LPMDiscoveryCaseNotionStrategy.setSelectedItem(parameters.getCaseNotionStrategy().getName());
        this.box_LPMDiscoveryCaseNotionStrategy.addActionListener(e -> this.changedCaseNotion());
        
        
        // scrollable list with all object types where each can be ticked off or on
        this.list_LeadingTypes = new OCLPMList<String>("Select Object Types used as Leading Types",parameters.getObjectTypesList()); 
        addProperty("Process Executions", this.list_LeadingTypes);
        
        // set initial state to be all selected
        int start = 0;
        int end = this.list_LeadingTypes.getList().getModel().getSize() - 1;
        if (end >= 0) {
        	this.list_LeadingTypes.getList().setSelectionInterval(start, end);
        }
        
        // toggle list visible depending on selected case notion
        this.changedCaseNotion();
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
        parameters.setObjectTypesLeadingTypes(new HashSet<String>(list_LeadingTypes.getSelectedValuesList()));
        parameters.setCaseNotionStrategy(this.caseNotionListEnums.get(box_LPMDiscoveryCaseNotionStrategy.getSelectedIndex()));
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
    
    private void changedCaseNotion() {
    	CaseNotionStrategy currentStrat = this.caseNotionListEnums.get(box_LPMDiscoveryCaseNotionStrategy.getSelectedIndex());
    	if (CaseNotionStrategy.typeSelectionNeeded.contains(currentStrat)) {
        	this.list_LeadingTypes.setEnabled(true);
        }
        else {
        	this.list_LeadingTypes.setEnabled(false);
        }
    }
}
