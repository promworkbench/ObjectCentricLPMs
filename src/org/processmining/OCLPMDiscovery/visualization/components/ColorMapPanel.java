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

    public ColorMapPanel(HashMap<String, Color> colorMap) {
        this.colorMap = colorMap;
        setPreferredSize(new Dimension(200, colorMap.size() * cellHeight));
    }
    
    public ColorMapPanel(HashMap<String, Color> colorMap, int width) {
        this.colorMap = colorMap;
        setPreferredSize(new Dimension(width, colorMap.size() * cellHeight));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int i = 0;
        for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
            g.setColor(entry.getValue());
            g.fillRect(0, i * cellHeight, 50, cellHeight);
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), 60, (i + 1) * cellHeight - 10);
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
