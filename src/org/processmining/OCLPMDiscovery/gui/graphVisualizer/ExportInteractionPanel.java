package org.processmining.OCLPMDiscovery.gui.graphVisualizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.freehep.graphicsbase.util.export.ExportDialog;
import org.processmining.OCLPMDiscovery.gui.OCLPMButton;
import org.processmining.OCLPMDiscovery.gui.OCLPMScalableViewPanel;
import org.processmining.OCLPMDiscovery.gui.OCLPMViewInteractionPanel;
import org.processmining.framework.util.ui.scalableview.ScalableComponent;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class ExportInteractionPanel extends JPanel implements OCLPMViewInteractionPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1036741994786060955L;
	protected final OCLPMScalableViewPanel panel;
	private ScalableComponent scalable;
	private OCLPMButton exportButton;

	public ExportInteractionPanel(OCLPMScalableViewPanel panel) {
		this.panel = panel;
		double size[][] = { { 10, TableLayoutConstants.FILL, 10 }, { 10, TableLayoutConstants.FILL, 10 } };
		setLayout(new TableLayout(size));
//		exportButton = new SlickerButton("Export view...");
		exportButton = new OCLPMButton("Export view...");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		this.add(exportButton, "1, 1");
	}

	private void export() {
		ExportDialog export = new ExportDialog();
		export.showExportDialog(this, "Export view as ...", scalable.getComponent(), "View");
	}

	public void updated() {
		// TODO Auto-generated method stub

	}

	public String getPanelName() {
		return "Export";
	}

	public JComponent getComponent() {
		return this;
	}

	public void setScalableComponent(ScalableComponent scalable) {
		this.scalable = scalable;
	}

	public void setParent(OCLPMScalableViewPanel viewPanel) {
	}

	public double getHeightInView() {
		return 50;
	}

	public double getWidthInView() {
		return 100;
	}

	public void willChangeVisibility(boolean to) {
	}
}
