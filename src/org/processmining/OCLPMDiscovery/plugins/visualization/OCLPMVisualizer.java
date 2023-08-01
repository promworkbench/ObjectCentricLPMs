package org.processmining.OCLPMDiscovery.plugins.visualization;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.processmining.OCLPMDiscovery.gui.OCLPMAutoResizeTextArea;
import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.gui.OCLPMPanel;
import org.processmining.OCLPMDiscovery.gui.OCLPMScrollPane;
import org.processmining.OCLPMDiscovery.gui.OCLPMSplitPane;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.utils.OCLPMUtils;
import org.processmining.OCLPMDiscovery.visualization.components.ColorMapPanel;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

public class OCLPMVisualizer {

    @Plugin(name = "@0 Visualize Object-Centric Local Process Model", returnLabels = {"Visualized Object-Centric Local Process Model"},
            returnTypes = {JComponent.class}, parameterLabels = {"Object-Centric Local Process Model", "OCLPM Result", "OCLPMColors"}, userAccessible = false)
    @Visualizer
    @PluginVariant(requiredParameterLabels = {0, 1})
    public JComponent visualize(PluginContext context, ObjectCentricLocalProcessModel oclpm, OCLPMResult oclpmResult, OCLPMColors theme) {
    	if (theme == null) {
    		theme = new OCLPMColors();
    	}
        if (oclpm == null)
            throw new IllegalArgumentException("The local process model to be visualized should not be null: " + oclpm);
        AcceptingPetriNet net = OCLPMUtils.getAcceptingPetriNetRepresentation(oclpm);
        
        int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        
        // Petri Net on the left
        OCLPMScrollPane petriNetPane = new OCLPMScrollPane((new CustomAcceptingPetriNetVisualizer()).visualize(context, net, oclpmResult, theme), theme);
        
        // Color Legend
        OCLPMScrollPane colorPane = new OCLPMScrollPane(new ColorMapPanel(oclpmResult.getMapTypeColor(), theme), theme);
        
        // model stats
        JComponent evalComponent = new OCLPMPanel(theme);
        OCLPMScrollPane evalPane = new OCLPMScrollPane(evalComponent, theme);
        evalComponent.setLayout(new BoxLayout(evalComponent, BoxLayout.Y_AXIS));
        OCLPMAutoResizeTextArea ta_evaluation = new OCLPMAutoResizeTextArea("", false, theme);
        
        // add for which leading type this model has been discovered
        if (oclpm.getDiscoveryTypes().size() == 1) {
        	ta_evaluation.append("Discovered using the leading type:\n"+oclpm.getDiscoveryTypes().toArray()[0]+"\n");
        }
        else if (oclpm.getDiscoveryTypes().size() > 1) {
        	ta_evaluation.append("Discovered using the leading type:\n");
        	for (Object curType : oclpm.getDiscoveryTypes().toArray()) {
        		ta_evaluation.append((String) curType+"\n");
        	}
        }
        
        //TODO oclpm evaluation 
        ta_evaluation.append("\n"+"Evaluation scores:"+"\n");
        ta_evaluation.append(oclpm.getEvaluationString());
        
        evalComponent.add(ta_evaluation);
//        evalComponent.add(ComponentFactory.getComplexEvaluationResultComponent(oclpm.getAdditionalInfo().getEvaluationResult()));
        
        
        //show OCLPMDiscovery settings used to obtain the OCLPMResult
        JComponent settingsComponent = new OCLPMPanel(theme);
        OCLPMScrollPane settingsPane = new OCLPMScrollPane(settingsComponent, theme);
        settingsComponent.setLayout(new BoxLayout(settingsComponent, BoxLayout.Y_AXIS));
//        OCLPMTextArea ta_discoverySettings = new OCLPMTextArea(oclpmResult.getOclpmDiscoverySettings(), false, false, theme);
        OCLPMAutoResizeTextArea ta_discoverySettings = new OCLPMAutoResizeTextArea("", false, theme);
        ta_discoverySettings.append(oclpmResult.getOclpmDiscoverySettings());
        settingsComponent.add(ta_discoverySettings);
        
        // Component on the right
        OCLPMSplitPane sp_R1 = new OCLPMSplitPane(JSplitPane.VERTICAL_SPLIT, theme, 0);
        OCLPMSplitPane sp_R2 = new OCLPMSplitPane(JSplitPane.VERTICAL_SPLIT, theme, 0);
        sp_R1.setTopComponent(colorPane);
        sp_R1.setBottomComponent(sp_R2);
        sp_R2.setTopComponent(evalPane);
        sp_R2.setBottomComponent(settingsPane);
        
        // outer Component
        OCLPMSplitPane componentOuter = new OCLPMSplitPane(JSplitPane.HORIZONTAL_SPLIT, theme);
        componentOuter.setLeftComponent(petriNetPane);
        componentOuter.setRightComponent(sp_R1);
        
        //===============
        // set sizes
        //===============
        // outer component        
        componentOuter.setResizeWeight(0.8);
        
        // petri net
        petriNetPane.setPreferredSize(new Dimension(80 * windowWidth / 100, windowHeight));
        
        // right component
        sp_R1.setPreferredSize(new Dimension(20 * windowWidth / 100, windowHeight));
        sp_R1.setResizeWeight(0.3);
        sp_R2.setResizeWeight(0.5);
        
        // stuff inside the right component
        colorPane.setPreferredSize(new Dimension(windowWidth, 20 * windowHeight / 100));
        evalPane.setPreferredSize(new Dimension(windowWidth, 80 * windowHeight / 100));
        
        return componentOuter;
    }
}
