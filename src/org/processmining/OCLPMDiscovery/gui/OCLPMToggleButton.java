package org.processmining.OCLPMDiscovery.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JToggleButton;


public class OCLPMToggleButton extends JToggleButton{

	OCLPMColors theme = OCLPMColors.getLightMode();
	private int cornerRadius = 8;
	private boolean hover; // Track the hover state
	
	public OCLPMToggleButton() {
		super();
	}
	
	public OCLPMToggleButton(OCLPMColors theme) {
		super();
		this.theme = theme;
		
		this.setBackground(theme.NON_FOCUS);
		this.setForeground(theme.TEXT);
		
		// remove the box around the text
		this.setFocusPainted(false);
		
		setContentAreaFilled(false);
		
		addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
            	 hover = true;
                 repaint(); // Request repaint to update the rendering
            }

            @Override
            public void mouseExited(MouseEvent e) {
            	hover = false;
                repaint(); // Request repaint to update the rendering
            }
        });
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.hover) {
        	g2.setColor(this.theme.ACCENT);
        }
        else {
	        if (isSelected()) {
	            g2.setColor(this.theme.FOCUS);
	        } else {
	            g2.setColor(this.theme.NON_FOCUS);
	        }
        }

        // Paint rounded background
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), this.cornerRadius, this.cornerRadius);

        // Paint text
        super.paintComponent(g2);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Remove border painting for rounded appearance
    }
    
    public void setCornerRadius (int cornerRadius) {
    	this.cornerRadius = cornerRadius;
    }
}
