package org.processmining.OCLPMDiscovery.wizards.steps;

import java.time.Duration;
import java.util.function.Function;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMCheckBoxWithTextField;
import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.gui.OCLPMTextField;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.parameters.SPECppParameters;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoverySPECppStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "SPECpp Settings";
	
	private OCLPMCheckBoxWithTextField discoveryTimeLimitInput;
    private OCLPMCheckBoxWithTextField totalTimeLimitInput;
    private JCheckBox permitNegativeMarkingsDuringReplayBox;
    private OCLPMTextField tauTextField;
    private OCLPMDiscoveryParameters parameters;

    public OCLPMDiscoverySPECppStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        parameters.setSpecppParameters(new SPECppParameters());
        this.parameters = parameters;
        
        // create UI elements
        int nameLabelSize = 250;
        
        totalTimeLimitInput = new OCLPMCheckBoxWithTextField(true,true,"",true,nameLabelSize);
        totalTimeLimitInput.getCheckBox()
                           .setToolTipText("Real time limit over entire computation (discovery + post processing). Stops abruptly.");
        totalTimeLimitInput.getTextField()
                           .setToolTipText("Time in Minutes (double values possible)");
        addProperty("total time limit [minutes]",this.totalTimeLimitInput,nameLabelSize);
        
        discoveryTimeLimitInput = new OCLPMCheckBoxWithTextField(true,true,"",true,nameLabelSize);
        discoveryTimeLimitInput.getCheckBox()
                               .setToolTipText("Real time limit for place discovery. Gracefully continues to post processing with intermediate result.");
        discoveryTimeLimitInput.getTextField()
                               .setToolTipText("Time in Minutes (double values possible)");
        addProperty("discovery time limit [minutes]",this.discoveryTimeLimitInput,nameLabelSize);
        
        permitNegativeMarkingsDuringReplayBox = addCheckBox("Permit negative markings during replay",nameLabelSize);
        
        tauTextField = new OCLPMTextField(String.valueOf(parameters.getSpecppParameters().getTau()));
        String hint = "Threshold value in [0,1] for the eST-Miner.";
        tauTextField.setHint(hint); // shows only when text field is empty and not in focus
        tauTextField.getTextField().setToolTipText(hint);
        addProperty("tau (eST-Miner Threshold in [0,1])",tauTextField, nameLabelSize);
        
        
        // set default selections
        if (parameters.getSpecppParameters().getTotalTimeLimit() != null) {
        	String t = String.valueOf(parameters.getSpecppParameters().getTotalTimeLimitAsMinutes());
            totalTimeLimitInput.setText(t);
            totalTimeLimitInput.setSelected(true);
        } else totalTimeLimitInput.setSelected(false);
        
        if (parameters.getSpecppParameters().getDiscoveryTimeLimit() != null) {
        	String t = String.valueOf(parameters.getSpecppParameters().getDiscoveryTimeLimitAsMinutes());
            discoveryTimeLimitInput.setText(t);
            discoveryTimeLimitInput.setSelected(true);
        } else discoveryTimeLimitInput.setSelected(false);
        
        this.permitNegativeMarkingsDuringReplayBox.setSelected(parameters.getSpecppParameters().isPermitNegativeMarkingsDuringReplay());
        
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
        if (this.totalTimeLimitInput.getCheckBox().isSelected()) {
        	parameters.getSpecppParameters().setTotalTimeLimit(this.stringToDuration(this.totalTimeLimitInput.getText()));
        }
        else {
        	parameters.getSpecppParameters().setTotalTimeLimit(Duration.ofDays(8));
        }
        if (this.discoveryTimeLimitInput.getCheckBox().isSelected()) {
        	parameters.getSpecppParameters().setDiscoveryTimeLimit(this.stringToDuration(this.discoveryTimeLimitInput.getText()));
        }
        else {
        	parameters.getSpecppParameters().setDiscoveryTimeLimit(Duration.ofDays(8));
        }
        parameters.getSpecppParameters().setPermitNegativeMarkingsDuringReplay(this.permitNegativeMarkingsDuringReplayBox.isSelected());
        double tau = Double.parseDouble(this.tauTextField.getText());
        if (0 <= tau && tau <= 1)
        	parameters.getSpecppParameters().setTau(tau);
        parameters.getSpecppParameters().registerParameters();
        return parameters;
    }
    
    private Duration stringToDuration(String input) {
    	Duration duration = parameters.getSpecppParameters().getTotalTimeLimit(); // fallback
    	try
    	{
    	  double minutes = Double.parseDouble(input);
    	  long seconds = (long) (minutes*60.0);
    	  duration = Duration.ofSeconds(seconds);
    	}
    	catch(NumberFormatException e)
    	{
    		System.out.println("Entered incorrect time format.");
    	  //not a double
    	}
    	return duration;
    }

    @Override
    public boolean canApply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        return jComponent instanceof OCLPMDiscoverySPECppStep;
    }

    @Override
    public JComponent getComponent(OCLPMDiscoveryParameters parameters) {
        return this;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
    
    private static final Function<String, Duration> durationFunc = Duration::parse;
}
