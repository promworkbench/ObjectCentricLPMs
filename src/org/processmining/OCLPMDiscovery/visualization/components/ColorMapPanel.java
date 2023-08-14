package org.processmining.OCLPMDiscovery.visualization.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;

public class ColorMapPanel extends JPanel implements Scrollable {

    private final HashMap<String, Color> colorMap;
    private final int cellHeight = 30;
    private final int borderSize = 1;
    private final int rowSeparation = 2;
    private final int diameter = cellHeight-2*rowSeparation;
    private final int textDistance = 6; // additional distance from circles to the string
    private final Font font = new Font("Arial", Font.PLAIN, 14);
    private final Font fontBold = new Font("Arial", Font.BOLD, 16);
    private OCLPMColors theme = new OCLPMColors();
    private boolean showExternalObjectFlow = true;

    public ColorMapPanel(HashMap<String, Color> colorMap) {
        this.colorMap = colorMap;
        setPreferredSize(new Dimension(200, (colorMap.size()+4) * cellHeight)); // +1 for the legend, +2 for arcs, +1 for start/end place
        this.setBackground(this.theme.BACKGROUND);
        this.setForeground(this.theme.BACKGROUND);
        for (Component component : this.getComponents()) {
        	component.setBackground(this.theme.BACKGROUND);
        }
    }
    
    public ColorMapPanel(HashMap<String, Color> colorMap, OCLPMColors theme) {
        this(colorMap);
        this.theme = theme;
        this.setBackground(this.theme.BACKGROUND);
        this.setForeground(this.theme.BACKGROUND);
        for (Component component : this.getComponents()) {
        	component.setBackground(this.theme.BACKGROUND);
        	component.setForeground(this.theme.BACKGROUND);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int i = 0;
        // paint title
        g.setColor(theme.TEXT);
        g.setFont(fontBold);
        g.drawString("Legend", rowSeparation*2+textDistance, (i + 1) * cellHeight - 10);
        g.setFont(font);
        i++;
        // paint entries
        for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
        	g.setColor(Color.BLACK);
            g.fillOval(rowSeparation, (i * cellHeight)+rowSeparation, diameter, diameter); // circle border
            g.setColor(theme.TEXT);
            g.drawString(entry.getKey(), diameter+rowSeparation*2+textDistance, (i + 1) * cellHeight - 10);
            g.setColor(entry.getValue());
            g.fillOval(rowSeparation+borderSize, (i * cellHeight)+rowSeparation+borderSize, diameter-2*borderSize, diameter-2*borderSize); // circle
            g.setColor(Color.BLACK);
            i++;
        }
        // paint usual arc
        g.setColor(theme.ELEMENTS);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        int arrowYPos = (i*cellHeight)+rowSeparation+cellHeight/2;
        int apex = rowSeparation + diameter + 10;
        // Arrowshaft
        int[] xPoints = {rowSeparation, apex-5};
        int[] yPoints = {arrowYPos, arrowYPos};
        g.drawPolyline(xPoints, yPoints, xPoints.length);
        // Arrowhead
        int[] xTriangle = {apex-20, apex, apex-20};
        int[] yTriangle = {arrowYPos-10, arrowYPos, arrowYPos+10};
        g.fillPolygon(xTriangle, yTriangle, xTriangle.length);
        // ArrowText
        g.setColor(theme.TEXT);
        g.drawString("Arc", apex+textDistance, (i + 1) * cellHeight - 10);
        
        // paint variable arc
        i++;
        g.setColor(theme.ELEMENTS.darker());
        g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        arrowYPos = (i*cellHeight)+rowSeparation+cellHeight/2;
        apex = rowSeparation + diameter + 10;
        // Arrowshaft
        xPoints[0] = rowSeparation; xPoints[1]= apex-5;
        yPoints[0] = arrowYPos; yPoints[1] = arrowYPos;
        g.drawPolyline(xPoints, yPoints, xPoints.length);
        // Arrowhead
        xTriangle[0] = apex-20; xTriangle[1] = apex; xTriangle[2] = apex-20;
        yTriangle[0] = arrowYPos-10; yTriangle[1] = arrowYPos; yTriangle[2] = arrowYPos+10;
        g.fillPolygon(xTriangle, yTriangle, xTriangle.length);
        // ArrowText
        g.setColor(theme.TEXT);
        g.drawString("Variable Arc", apex+textDistance, (i + 1) * cellHeight - 10);
        
        // paint start / end place
        if (this.showExternalObjectFlow) {
        	i++;
        	g.setColor(theme.ACCENT);
        	int newHeight = (int) (diameter*(2.0/3.0));
        	g.fillOval(rowSeparation, (i * cellHeight)+rowSeparation+(diameter-newHeight)/2, diameter, newHeight); // circle border
        	g.setColor(theme.ELEMENTS);
//        	newHeight *= 0.9;
        	int borderSize = this.borderSize*2;
        	g.fillOval(rowSeparation+borderSize, (i * cellHeight)+rowSeparation+borderSize+(diameter-newHeight)/2, diameter-2*borderSize, newHeight-2*borderSize); // circle
        	g.setColor(theme.TEXT);
            g.drawString("Starting / Ending Place", diameter+rowSeparation*2+textDistance, (i + 1) * cellHeight - 10);
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
