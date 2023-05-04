package org.processmining.OCLPMDiscovery.gui;

import java.awt.BorderLayout;

/**
 * @author 
 * 
 */
public class OCLPMCheckBoxWithTextField extends OCLPMCheckBoxWithPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3331182375174654223L;
	private final OCLPMTextField textField;

	/**
	 * 
	 */
	public OCLPMCheckBoxWithTextField() {
		this(true, true, "");
	}

	/**
	 * @param checked
	 * @param hideIfNotSelected
	 * @param text
	 */
	public OCLPMCheckBoxWithTextField(final boolean checked, final boolean hideIfNotSelected, final String text) {
		super(checked, hideIfNotSelected);

		textField = new OCLPMTextField(text);
		getPanel().setLayout(new BorderLayout());
		getPanel().add(textField, BorderLayout.CENTER);

	}
	
	/**
	 * @param checked
	 * @param hideIfNotSelected
	 * @param text
	 * @param prefSizeX Preferred size of the panel (default: 530).
	 *  Suggestion: prefSizeX = 530 - (nameLabelSize-150) = 680-nameLabelSize
	 */
	public OCLPMCheckBoxWithTextField(final boolean checked, final boolean hideIfNotSelected, final String text, int prefSizeX) {
		super(checked, hideIfNotSelected, prefSizeX);

		textField = new OCLPMTextField(text);
		getPanel().setLayout(new BorderLayout());
		getPanel().add(textField, BorderLayout.CENTER);

	}
	
	/**
	 * @param checked
	 * @param hideIfNotSelected
	 * @param text
	 * @param adjustToName 
	 * 		Set to true if it should take the suggested size depending on the nameLabelSize (from addProperty)
	 * @param nameLabelSize 
	 * 		The size given to addProperty
	 */
	public OCLPMCheckBoxWithTextField(final boolean checked, final boolean hideIfNotSelected, final String text, boolean adjustToName, int nameLabelSize) {
		this(checked, hideIfNotSelected, text, 680-nameLabelSize);
	}

	/**
	 * @param checked
	 * @param text
	 */
	public OCLPMCheckBoxWithTextField(final boolean checked, final String text) {
		this(checked, true, text);
	}

	/**
	 * @param text
	 */
	public OCLPMCheckBoxWithTextField(final String text) {
		this(true, true, text);
	}

	/**
	 * @return
	 */
	public String getText() {
		return textField.getText();
	}
	
	public OCLPMTextField getTextField() {
		return this.textField;
	}

	/**
	 * @param text
	 */
	public void setText(final String text) {
		textField.setText(text);
	}

}
