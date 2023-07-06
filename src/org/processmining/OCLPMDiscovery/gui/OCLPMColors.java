package org.processmining.OCLPMDiscovery.gui;

import java.awt.Color;

import org.processmining.framework.util.ui.widgets.WidgetColors;

public class OCLPMColors {
	// colors originally used by ProM widgets
	public Color BACKGROUND = WidgetColors.COLOR_ENCLOSURE_BG;
	public Color ELEMENTS = WidgetColors.COLOR_LIST_BG; // idk if this was the correct one
	// Textcolor should be readable on BACKGROUND and ELEMENTS
	public Color TEXT = WidgetColors.TEXT_COLOR;
	// Focus color should be very different from ELEMENTS, text is not well readable on the focus color 
	public Color FOCUS = new Color(160, 160, 160);
	// Textcolor that is readable on focused elements
	public Color TEXT_INVERS = Color.WHITE;
	public Color NON_FOCUS = WidgetColors.COLOR_NON_FOCUS;
	public Color ACCENT = Color.RED;
	
	/**
	 * colors originally used by ProM widgets
	 */
	public OCLPMColors() {
	}
	
	public static OCLPMColors getLightMode() {
		OCLPMColors colors = new OCLPMColors();
		colors.BACKGROUND = Color.WHITE;
		colors.ELEMENTS =new Color(160, 160, 160);
		colors.TEXT = Color.BLACK;
		colors.TEXT_INVERS = Color.WHITE;
		colors.NON_FOCUS = new Color(200, 200, 200);
		colors.FOCUS =  new Color(90, 90, 90);
		return colors;
	}
	
	public static OCLPMColors getDarkMode() {
		OCLPMColors colors = new OCLPMColors();
		colors.BACKGROUND = new Color(90, 90, 90);
		colors.ELEMENTS = new Color(140, 140, 140);
		colors.TEXT = Color.WHITE;
		colors.TEXT_INVERS = Color.BLACK;
		colors.NON_FOCUS = new Color(110, 110, 110);
		colors.FOCUS = new Color(160, 160, 160);
		return colors;
	}
	
	public void setAccent(Color color) {
		this.ACCENT = color;
	}

}
