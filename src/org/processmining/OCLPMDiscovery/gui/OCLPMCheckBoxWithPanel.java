package org.processmining.OCLPMDiscovery.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author michael
 * ProMCheckBoxWithPanel with some changes for size adjustments
 */
public class OCLPMCheckBoxWithPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1605514658715195558L;
	private final JCheckBox checkBox;
	private final JPanel panel;

	/**
	 * 
	 */
	public OCLPMCheckBoxWithPanel() {
		this(true, true);
	}

	/**
	 * @param checked
	 */
	public OCLPMCheckBoxWithPanel(final boolean checked) {
		this(checked, true);
	}

	/**
	 * @param checked
	 * @param hideIfNotChecked
	 * @param prefSizeX preferred size of the panel (default: 530)
	 */
	public OCLPMCheckBoxWithPanel(final boolean checked, final boolean hideIfNotChecked, int prefSizeX, int minSizeX, int maxSizeX) {

		panel = new JPanel();
		checkBox = SlickerFactory.instance().createCheckBox("", checked);

		if (hideIfNotChecked) {
			checkBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					toggleVisibility();
				}
			});
		}

		setLayout(new BorderLayout());
		setOpaque(false);

		checkBox.setPreferredSize(new Dimension(30, 30));
		checkBox.setMinimumSize(checkBox.getPreferredSize());
		checkBox.setMaximumSize(checkBox.getPreferredSize());

		panel.setPreferredSize(new Dimension(prefSizeX, 30)); // default 530
		panel.setMinimumSize(new Dimension(minSizeX,30)); // wasn't there in the proM version
		panel.setMaximumSize(new Dimension(maxSizeX,30)); // wasn't there in the proM version

		this.add(checkBox, BorderLayout.WEST);
		this.add(Box.createHorizontalGlue());
		this.add(panel, BorderLayout.EAST);
	}
	
	public OCLPMCheckBoxWithPanel(final boolean checked, final boolean hideIfNotChecked, int prefSizeX) {
		this(checked, hideIfNotChecked, prefSizeX, 20 , 530);
	}
	
	/**
	 * @param checked
	 * @param hideIfNotChecked
	 */
	public OCLPMCheckBoxWithPanel(final boolean checked, final boolean hideIfNotChecked) {
		this(checked, hideIfNotChecked, 530, 530, 530);
	}

	/**
	 * @return
	 */
	public JCheckBox getCheckBox() {
		return checkBox;
	}

	/**
	 * @return
	 */
	public boolean isSelected() {
		return checkBox.isSelected();
	}

	/**
	 * @param checked
	 */
	public void setSelected(final boolean checked) {
		checkBox.setSelected(checked);
	}

	private void toggleVisibility() {
		panel.setVisible(!panel.isVisible());
	}

	protected JPanel getPanel() {
		return panel;
	}
}
