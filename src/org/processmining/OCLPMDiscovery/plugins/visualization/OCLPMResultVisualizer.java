package org.processmining.OCLPMDiscovery.plugins.visualization;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.visualization.components.SimpleCollectionOfElementsComponent;
import org.processmining.OCLPMDiscovery.visualization.components.tables.factories.OCLPMResultPluginVisualizerTableFactory;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;


//@Plugin(name = "@0 Visualize OCLPM Result",
//        returnLabels = {"Visualized OCLPM Result"},
//        returnTypes = {JComponent.class},
//        parameterLabels = {"Petri Net Array"})
//@Visualizer
public class OCLPMResultVisualizer {
	
//	public OCLPMColors theme = OCLPMColors.getClownMode();
//	public OCLPMColors theme = OCLPMColors.getDarkMode();
	public OCLPMColors theme = OCLPMColors.getLightMode();

	@Plugin(name = "@0 OCLPM Visualizer Light",
            returnLabels = {"Visualized OCLPM Result"},
            returnTypes = {JComponent.class},
            parameterLabels = {"Petri Net Array"})
    @Visualizer
    @PluginVariant(variantLabel = "OCLPM Visualizer Light", requiredParameterLabels = {0})
    public JComponent visualize(UIPluginContext context, OCLPMResult result) {

        if (result.size() < 1)
            return new JPanel();
        
        result.refreshColors();
        
        OCLPMResultPluginVisualizerTableFactory factory = new OCLPMResultPluginVisualizerTableFactory();
        SimpleCollectionOfElementsComponent scoec = new SimpleCollectionOfElementsComponent<>(context, result, factory, theme);
        return scoec;
    }
    
    @Plugin(name = "@1 OCLPM Visualizer Dark",
            returnLabels = {"Visualized OCLPM Result"},
            returnTypes = {JComponent.class},
            parameterLabels = {"Petri Net Array"})
    @Visualizer
    @PluginVariant(variantLabel = "OCLPM Visualizer Dark", requiredParameterLabels = {0})
    public JComponent visualizeDark(UIPluginContext context, OCLPMResult result) {
    	this.theme = OCLPMColors.getDarkMode();
    	return visualize(context,result);
    }
    
    @Plugin(name = "OCLPM Visualizer Debug",
            returnLabels = {"Visualized OCLPM Result"},
            returnTypes = {JComponent.class},
            parameterLabels = {"Petri Net Array"})
    @Visualizer
    @PluginVariant(variantLabel = "OCLPM Visualizer Clown", requiredParameterLabels = {0})
    public JComponent visualizeClown(UIPluginContext context, OCLPMResult result) {
    	this.theme = OCLPMColors.getClownMode();
    	return visualize(context,result);
    }
}

