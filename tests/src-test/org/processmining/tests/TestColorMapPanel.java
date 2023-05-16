package org.processmining.tests;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.processmining.OCLPMDiscovery.visualization.components.ColorMapPanel;

public class TestColorMapPanel {
	public static void main(String[] args) {
		HashMap<String, Color> colorMap = new HashMap<>();
		
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
		ColorMapPanel colorMapPanel = new ColorMapPanel(colorMap);

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
		JPanel panelLeft = new JPanel();
		panelLeft.setPreferredSize(new Dimension(80 * windowWidth / 100, windowHeight));
//		container.add(panelLeft);
		
		JScrollPane colorPane = new JScrollPane(colorMapPanel);
		colorPane.setPreferredSize(new Dimension(20 * windowWidth / 100, 20 * windowHeight / 100));
		
		JPanel panelRightBottom = new JPanel();
		panelRightBottom.setPreferredSize(new Dimension(20 * windowWidth / 100, 80 * windowHeight / 100));
		
		// Right split pane
		JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPaneRight.setPreferredSize(new Dimension(20 * windowWidth / 100, windowHeight));
		splitPaneRight.setResizeWeight(0.3);
		splitPaneRight.setTopComponent(colorPane);
		splitPaneRight.setBottomComponent(panelRightBottom);
		container.add(splitPaneRight);
		
		// outer split pane
		JSplitPane splitPaneOuter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//		splitPaneOuter.setPreferredSize(new Dimension(20 * windowWidth / 100, windowHeight));
		splitPaneOuter.setResizeWeight(0.8);
		splitPaneOuter.setLeftComponent(panelLeft);
		splitPaneOuter.setRightComponent(splitPaneRight);
		container.add(splitPaneOuter);
		
//		frame.setPreferredSize(new Dimension(15 * frame.getContentPane().getWidth(), frame.getContentPane().getHeight() / 20));
//		frame.setPreferredSize(new Dimension(100, 200));
		frame.pack();
		frame.setVisible(true);
	}
}
