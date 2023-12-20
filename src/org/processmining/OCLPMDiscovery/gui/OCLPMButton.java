package org.processmining.OCLPMDiscovery.gui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class OCLPMButton extends JButton{
	public OCLPMColors theme;
	private int cornerRadius = 10;
	
	public OCLPMButton(String text) {
		this(text,OCLPMColors.getLightMode());
	}
	
	public OCLPMButton(String text, OCLPMColors theme) {
	        super(text);

	        this.theme = theme;
	        this.setBackground(theme.ELEMENTS);
	        this.setForeground(theme.TEXT);
	        addMouseListener(new ButtonMouseListener());
	        addMouseMotionListener(new ButtonMouseMotionListener());
	        setBorder(new EmptyBorder(0, 0, 0, 0));
	        setOpaque(false);
    }
	
	public OCLPMButton(String text, OCLPMColors theme, int cornerRadius) {
		this(text, theme);
		this.setCornerRadius(cornerRadius);
	}
	
	public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }
	
	@Override
    public boolean isOpaque() {
        return false;
    }
	
	@Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the rounded rectangle background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        // Draw the text
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);

        g2.dispose();
    }

    private class ButtonMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setBackground(theme.ACCENT);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setBackground(theme.ELEMENTS);
        }
    }

    private class ButtonMouseMotionListener implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (contains(e.getPoint())) {
            	setBackground(theme.ACCENT);
            } else {
            	setBackground(theme.ELEMENTS);
            }
        }
    }
}