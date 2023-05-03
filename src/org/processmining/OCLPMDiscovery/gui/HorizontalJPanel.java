package org.processmining.OCLPMDiscovery.gui;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class HorizontalJPanel extends JPanel {
    public void addSpaced(JComponent component) {
        add(Box.createHorizontalStrut(5));
        add(component);
    }
}