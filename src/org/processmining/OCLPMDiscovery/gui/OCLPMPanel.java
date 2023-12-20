package org.processmining.OCLPMDiscovery.gui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class OCLPMPanel extends JPanel{

	private int borderWidth = 0;
	private int size = 0;
	
	public OCLPMPanel(OCLPMColors theme) {
		super();
		this.setBackground(theme.BACKGROUND);
	}
	
	public OCLPMPanel (int size, int borderWidth, OCLPMColors theme) {
		this(theme);
		this.size = size;
		this.borderWidth = borderWidth;
		setBorder(BorderFactory.createEmptyBorder(borderWidth, size, borderWidth, size));
	}
	
	public OCLPMPanel (int size, int borderWidth) {
		this(new OCLPMColors());
		this.size = size;
		this.borderWidth = borderWidth;
		setBorder(BorderFactory.createEmptyBorder(borderWidth, size, borderWidth, size));
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (isOpaque()) {
			g2d.setColor(getBackground());
			g2d.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, size * 2, size * 2);
		}
		if (borderWidth > 0) {
			g2d.setColor(getForeground());
			g2d.setStroke(new BasicStroke(borderWidth));
			g2d.drawRoundRect(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth - 1, getHeight() - borderWidth
					- 1, size * 2, size * 2);
		}
	}
}
