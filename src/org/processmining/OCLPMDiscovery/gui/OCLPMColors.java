package org.processmining.OCLPMDiscovery.gui;

import java.awt.Color;

import org.processmining.framework.util.ui.widgets.WidgetColors;

public class OCLPMColors {
	// colors originally used by ProM widgets (i hope)
	public Color BACKGROUND = WidgetColors.COLOR_ENCLOSURE_BG;
	public Color ELEMENTS = WidgetColors.COLOR_LIST_BG; // idk if this was the correct one
	public Color TEXT = WidgetColors.TEXT_COLOR; 
	public Color FOCUS = new Color(160, 160, 160);
	public Color TEXT_INVERS = Color.WHITE;
	public Color NON_FOCUS = WidgetColors.COLOR_NON_FOCUS;
	public Color ACCENT = Color.RED;
	
	// Guide
	// BACKGROUND:
		// main color
	// ELEMENTS:
		// should be only a little different (darker/lighter) than the background
		// The text should be easily legible on both elements and background
		// Used for elements like buttons, scrollbars and some borders
	// FOCUS:
		// Significantly different from the ELEMENTS color
		// Used e.g. for selected table entries, pressed buttons
	// TEXT:
		// Should be legible on BACKGROUND and ELEMENTS colors
	// TEXT_INVERS:
		// Should be legible on FOCUS color
	// ACCENT
		// Color splash, used e.g., when hovering over scrollbar or button
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
		colors.ACCENT = new Color(189, 8, 47); // bit darker red with pink tint
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
		colors.ACCENT = Color.ORANGE;
		return colors;
	}
	
	/*
	 * Just to know which color does what.
	 */
	public static OCLPMColors getClownMode() {
		OCLPMColors colors = new OCLPMColors();
		colors.BACKGROUND = Color.RED;
		colors.ELEMENTS = Color.GREEN;
		colors.TEXT = Color.BLUE;
		colors.TEXT_INVERS = Color.YELLOW;
		colors.NON_FOCUS = Color.PINK;
		colors.FOCUS = Color.CYAN;
		colors.ACCENT = Color.ORANGE;
		return colors;
	}
	
	public static OCLPMColors getCustomMode(
			Color background,
			Color elements,
			Color text,
			Color text_invers,
			Color focus,
			Color non_focus,
			Color accent
			) {
		OCLPMColors colors = new OCLPMColors();
		colors.BACKGROUND = Color.RED;
		colors.ELEMENTS = Color.GREEN;
		colors.TEXT = Color.BLUE;
		colors.TEXT_INVERS = Color.YELLOW;
		colors.FOCUS = Color.CYAN;
		colors.NON_FOCUS = Color.PINK;
		colors.ACCENT = Color.ORANGE;
		return colors;
	}
	
	public void setAccent(Color color) {
		this.ACCENT = color;
	}

}
