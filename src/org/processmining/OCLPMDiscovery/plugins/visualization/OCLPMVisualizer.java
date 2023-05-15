package org.processmining.OCLPMDiscovery.plugins.visualization;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.utils.OCLPMUtils;
import org.processmining.OCLPMDiscovery.visualization.components.ColorMapPanel;
import org.processmining.OCLPMDiscovery.visualization.components.ComponentFactory;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinetclassicalreductor.plugins.ReduceUsingMurataRulesPlugin;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

public class OCLPMVisualizer {

    @Plugin(name = "@0 Visualize Object-Centric Local Process Model", returnLabels = {"Visualized Object-Centric Local Process Model"},
            returnTypes = {JComponent.class}, parameterLabels = {"Object-Centric Local Process Model"}, userAccessible = false)
    @Visualizer
    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualize(PluginContext context, ObjectCentricLocalProcessModel oclpm, OCLPMResult oclpmResult) {
        if (oclpm == null)
            throw new IllegalArgumentException("The local process model to be visualized should not be null: " + oclpm);
        AcceptingPetriNet net = OCLPMUtils.getAcceptingPetriNetRepresentation(oclpm);
        ReduceUsingMurataRulesPlugin reductorPlugin = new ReduceUsingMurataRulesPlugin();
        net = reductorPlugin.runDefault(context, net);

        JComponent component = new JPanel();
        component.setLayout(new BoxLayout(component, BoxLayout.X_AXIS));
        
        int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        
        // Petri Net on the left
        component.add((new CustomAcceptingPetriNetVisualizer()).visualize(context, net, oclpmResult));

        // Component on the right
        JComponent componentRight = new JPanel();
        componentRight.setLayout(new BoxLayout(componentRight, BoxLayout.Y_AXIS));
        
        // add color legend top right
        JScrollPane colorPane = new JScrollPane(new ColorMapPanel(oclpmResult.getMapTypeColor()));
        componentRight.add(colorPane);
        
        // model stats
        JComponent evalComponent = new JPanel();
        JScrollPane evalPane = new JScrollPane(evalComponent);
        evalComponent.setLayout(new BoxLayout(evalComponent, BoxLayout.Y_AXIS));
        evalComponent.add(ComponentFactory.getComplexEvaluationResultComponent(oclpm.getAdditionalInfo().getEvaluationResult()));
        
        // ?
        evalComponent.add(new JLabel("Histogram"));
        
        componentRight.add(evalPane);
        component.add(componentRight);
        
        //===============
        // set sizes
        //===============
        // petri net
        component.getComponent(0).setPreferredSize(new Dimension(80 * windowWidth / 100, windowHeight));
        
        // right component
//        component.getComponent(1).setPreferredSize(new Dimension(20 * windowWidth / 100, windowHeight));
        componentRight.setPreferredSize(new Dimension(20 * windowWidth / 100, windowHeight));
        
        // stuff inside the right component
        colorPane.setPreferredSize(new Dimension(windowWidth, 20 * windowHeight / 100));
        evalPane.setPreferredSize(new Dimension(windowWidth, 80 * windowHeight / 100));
        
        return component;
    }
}
