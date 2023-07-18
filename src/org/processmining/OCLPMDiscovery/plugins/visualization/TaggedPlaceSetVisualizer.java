package org.processmining.OCLPMDiscovery.plugins.visualization;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.gui.OCLPMPanel;
import org.processmining.OCLPMDiscovery.gui.OCLPMTextArea;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.visualization.components.SimpleCollectionOfElementsComponent;
import org.processmining.OCLPMDiscovery.visualization.components.tables.factories.OCLPMResultPluginVisualizerTableFactory;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;

public class TaggedPlaceSetVisualizer {
	
//	public OCLPMColors theme = OCLPMColors.getClownMode();
//	public OCLPMColors theme = OCLPMColors.getDarkMode();
	public OCLPMColors theme = OCLPMColors.getLightMode();

	@Plugin(name = "@0 Tagged PlaceSet visalizer",
            returnLabels = {"Visualized Tagged PlaceSet"},
            returnTypes = {JComponent.class},
            parameterLabels = {"TaggedPlace Array"})
    @Visualizer
    @PluginVariant(variantLabel = "Tagged PlaceSet visalizer", requiredParameterLabels = {0})
    public JComponent visualize(UIPluginContext context, PlaceSet placeSet) {

        if (placeSet.size() < 1) {
        	OCLPMPanel panel = new OCLPMPanel(theme);
	    	panel.add(new OCLPMTextArea("The given PlaceSet doesn't contain any places.",theme));
	        return panel;
        }
	        
        if (!(placeSet.getList().getElement(0) instanceof TaggedPlace)) {
        	OCLPMPanel panel = new OCLPMPanel(theme);
        	panel.add(new OCLPMTextArea("The given PlaceSet doesn't contain tagged places.",theme));
            return panel;
        }
        
        OCLPMResult result = new OCLPMResult(placeSet);
        
        OCLPMResultPluginVisualizerTableFactory factory = new OCLPMResultPluginVisualizerTableFactory();
        SimpleCollectionOfElementsComponent scoec = new SimpleCollectionOfElementsComponent<>(context, result, factory, theme);
        return scoec;
    }
}

