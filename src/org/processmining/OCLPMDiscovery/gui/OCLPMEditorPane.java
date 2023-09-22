package org.processmining.OCLPMDiscovery.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JEditorPane;
import javax.swing.border.EmptyBorder;

public class OCLPMEditorPane extends JEditorPane{
	private int paddingLeft = 15;
    private int paddingTop = 15;
    
	public OCLPMEditorPane(String text, boolean editable, boolean html, OCLPMColors theme) {
		super();
		this.setOpaque(true);
		this.setBackground(theme.BACKGROUND);
		this.setForeground(theme.TEXT);
		this.setSelectedTextColor(theme.ELEMENTS);
		this.setSelectionColor(theme.ACCENT);
		this.setCaretColor(theme.ACCENT);

		if (!editable) {
			setEditable(false);
//			setLineWrap(true);
		}
		
		if (html) {
			 this.setContentType("text/html");
		}
		
//		this.setLineWrap(false);
		this.setText(text);
		this.setPadding(this.paddingLeft, this.paddingTop);
		
		init();
	}
	
	public void setPadding(int left, int top) {
		this.paddingLeft = left;
		this.paddingTop = top;
		setBorder(new EmptyBorder(paddingTop, paddingLeft, 0, 0));
	}
	
	@Override
    public Dimension getPreferredSize() {
        String text = getText();
        Font font = getFont();
        FontMetrics fontMetrics = getFontMetrics(font);

        if (text.isEmpty()) {
            return super.getPreferredSize();
        }

        int maxWidth = 0;
        int totalHeight = 0;

        String[] lines; 
        if (this.getContentType()=="text/html") {
        	lines = text.split("<br>");
        }
        else {
	        lines = text.split("\n");
        	
        }
        int prevLineHeight = 0;
        for (String line : lines) {
        	if (line.length() == 0) {
        		totalHeight += prevLineHeight;
        		continue;
        	}
            TextLayout layout = new TextLayout(line, font, fontMetrics.getFontRenderContext());
            Rectangle2D bounds = layout.getBounds();
            int lineWidth = (int) Math.ceil(bounds.getWidth());
            int lineHeight = (int) Math.ceil(bounds.getHeight());
            prevLineHeight = lineHeight;
            maxWidth = Math.max(maxWidth, lineWidth);
            totalHeight += lineHeight;
        }

        int width = maxWidth + getInsets().left + getInsets().right;
        int height = (int) (1.7*totalHeight) + getInsets().top + getInsets().bottom;

        return new Dimension((int) (width*1.05), height);
    }
	
	private void init() {
        // Add a component listener to adjust the size when the content changes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setSize(getPreferredSize());
                revalidate();
            }
        });
    }
	
	@Override
    public void setText(String t) {
        super.setText(t);
        setSize(getPreferredSize());
        revalidate();
    }
	
	public void append(String t) {
        String currentText = getText();
        String newText = currentText + t;
        this.setText(newText);
        revalidate();
        repaint();
    }
}
