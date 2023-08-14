/**
 * 
 */
package org.processmining.OCLPMDiscovery.visualization.components.shapes;

import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

import org.processmining.models.shapes.AbstractShape;

public class RoundedRect extends AbstractShape {
	private double roundness = 0.125;
	
	public RoundedRect () {
		
	}
	
	public RoundedRect (double roundness) {
		super();
		this.roundness = roundness;
	}

	public GeneralPath getPath(double x, double y, double width, double height) {
		double m = Math.max(width, height) * this.roundness;

		// Width and height have correct ratio;
		GeneralPath path = new GeneralPath();

		// main border
		java.awt.Shape rect = new RoundRectangle2D.Double(x, y, width, height, m, m);
		path.append(rect, false);

		return path;
	}

}
