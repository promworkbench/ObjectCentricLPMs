package org.processmining.OCLPMDiscovery.wizards.steps;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;

public class OCLPMDiscoveryDummyFinishStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters>{
	
	private static final String TITLE = "Finished Configuration";
	ProMTextArea textArea;
	private final ProMTextField modelLimit;

    public OCLPMDiscoveryDummyFinishStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);
        this.textArea = new ProMTextArea();
        textArea.setEditable(false);
        addProperty("",textArea,0);
        
        modelLimit = new ProMTextField("100",
              "Number of OCLPMs that will be returned");
        addProperty("Model Count", modelLimit);
        modelLimit.addKeyListener(new KeyListener() {
          @Override
          public void keyTyped(KeyEvent keyEvent) {
              if (!Character.isDigit(keyEvent.getKeyChar()) &&
                      !(keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
                  keyEvent.consume();
              }
          }

          @Override
          public void keyPressed(KeyEvent keyEvent) {
              if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_V) {
                  keyEvent.consume();
              }
          }

          @Override
          public void keyReleased(KeyEvent keyEvent) {

          }
        });
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent)) {
            return parameters;
        }
        parameters.setModelLimit(Integer.parseInt(modelLimit.getText()));
        return parameters;
    }

    @Override
    public boolean canApply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        return jComponent instanceof OCLPMDiscoveryDummyFinishStep;
    }

    @Override
    public JComponent getComponent(OCLPMDiscoveryParameters parameters) {
    	textArea.setText(parameters.toString());
    	this.modelLimit.setText(String.valueOf(parameters.getModelLimit()));
        return this;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
