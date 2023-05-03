package org.processmining.OCLPMDiscovery.gui;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.processmining.framework.util.ui.widgets.ProMTextField;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class CheckboxedTextField extends HorizontalJPanel {

    protected final ProMTextField field;
    protected final JCheckBox checkBox;

    public CheckboxedTextField(String label, boolean enabledByDefault, int inputTextColumns) {
        checkBox = SlickerFactory.instance().createCheckBox(label, enabledByDefault);
        checkBox.addActionListener(e -> updateTextFieldState());
        add(checkBox);
        field = new ProMTextField();
        field.getTextField().setColumns(inputTextColumns);
        addSpaced(field);
        updateTextFieldState();
    }

    private void updateTextFieldState() {
        field.setEnabled(checkBox.isEnabled());
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public ProMTextField getProMField() {
        return field;
    }
    
    public JTextField getTextField() {
        return field.getTextField();
    }

    public String getText() {
        return checkBox.isEnabled() ? field.getText() : null;
    }


}