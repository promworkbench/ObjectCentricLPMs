package org.processmining.OCLPMDiscovery.gui;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import org.processmining.framework.util.ui.widgets.ProMComboBoxPopup;

public class OCLPMComboBoxUI extends BasicComboBoxUI {
	/**
	 * @param c
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static ComponentUI createUI(final JComponent c, OCLPMColors theme) {
		return new OCLPMComboBoxUI((JComboBox/* <?> */) c, theme);
	}

	@SuppressWarnings("rawtypes")
	private final JComboBox/* <?> */component;
	private OCLPMColors theme = OCLPMColors.getLightMode();

	/**
	 * @param c
	 */
	@SuppressWarnings("rawtypes")
	public OCLPMComboBoxUI(final JComboBox/* <?> */c, OCLPMColors theme) {
		component = c;
		this.theme=theme;
	}

	/**
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#configureArrowButton()
	 */
	@Override
	public void configureArrowButton() {
		super.configureArrowButton();
		arrowButton.setBorder(BorderFactory.createEmptyBorder());
	}

	/**
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#paintCurrentValueBackground(java.awt.Graphics,
	 *      java.awt.Rectangle, boolean)
	 */
	@Override
	public void paintCurrentValueBackground(final Graphics g, final Rectangle bounds, final boolean hasFocus) {
	}

	@Override
	protected void configureEditor() {
		super.configureEditor();
		if (editor instanceof JComponent) {
			((JComponent) editor).setBorder(BorderFactory.createEmptyBorder());
			((JComponent) editor).setBackground(this.theme.ELEMENTS);
			((JComponent) editor).setForeground(this.theme.TEXT);
		}
	}
	
	@Override
	protected JButton createArrowButton() {
		final JButton button = new BasicArrowButton(SwingConstants.SOUTH, this.theme.NON_FOCUS,
				this.theme.NON_FOCUS, this.theme.TEXT, this.theme.NON_FOCUS);
		button.setName("ComboBox.arrowButton");
		return button;
	}

	@Override
	protected ComboPopup createPopup() {
		final BasicComboPopup result = new ProMComboBoxPopup(component); //TODO make own OCLPMComboBoxPopup
		return result;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		comboBox.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
	}

	@Override
	protected Rectangle rectangleForCurrentValue() {
		final int width = comboBox.getWidth();
		final int height = comboBox.getHeight();
		final Insets insets = getInsets();
		int buttonSize = height - (insets.top + insets.bottom);
		if (arrowButton != null) {
			buttonSize = arrowButton.getWidth();
		}
		return new Rectangle(insets.left + 3, insets.top - 1, width
				- (insets.left + insets.right + buttonSize + 3 + 3 + 10), height - (insets.top + insets.bottom) + 1);
	}

}
