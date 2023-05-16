package org.processmining.OCLPMDiscovery.plugins.visualization;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

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
        
        int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        
        //TODO instead of the JScrollPanes, use nicer looking ones (ProMScrollPanes?)
        
        // Petri Net on the left
        JScrollPane petriNetPane = new JScrollPane((new CustomAcceptingPetriNetVisualizer()).visualize(context, net, oclpmResult));
//        component.add((new CustomAcceptingPetriNetVisualizer()).visualize(context, net, oclpmResult));
        
        // Color Legend
        JScrollPane colorPane = new JScrollPane(new ColorMapPanel(oclpmResult.getMapTypeColor()));
        
        // model stats
        JComponent evalComponent = new JPanel();
        JScrollPane evalPane = new JScrollPane(evalComponent);
        evalComponent.setLayout(new BoxLayout(evalComponent, BoxLayout.Y_AXIS));
        evalComponent.add(ComponentFactory.getComplexEvaluationResultComponent(oclpm.getAdditionalInfo().getEvaluationResult()));
        
        // ?
        evalComponent.add(new JLabel("Histogram"));
        
        // Component on the right
        JSplitPane componentRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        componentRight.setTopComponent(colorPane);
        componentRight.setBottomComponent(evalPane);
        
        // outer Component
        JSplitPane componentOuter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        componentOuter.setLeftComponent(petriNetPane);
        componentOuter.setRightComponent(componentRight);
        
        //===============
        // set sizes
        //===============
        // outer component        
        componentOuter.setResizeWeight(0.8);
        
        // petri net
        petriNetPane.setPreferredSize(new Dimension(80 * windowWidth / 100, windowHeight));
        
        // right component
        componentRight.setPreferredSize(new Dimension(20 * windowWidth / 100, windowHeight));
        componentRight.setResizeWeight(0.3);
        
        // stuff inside the right component
        colorPane.setPreferredSize(new Dimension(windowWidth, 20 * windowHeight / 100));
        evalPane.setPreferredSize(new Dimension(windowWidth, 80 * windowHeight / 100));
        
        return componentOuter;
    }
}
