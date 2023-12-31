package org.processmining.tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.gui.OCLPMPanel;
import org.processmining.OCLPMDiscovery.gui.OCLPMScrollPane;
import org.processmining.OCLPMDiscovery.gui.OCLPMSplitPane;
import org.processmining.OCLPMDiscovery.gui.OCLPMToggleButton;
import org.processmining.OCLPMDiscovery.visualization.components.ColorMapPanel;

public class TestColorMapPanel {
	public static void main(String[] args) {
		HashMap<String, Color> colorMap = new HashMap<>();
		OCLPMColors colorTheme = OCLPMColors.getLightMode();
		OCLPMColors theme = colorTheme;
//		colorTheme = OCLPMColors.getDarkMode();
		Boolean useLaF = false;
		
		if (useLaF) {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            UIManager.put("nimbusBase", colorTheme.ELEMENTS); // scroll bar
		    		UIManager.put("nimbusBlueGrey", Color.PINK); // dividers and scroll bar background
		    		UIManager.put("control", colorTheme.BACKGROUND);
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
			try {
				UIManager.setLookAndFeel(
//					UIManager.getCrossPlatformLookAndFeelClassName()
					UIManager.getSystemLookAndFeelClassName()
				);
			} 
			catch(Exception e2) {
				System.out.println("Failed setting the look and feel.");
			}
		}
		}
		
		// generate colors
		int numTypes = 10;
		float saturation = 1.0f;
		float luminance = 1f;
		float hueStart = 0f;
		float hueEnd = 255f;
		float hue = hueStart;
		Color[] colors = new Color[numTypes];
		for (int i=0; i<numTypes; i++) {
			hue = hueStart + ((hueEnd-hueStart)/numTypes)*i; // hue in [0,255] but java uses a value in [0,1] to get the 360 degree angle for the hue circle
			hue = hue/255f; 
			colors[i] = Color.getHSBColor(hue, saturation, luminance);
			colorMap.put("Test"+i, colors[i]);
		}
//		colorMap.put("Red", Color.RED);
//		colorMap.put("Green", Color.GREEN);
//		colorMap.put("Blue", Color.BLUE);
		ColorMapPanel colorMapPanel = new ColorMapPanel(colorMap, colorTheme);
		
		
		// ToggleButton
		OCLPMToggleButton expandBtn = new OCLPMToggleButton(theme); // create an expand/shrink button
        expandBtn.setText("Expand"); // in the beginning set the text to Expand
        expandBtn.setSelected(false); // and selected to false

		// set the preferred dimension of the two containers
        int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        
        JFrame frame = new JFrame("Color Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// outer container
		Container container = frame.getContentPane();
		container.setPreferredSize(new Dimension(windowWidth, windowHeight));
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		
		// left container
		OCLPMPanel panelLeft = new OCLPMPanel(colorTheme);
		panelLeft.setPreferredSize(new Dimension(80 * windowWidth / 100, windowHeight));
//		container.add(panelLeft);
		
		OCLPMScrollPane colorPane = new OCLPMScrollPane(colorMapPanel, colorTheme);
		colorPane.setPreferredSize(new Dimension(20 * windowWidth / 100, 20 * windowHeight / 100));
		
		OCLPMPanel panelRightBottom = new OCLPMPanel(colorTheme);
		panelRightBottom.setPreferredSize(new Dimension(20 * windowWidth / 100, 80 * windowHeight / 100));
		panelRightBottom.add(expandBtn, BorderLayout.PAGE_END);
		
		// Right split pane
		OCLPMSplitPane splitPaneRight = new OCLPMSplitPane(JSplitPane.VERTICAL_SPLIT, colorTheme);
		splitPaneRight.setPreferredSize(new Dimension(20 * windowWidth / 100, windowHeight));
		splitPaneRight.setResizeWeight(0.3);
		splitPaneRight.setTopComponent(colorPane);
		splitPaneRight.setBottomComponent(panelRightBottom);
		container.add(splitPaneRight);
		
		// outer split pane
		OCLPMSplitPane splitPaneOuter = new OCLPMSplitPane(OCLPMSplitPane.HORIZONTAL_SPLIT, colorTheme);
//		splitPaneOuter.setPreferredSize(new Dimension(20 * windowWidth / 100, windowHeight));
		splitPaneOuter.setResizeWeight(0.8);
		splitPaneOuter.setLeftComponent(panelLeft);
		splitPaneOuter.setRightComponent(splitPaneRight);
		container.add(splitPaneOuter);
		
//		frame.setPreferredSize(new Dimension(15 * frame.getContentPane().getWidth(), frame.getContentPane().getHeight() / 20));
//		frame.setPreferredSize(new Dimension(100, 200));
//		SwingUtilities.updateComponentTreeUI(frame);
		frame.pack();
		frame.setVisible(true);
	}
}
