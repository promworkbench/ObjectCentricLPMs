package org.processmining.OCLPMDiscovery.plugins.visualization;

import java.awt.Font;
import java.awt.Insets;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.placebasedlpmdiscovery.model.additionalinfo.Passage;
import org.processmining.placebasedlpmdiscovery.model.additionalinfo.PlaceAdditionalInfo;

public class TaggedPlaceVisualizer {

    private static final Font LABEL_FONT = new Font(Font.DIALOG, Font.PLAIN, 20);

    @Plugin(name = "@0 Visualize Tagged Place", returnLabels = {"Visualized Place"},
            returnTypes = {JComponent.class},
            parameterLabels = {"Place"}, userAccessible = false)
    @Visualizer
    @PluginVariant(requiredParameterLabels = {0})
    public JComponent visualize(UIPluginContext context, TaggedPlace place, OCLPMResult result) {
        if (place == null)
            throw new IllegalArgumentException("The local process model to be visualized should not be null: " + place);

        JComponent component = new JPanel();
        component.setLayout(new BoxLayout(component, BoxLayout.X_AXIS));
        component.add((new CustomAcceptingPetriNetVisualizer()).visualize(context, new ObjectCentricLocalProcessModel(place), result));
        component.add(getPlaceAdditionalInfoComponent(place.getAdditionalInfo()));
        return component;
    }

    private JComponent getPlaceAdditionalInfoComponent(PlaceAdditionalInfo placeAdditionalInfo) {
        if (placeAdditionalInfo == null || placeAdditionalInfo.getPassageUsage() == null)
            return new JPanel();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 30)));

        for (Map.Entry<Passage, Integer> entry :
                placeAdditionalInfo.getPassageUsage().entrySet()) {
            JLabel label = new JLabel(entry.getKey() + " : " + entry.getValue());
            label.setFont(LABEL_FONT);
            panel.add(label);
        }

        return panel;
    }
}
