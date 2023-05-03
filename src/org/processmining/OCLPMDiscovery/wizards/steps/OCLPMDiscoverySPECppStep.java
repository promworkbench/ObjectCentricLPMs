package org.processmining.OCLPMDiscovery.wizards.steps;

import java.time.Duration;
import java.util.function.Function;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.parameters.SPECppParameters;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.specpp.prom.mvc.swing.ActivatableTextBasedInputField;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;

public class OCLPMDiscoverySPECppStep extends ProMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "SPECpp Settings";
	
	//TODO ui
	private ActivatableTextBasedInputField<Duration> discoveryTimeLimitInput;
    private ActivatableTextBasedInputField<Duration> totalTimeLimitInput;
    private JCheckBox permitNegativeMarkingsDuringReplayBox;
    private ProMTextField tauTextField;

    public OCLPMDiscoverySPECppStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        parameters.setSpecppParameters(new SPECppParameters());
        
        //TODO Make UI elements more beautiful
        // create UI elements
        totalTimeLimitInput = SwingFactory.activatableTextBasedInputField("activate", false, durationFunc, 25);
        totalTimeLimitInput.getCheckBox()
                           .setToolTipText("Real time limit over entire computation (discovery + post processing). Stops abruptly.");
        totalTimeLimitInput.getTextField()
                           .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
//        totalTimeLimitInput.addVerificationStatusListener(listener);
        this.totalTimeLimitInput = addProperty("total time limit",this.totalTimeLimitInput);
        
        discoveryTimeLimitInput = SwingFactory.activatableTextBasedInputField("activate", false, durationFunc, 25);
        discoveryTimeLimitInput.getCheckBox()
                               .setToolTipText("Real time limit for place discovery. Gracefully continues to post processing with intermediate result.");
        discoveryTimeLimitInput.getTextField()
                               .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
//        discoveryTimeLimitInput.addVerificationStatusListener(listener);
        this.discoveryTimeLimitInput = addProperty("discovery time limit",this.discoveryTimeLimitInput);
        
        this.permitNegativeMarkingsDuringReplayBox = addCheckBox("Permit negative markings during replay");
        
        this.tauTextField = addTextField("Tau",String.valueOf(parameters.getSpecppParameters().getTau()));
        this.tauTextField.setToolTipText("Threshold value in [0,1] for the eST-Miner.");
        
        
        // set default selections
        if (parameters.getSpecppParameters().getTotalTimeLimit() != null) {
            totalTimeLimitInput.setText(parameters.getSpecppParameters().getTotalTimeLimit().toString());
            totalTimeLimitInput.activate();
        } else totalTimeLimitInput.deactivate();
        
        if (parameters.getSpecppParameters().getDiscoveryTimeLimit() != null) {
            discoveryTimeLimitInput.setText(parameters.getSpecppParameters().getDiscoveryTimeLimit().toString());
            discoveryTimeLimitInput.activate();
        } else discoveryTimeLimitInput.deactivate();
        
        this.permitNegativeMarkingsDuringReplayBox.setSelected(parameters.getSpecppParameters().isPermitNegativeMarkingsDuringReplay());
        
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
        parameters.getSpecppParameters().setDiscoveryTimeLimit(this.discoveryTimeLimitInput.getInput());
        parameters.getSpecppParameters().setTotalTimeLimit(this.totalTimeLimitInput.getInput());
        parameters.getSpecppParameters().setPermitNegativeMarkingsDuringReplay(this.permitNegativeMarkingsDuringReplayBox.isSelected());
        double tau = Double.parseDouble(this.tauTextField.getText());
        if (0 <= tau && tau <= 1)
        	parameters.getSpecppParameters().setTau(tau);
        parameters.getSpecppParameters().registerParameters();
        return parameters;
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
