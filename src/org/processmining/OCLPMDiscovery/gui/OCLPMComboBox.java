package org.processmining.OCLPMDiscovery.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.processmining.framework.util.ui.widgets.ProMComboBoxUI;
import org.processmining.framework.util.ui.widgets.WidgetColors;

@SuppressWarnings("rawtypes")
public class OCLPMComboBox<E> extends JComboBox/* <E> */{

	private static final long serialVersionUID = 1L;

	/**
	 * @param values
	 * @return
	 */
	public static <T> Object[] toArray(final Iterable<T> values) {
		final ArrayList<T> valueList = new ArrayList<T>();
		for (final T value : values) {
			valueList.add(value);
		}
		return valueList.toArray();
	}

	private final OCLPMPanel borderPanel;
	private final OCLPMPanel buttonPanel;
	
	public OCLPMComboBox(final ComboBoxModel<E> model) {
		super(model);
		borderPanel = new OCLPMPanel(15, 3);
		borderPanel.setOpaque(true);
		borderPanel.setBackground(WidgetColors.COLOR_LIST_BG);
		borderPanel.setForeground(WidgetColors.COLOR_ENCLOSURE_BG);
		buttonPanel = new OCLPMPanel(15, 3);
		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		buttonPanel.setForeground(WidgetColors.COLOR_ENCLOSURE_BG);
		setOpaque(false);
		setBackground(WidgetColors.COLOR_LIST_BG);
		setForeground(WidgetColors.COLOR_LIST_FG);
		setMinimumSize(new Dimension(200, 30));
		setMaximumSize(new Dimension(1000, 30));
		setPreferredSize(new Dimension(1000, 30));

		setUI(new ProMComboBoxUI(this));
	}
	
	@SuppressWarnings("unchecked")
	public OCLPMComboBox(final ComboBoxModel<E> model, OCLPMColors theme) {
		super(model);
		borderPanel = new OCLPMPanel(15, 3, theme);
		borderPanel.setOpaque(true);
		borderPanel.setBackground(theme.ELEMENTS);
		borderPanel.setForeground(theme.NON_FOCUS);
		buttonPanel = new OCLPMPanel(15, 3, theme);
		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(theme.NON_FOCUS);
		buttonPanel.setForeground(theme.NON_FOCUS);
		setOpaque(false);
		setBackground(theme.ELEMENTS);
		setForeground(theme.TEXT);
		setMinimumSize(new Dimension(1, 30));
		setMaximumSize(new Dimension(1000, 30));
		setPreferredSize(new Dimension(1000, 30));

		setUI(new OCLPMComboBoxUI(this, theme));
	}
	
	
	public OCLPMComboBox(final ComboBoxModel<E> model, boolean noSize) {
		this(model);
		resetSizes();
	}

	public OCLPMComboBox<E> resetSizes() {
		setPreferredSize(null);
		setMaximumSize(null);
		setMinimumSize(null);
		return this;
	}

	/**
	 * @param values
	 */
	public OCLPMComboBox(final Iterable<E> values) {
		this(OCLPMComboBox.toArray(values));
	}
	
	/**
	 * @param values
	 */
	public OCLPMComboBox(final Iterable<E> values, OCLPMColors theme) {
		this(OCLPMComboBox.toArray(values), theme);
	}

	/**
	 * @param values
	 */
	@SuppressWarnings({ "unchecked" })
	public OCLPMComboBox(final Object[] values) {
		this(new DefaultComboBoxModel<E>((E[]) values));
	}
	
	/**
	 * @param values
	 */
	@SuppressWarnings({ "unchecked" })
	public OCLPMComboBox(final Object[] values, OCLPMColors theme) {
		this(new DefaultComboBoxModel<E>((E[]) values), theme);
	}

	/**
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	public void addAllItems(final Iterable<E> values) {
		for (final E value : values) {
			addItem(value);
		}
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(final Graphics g) {
		if (!Boolean.TRUE.equals(getClientProperty("JComboBox.isTableCellEditor"))) {
			final Rectangle bounds = getBounds();
			buttonPanel.setBounds(bounds);
			buttonPanel.paintComponent(g);
			final Dimension d = new Dimension();
			d.setSize(bounds.getWidth() - bounds.getHeight(), bounds.getHeight());
			bounds.setSize(d);
			borderPanel.setBounds(bounds);
			borderPanel.paintComponent(g);
		}
		super.paintComponent(g);
	}
}
