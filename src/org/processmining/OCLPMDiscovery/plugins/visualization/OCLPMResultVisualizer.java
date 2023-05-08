package org.processmining.OCLPMDiscovery.plugins.visualization;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.visualization.components.SimpleCollectionOfElementsComponent;
import org.processmining.OCLPMDiscovery.visualization.components.tables.factories.OCLPMResultPluginVisualizerTableFactory;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;


@Plugin(name = "@0 Visualize OCLPM Result",
        returnLabels = {"Visualized OCLPM Result"},
        returnTypes = {JComponent.class},
        parameterLabels = {"Petri Net Array"})
@Visualizer
public class OCLPMResultVisualizer {

    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualize(UIPluginContext context, OCLPMResult result) {

        if (result.size() < 1)
            return new JPanel();
        
        OCLPMResultPluginVisualizerTableFactory factory = new OCLPMResultPluginVisualizerTableFactory();
        SimpleCollectionOfElementsComponent scoec = new SimpleCollectionOfElementsComponent<>(context, result, factory);
        return scoec;
    }
}

