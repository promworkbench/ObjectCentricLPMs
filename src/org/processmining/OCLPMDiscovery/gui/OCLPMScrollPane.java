package org.processmining.OCLPMDiscovery.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

public class OCLPMScrollPane extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param component
	 */
	public OCLPMScrollPane(final JComponent component) {
		super(component);
		setOpaque(true);
		setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		setBorder(BorderFactory.createEmptyBorder());
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollBar vBar = getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		vBar = getHorizontalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
	}
	
	public OCLPMScrollPane(final JComponent component, OCLPMColors colors) {
		super(component);
		setOpaque(true);
		setBackground(colors.BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder());
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollBar vBar = getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), colors.ACCENT,
				colors.ELEMENTS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(colors.BACKGROUND);
		vBar = getHorizontalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), colors.ACCENT,
				colors.ELEMENTS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(colors.BACKGROUND);
		this.getViewport().setBackground(colors.BACKGROUND);
	}

}
