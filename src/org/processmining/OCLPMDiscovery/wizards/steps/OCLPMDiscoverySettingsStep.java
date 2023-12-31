package org.processmining.OCLPMDiscovery.wizards.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMList;
import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.Miner;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoverySettingsStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "OCLPM Discovery Settings";
	
	OCLPMList<String> list_TypesPlaceNets;
	ProMComboBox<String> box_placeDiscoveryMiner;
	List<Miner> minersListEnums;

    public OCLPMDiscoverySettingsStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);        
		
        // scrollable list with all object types where each can be ticked off or on
        this.list_TypesPlaceNets = new OCLPMList<String>("Select Object Types for Place Net Discovery",parameters.getObjectTypesList());
        addProperty("Place Nets", this.list_TypesPlaceNets);
        
        // set initial state to be all selected
        int start = 0;
        int end = this.list_TypesPlaceNets.getList().getModel().getSize() - 1;
        if (end >= 0) {
        	this.list_TypesPlaceNets.getList().setSelectionInterval(start, end);
        }
//        System.out.println("Finished setting up ProMLists.");

        // selection of miner for place nets discovery
        this.minersListEnums = Arrays.asList(Miner.values());
        ArrayList<String> minersListStrings = new ArrayList<String>();
        this.minersListEnums.forEach(curMiner -> minersListStrings.add(curMiner.getName()));
        this.box_placeDiscoveryMiner = addComboBox("Place Nets Miner", minersListStrings);
        // set default selection
        this.box_placeDiscoveryMiner.setSelectedItem(parameters.getPlaceDiscoveryAlgorithm().getName());
        
//        System.out.println("Finished OCLPMDiscoverySettingsStep constructor.");
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
        parameters.setObjectTypesPlaceNets(new HashSet<String>(list_TypesPlaceNets.getSelectedValuesList()));
        parameters.setPlaceDiscoveryAlgorithm(this.minersListEnums.get(box_placeDiscoveryMiner.getSelectedIndex()));
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
