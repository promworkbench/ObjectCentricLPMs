package org.processmining.OCLPMDiscovery.plugins.visualization;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.gui.OCLPMEditorPane;
import org.processmining.OCLPMDiscovery.gui.OCLPMPanel;
import org.processmining.OCLPMDiscovery.gui.OCLPMScrollPane;
import org.processmining.OCLPMDiscovery.gui.OCLPMSplitPane;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.visualization.components.ColorMapPanel;
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
        
        int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        
        // Petri Net on the left
        OCLPMScrollPane petriNetPane = new OCLPMScrollPane((new CustomAcceptingPetriNetVisualizer()).visualize(context, oclpm, oclpmResult, theme), theme);
        
        // Color Legend
        OCLPMScrollPane colorPane = new OCLPMScrollPane(new ColorMapPanel(oclpmResult.getMapTypeColor(), theme), theme);
        
        
        // Set the default font in the StyleSheet
        HTMLEditorKit editorKit = new HTMLEditorKit();
        StyleSheet styleSheet = editorKit.getStyleSheet();
        styleSheet.addRule("body { font-family: Arial, sans-serif; }");
        
        
        // model stats
        JComponent evalComponent = new OCLPMPanel(theme);
        OCLPMScrollPane evalPane = new OCLPMScrollPane(evalComponent, theme);
        evalComponent.setLayout(new BoxLayout(evalComponent, BoxLayout.Y_AXIS));
        OCLPMEditorPane ta_evaluation = new OCLPMEditorPane("", false, true, theme);
        ta_evaluation.setEditorKit(editorKit);
        String evalHtml = "<html><body>";
        
        // add for which leading type this model has been discovered
        if (oclpm.getDiscoveryTypes().size() == 1) {
        	evalHtml += "<b>Discovered using the leading type:</b><br>"+oclpm.getDiscoveryTypes().toArray()[0]+"<br>";
        }
        else if (oclpm.getDiscoveryTypes().size() > 1) {
        	evalHtml += "<b>Discovered using the leading types:</b><br>";
        	for (Object curType : oclpm.getDiscoveryTypes().toArray()) {
        		evalHtml += (String) curType+"<br>";
        	}
        }
        
        // oclpm evaluation 
        evalHtml += " <br>"+"<b>Evaluation scores:</b>"+"<br>";
        evalHtml += oclpm.getEvaluationStringHTML();
        
        ta_evaluation.setText(evalHtml);
        evalComponent.add(ta_evaluation);
        evalComponent.setPreferredSize(ta_evaluation.getPreferredSize());
//        evalComponent.setPreferredSize(new Dimension(windowWidth, 80 * windowHeight / 100));
//        evalComponent.add(ComponentFactory.getComplexEvaluationResultComponent(oclpm.getAdditionalInfo().getEvaluationResult()));
        
        
        //show OCLPMDiscovery settings used to obtain the OCLPMResult
        JComponent settingsComponent = new OCLPMPanel(theme);
        OCLPMScrollPane settingsPane = new OCLPMScrollPane(settingsComponent, theme);
        settingsComponent.setLayout(new BoxLayout(settingsComponent, BoxLayout.Y_AXIS));
        OCLPMEditorPane ta_discoverySettings = new OCLPMEditorPane("", false, true, theme);
        ta_discoverySettings.setEditorKit(editorKit);
        String settingsHtml = "<html><body>";
        settingsHtml += oclpmResult.getOclpmDiscoverySettingsHTMLBody();
        
        String executionTimesHtml = " <br>"+"<b>Execution Times:</b>"+"<br>";
        executionTimesHtml += "Discovery starting with " + oclpmResult.getTimeStartingVariant() + ": " + oclpmResult.getExecutionTimeMinutes() + " minutes <br>";
        if (oclpmResult.getExecutionTimePlaceCompletion() != 0)
        	executionTimesHtml += "Place Completion: "+ oclpmResult.getExecutionTimePlaceCompletion() + " ms <br>";
        if (oclpmResult.getExecutionTimeExternalObjectFlow() != 0)
        	executionTimesHtml += "External Object Flow: "+ oclpmResult.getExecutionTimeExternalObjectFlow() + " ms <br>";
        
        ta_discoverySettings.setText(settingsHtml + executionTimesHtml + "</body></html>");
        
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
