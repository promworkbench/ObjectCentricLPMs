package org.processmining.OCLPMDiscovery.plugins.visualization;

import java.awt.Color;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.gui.OCLPMGraphPanel;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.visualization.components.OCLPMGraphVisualizer;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

public class CustomAcceptingPetriNetVisualizer {

    @Plugin(name = "@0 Visualize Accepting Petri Net",
            returnLabels = {"Visualized Accepting Petri Net"},
            returnTypes = {JComponent.class},
            parameterLabels = {"Accepting Petri Net"},
            userAccessible = false)
    @Visualizer
    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualize(PluginContext context, AcceptingPetriNet net, OCLPMResult oclpmResult) {
    	return this.visualize(context, net, oclpmResult, OCLPMColors.getLightMode());
    }
    
    public JComponent visualize(PluginContext context, AcceptingPetriNet net, OCLPMResult oclpmResult, OCLPMColors theme) {
        ViewSpecificAttributeMap map = new ViewSpecificAttributeMap();        
        
        //! this Marking-Color part doesn't work because the place color is changed again below to fit the theme
        for (Place place : net.getInitialMarking().baseSet()) {
            map.putViewSpecific(place, AttributeMap.FILLCOLOR, new Color(127, 0, 0));
        }
        for (Marking marking : net.getFinalMarkings()) {
            for (Place place : marking.baseSet()) {
                if (net.getInitialMarking().baseSet().contains(place)) {
                    map.putViewSpecific(place, AttributeMap.FILLCOLOR, new Color(127, 0, 127));
                } else {
                    map.putViewSpecific(place, AttributeMap.FILLCOLOR, new Color(0, 0, 127));
                }
            }
        }
        for (Transition t : net.getNet().getTransitions()) {
            if (!t.isInvisible()) {
                // map.putViewSpecific(t, AttributeMap.LABEL, lpm.getTransitions().get(t.getLabel()).getLabel());
                map.putViewSpecific(t, AttributeMap.LABEL, t.getLabel());
            }
            map.putViewSpecific(t, AttributeMap.STROKECOLOR, theme.TEXT); // color of border
            map.putViewSpecific(t, AttributeMap.LABELCOLOR, Color.PINK); // doesn't do anything
            // TODO change text color to theme.TEXT
            map.putViewSpecific(t, AttributeMap.EDGECOLOR, Color.PINK); // doesn't do anything
        }
        
        // color places based on object type
        for (Place p: net.getNet().getPlaces()) {
        	//! cannot convert from this place to taggedPlace so I need a Map from id to color for this.
        	
        	if (!oclpmResult.getMapIdColor().containsKey(p.getLabel())) {
        		System.out.println("Color map doesn't contain the place id "+p.getLabel());
        	}
        	// p.getId() is a new random id for the petri net node! 
        	// p.getLabel() is the id that is set when creating the node.
        	// p.getLabel() is now the object type...
        	// This would all be simpler if I could just change the id of a place to any String!
        	// ah I changed it back again
        	map.putViewSpecific(p, AttributeMap.FILLCOLOR, oclpmResult.getMapIdColor().get(p.getLabel()));
        	map.putViewSpecific(p, AttributeMap.STROKECOLOR, theme.TEXT);
        }
        
        String type, activity;
        for (PetrinetEdge arc : net.getNet().getEdges()) {
        	Place p;
        	// stuff that works:
//        	map.putViewSpecific(arc, AttributeMap.LINEWIDTH, 2f); // this works!
//        	map.putViewSpecific(arc, AttributeMap.EDGECOLOR, Color.GREEN); // this works!
//        	map.putViewSpecific(arc, AttributeMap.TOOLTIP, "Variable Arc of type "+type);
//        	map.putViewSpecific(arc, AttributeMap.LABEL, "variable");
//        	map.putViewSpecific(arc, AttributeMap.SHOWLABEL, true);
        	
        	
        	// stuff that doesn't work:
//        	map.putViewSpecific(arc, AttributeMap.FILLCOLOR, Color.PINK);
//        	map.putViewSpecific(arc, AttributeMap.EDGEEND, ArrowType.ARROWTYPE_CIRCLE);
//        	map.putViewSpecific(arc, AttributeMap.NUMLINES, 3);
//        	map.putViewSpecific(arc, AttributeMap.SHAPE, ArrowType.ARROWTYPE_DOUBLELINE);
//        	map.putViewSpecific(arc, AttributeMap.EDGEMIDDLE, ArrowType.ARROWTYPE_DOUBLELINE);
//        	map.putViewSpecific(arc, AttributeMap.STROKECOLOR, Color.BLUE);
//        	map.putViewSpecific(arc, AttributeMap.LABELALONGEDGE, "variable arc");
        	
        	if (arc.getSource() instanceof Transition) {
        		activity = ((Transition)(arc.getSource())).getLabel();
        		p = (Place) arc.getTarget();
        		type = oclpmResult.getTypeMap().get(p.getLabel());
        	}
        	else {
        		p = (Place) arc.getSource();
        		type = oclpmResult.getTypeMap().get(p.getLabel());
        		activity = ((Transition)(arc.getTarget())).getLabel();
        	}
        	
        	// show object type of place when hovering over it
        	map.putViewSpecific(p, AttributeMap.TOOLTIP, type);
        	
        	map.putViewSpecific(arc, AttributeMap.EDGECOLOR, oclpmResult.getMapTypeColor().get(type)); // color arc according to connected place
        	
        	// variable arcs
        	if (oclpmResult.getVariableArcActivities().get(p.getLabel()).contains(activity)) {
        		map.putViewSpecific(arc, AttributeMap.TOOLTIP, "Variable Arc");
        		map.putViewSpecific(arc, AttributeMap.LINEWIDTH, 2.5f);
//        		map.putViewSpecific(arc, AttributeMap.LABEL, "variable");
//        		map.putViewSpecific(arc, AttributeMap.SHOWLABEL, true);
        		map.putViewSpecific(arc, AttributeMap.EDGECOLOR, oclpmResult.getMapTypeColor().get(type).darker());
        		
        		// change the variable edge to be recognizable
        			// made them darker and thicker...

        		// doesn't work:
//        		map.putViewSpecific(arc, AttributeMap.SHAPE, ArrowType.ARROWTYPE_DOUBLELINE);
//        		map.putViewSpecific(arc, AttributeMap.SHAPEDECORATOR, true);
//        		map.putViewSpecific(arc, AttributeMap.STYLE, ArrowType.ARROWTYPE_DOUBLELINE);
//        		map.putViewSpecific(arc, AttributeMap.BORDERWIDTH, 4.2f);
//        		map.putViewSpecific(arc, AttributeMap.STROKECOLOR, Color.PINK);
//        		map.putViewSpecific(arc, AttributeMap.STROKE, 4);
//        		map.putViewSpecific(arc, AttributeMap.DASHPATTERN, 4);
//        		map.putViewSpecific(arc, AttributeMap.INSET, 4);
//        		map.putViewSpecific(arc, AttributeMap.DASHOFFSET, 4);
//        		map.putViewSpecific(arc, AttributeMap.EDGESTART, ArrowType.ARROWTYPE_DOUBLELINE);
//        		map.putViewSpecific(arc, AttributeMap.EDGESTARTFILLED, false);
//        		map.putViewSpecific(arc, AttributeMap.EDGEMIDDLEFILLED, false);
//        		map.putViewSpecific(arc, AttributeMap.EDGEENDFILLED, false);
//        		map.putViewSpecific(arc, AttributeMap.ICON, ArrowType.ARROWTYPE_DOUBLELINE);
//        		map.putViewSpecific(arc, AttributeMap.LABELVERTICALALIGNMENT, true);
//        		map.putViewSpecific(arc, AttributeMap.LABELALONGEDGE, "testing");
//        		map.putViewSpecific(arc, AttributeMap.POLYGON_POINTS, 5);
        	}
        	else {
        		map.putViewSpecific(arc, AttributeMap.TOOLTIP, "Normal Arc");
        	}
        }
        
        // change general color theme
        OCLPMGraphPanel panel = OCLPMGraphVisualizer.instance().visualizeGraph(context, net.getNet(), map, theme); 
//        panel.getComponent().setBackground(theme.BACKGROUND);

        return panel;
    }
}
