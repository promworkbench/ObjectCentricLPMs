package org.processmining.OCLPMDiscovery.visualization.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Scrollable;

public class ColorMapPanel extends JPanel implements Scrollable {

    private final HashMap<String, Color> colorMap;
    private final int cellHeight = 30;
    private final int borderSize = 1;
    private final int rowSeparation = 2;
    private final int diameter = cellHeight-2*rowSeparation;
    private final int textDistance = 6; // additional distance from circles to the string

    public ColorMapPanel(HashMap<String, Color> colorMap) {
        this.colorMap = colorMap;
        setPreferredSize(new Dimension(200, colorMap.size() * cellHeight));    
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int i = 0;
        for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
        	g.setColor(Color.BLACK);
            g.fillOval(rowSeparation, (i * cellHeight)+rowSeparation, diameter, diameter); // circle border
            g.drawString(entry.getKey(), diameter+rowSeparation*2+textDistance, (i + 1) * cellHeight - 10);
            g.setColor(entry.getValue());
            g.fillOval(rowSeparation+borderSize, (i * cellHeight)+rowSeparation+borderSize, diameter-2*borderSize, diameter-2*borderSize); // circle
            g.setColor(Color.BLACK);
            i++;
        }
    }

    // Scrollable implementation
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return cellHeight;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return cellHeight * 5;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
