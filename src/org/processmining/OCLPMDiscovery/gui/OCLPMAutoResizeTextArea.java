package org.processmining.OCLPMDiscovery.gui;

import java.awt.Dimension;

import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class OCLPMAutoResizeTextArea extends JTextArea{
	private int paddingLeft = 15;
    private int paddingTop = 15;
    
	public OCLPMAutoResizeTextArea(String text, boolean editable, OCLPMColors theme) {
		super();
		this.setOpaque(true);
		this.setBackground(theme.BACKGROUND);
		this.setForeground(theme.TEXT);
		this.setSelectedTextColor(theme.ELEMENTS);
		this.setSelectionColor(theme.ACCENT);
		this.setCaretColor(theme.ACCENT);

		if (!editable) {
			setEditable(false);
			setLineWrap(true);
		}
		
		this.setLineWrap(false);
		this.setText(text);
		this.setPadding(this.paddingLeft, this.paddingTop);
	}
	
	public void setPadding(int left, int top) {
		this.paddingLeft = left;
		this.paddingTop = top;
		setBorder(new EmptyBorder(paddingTop, paddingLeft, 0, 0));
	}
	
	@Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        int rows = getRows();
        int lineHeight = getFontMetrics(getFont()).getHeight();
        int borderHeight = getInsets().top + getInsets().bottom;
        int preferredHeight = (rows * lineHeight) + borderHeight + paddingTop;
        return new Dimension(size.width, preferredHeight);
    }
}
