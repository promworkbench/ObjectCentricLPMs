package org.processmining.OCLPMDiscovery.visualization.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
    private final Font font = new Font("Arial", Font.PLAIN, 14);
    private final Font fontBold = new Font("Arial", Font.BOLD, 16);

    public ColorMapPanel(HashMap<String, Color> colorMap) {
        this.colorMap = colorMap;
        setPreferredSize(new Dimension(200, (colorMap.size()+1) * cellHeight)); // +1 for the legend    
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        Font currentFont = g.getFont();
        int i = 0;
        // paint title
        g.setColor(Color.BLACK);
        g.setFont(fontBold);
        g.drawString("Place Object Type Legend", rowSeparation*2+textDistance, (i + 1) * cellHeight - 10);
        g.setFont(font);
        i++;
        // paint entries
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
