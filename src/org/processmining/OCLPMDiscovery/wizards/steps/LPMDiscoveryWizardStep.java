package org.processmining.OCLPMDiscovery.wizards.steps;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import org.processmining.OCLPMDiscovery.gui.OCLPMPropertiesPanel;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.framework.util.ui.widgets.ProMComboCheckBox;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.placebasedlpmdiscovery.prom.plugins.mining.PlaceBasedLPMDiscoveryParameters;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.factory.SlickerFactory;

// copy from placebasedlpmdiscovery wizard steps
public class LPMDiscoveryWizardStep extends OCLPMPropertiesPanel implements ProMWizardStep<OCLPMDiscoveryParameters> {

    private static final String TITLE = "LPM Discovery Configuration";

//    private final ProMTextField lpmCount;
    private final ProMTextField placeLimit;
    private final ProMTextField timeLimit;

    private final NiceIntegerSlider proximitySlider;

    private final NiceIntegerSlider minPlacesSlider;
    private final NiceIntegerSlider maxPlacesSlider;

    private final NiceIntegerSlider minTransitionsSlider;
    private final NiceIntegerSlider maxTransitionsSlider;

    private final NiceIntegerSlider concurrencyCardinality;

    //    private final ProMComboCheckBox filteringComponent;
    private final ProMComboCheckBox activitiesComponent;

    public LPMDiscoveryWizardStep(OCLPMDiscoveryParameters parameters) {
        super(TITLE);

//        lpmCount = new ProMTextField("100",
//                "Number of local process models that will be returned");
//        addProperty("LPM Count", lpmCount);
//        lpmCount.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent keyEvent) {
//                if (!Character.isDigit(keyEvent.getKeyChar()) &&
//                        !(keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
//                    keyEvent.consume();
//                }
//            }
//
//            @Override
//            public void keyPressed(KeyEvent keyEvent) {
//                if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_V) {
//                    keyEvent.consume();
//                }
//            }
//
//            @Override
//            public void keyReleased(KeyEvent keyEvent) {
//
//            }
//        });

        placeLimit = new ProMTextField("50",
                "Number of places that will be used for building the LPMs");
        addProperty("Place Limit", placeLimit);
        placeLimit.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (!Character.isDigit(keyEvent.getKeyChar()) &&
                        !(keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
                    keyEvent.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_V) {
                    keyEvent.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        timeLimit = new ProMTextField("3",
                "Maximum minutes that the algorithm can run");
        addProperty("Time Limit (in minutes)", timeLimit);
        timeLimit.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (!Character.isDigit(keyEvent.getKeyChar()) &&
                        !(keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
                    keyEvent.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_V) {
                    keyEvent.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        minPlacesSlider = SlickerFactory.instance()
                .createNiceIntegerSlider("",
                        2, 5, 2, NiceSlider.Orientation.HORIZONTAL);
        addProperty("Min Places", minPlacesSlider);

        maxPlacesSlider = SlickerFactory.instance()
                .createNiceIntegerSlider("",
                        3, 10, 5, NiceSlider.Orientation.HORIZONTAL);
        addProperty("Max Places", maxPlacesSlider);

        minPlacesSlider.addChangeListener(e -> {
            if (minPlacesSlider.getValue() > maxPlacesSlider.getValue())
                minPlacesSlider.setValue(maxPlacesSlider.getValue());
        });

        maxPlacesSlider.addChangeListener(e -> {
            if (maxPlacesSlider.getValue() < minPlacesSlider.getValue())
                maxPlacesSlider.setValue(minPlacesSlider.getValue());
        });

        minTransitionsSlider = SlickerFactory.instance()
                .createNiceIntegerSlider("",
                        3, 10, 3, NiceSlider.Orientation.HORIZONTAL);
        addProperty("Min Transitions", minTransitionsSlider);

        maxTransitionsSlider = SlickerFactory.instance()
                .createNiceIntegerSlider("",
                        5, 15, 10, NiceSlider.Orientation.HORIZONTAL);
        addProperty("Max Transitions", maxTransitionsSlider);

        minTransitionsSlider.addChangeListener(e -> {
            if (minTransitionsSlider.getValue() > maxTransitionsSlider.getValue())
                minTransitionsSlider.setValue(maxTransitionsSlider.getValue());
        });

        maxTransitionsSlider.addChangeListener(e -> {
            if (maxTransitionsSlider.getValue() < minTransitionsSlider.getValue())
                maxTransitionsSlider.setValue(minTransitionsSlider.getValue());
        });

        proximitySlider = SlickerFactory.instance()
                .createNiceIntegerSlider("",
                        5, 20, 7, NiceSlider.Orientation.HORIZONTAL);
        addProperty("Proximity Size", proximitySlider);

        concurrencyCardinality = SlickerFactory.instance()
                .createNiceIntegerSlider("", 1, 5, 1, NiceSlider.Orientation.HORIZONTAL);
        addProperty("Concurrency Cardinality", concurrencyCardinality);

//        filteringComponent = new ProMComboCheckBox(LPMFilterId.values(), true);
//        addProperty("Filtering Strategies", filteringComponent);

        activitiesComponent = new ProMComboCheckBox(parameters.getActivities().toArray(new String[0]), true);
        addProperty("Activity Selection", activitiesComponent);
    }

    @Override
    public OCLPMDiscoveryParameters apply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        if (!canApply(parameters, jComponent))
            return parameters;
        parameters.getPBLPMDiscoveryParameters().getPlaceChooserParameters().setChosenActivities(activitiesComponent.getSelectedItems());
//        placeBasedLPMDiscoveryParameters.getLpmFilterParameters()
//                .setLPMFilterIds(filteringComponent.getSelectedItems());
//        parameters.getPBLPMDiscoveryParameters().setLpmCount(Integer.parseInt(lpmCount.getText()));
        parameters.getPBLPMDiscoveryParameters().getPlaceChooserParameters().setPlaceLimit(Integer.parseInt(placeLimit.getText()));
        parameters.getPBLPMDiscoveryParameters().setTimeLimit(Long.parseLong(timeLimit.getText()) * 60000);
        parameters.getPBLPMDiscoveryParameters().getLpmCombinationParameters().setMaxNumPlaces(maxPlacesSlider.getValue());
        parameters.getPBLPMDiscoveryParameters().getLpmCombinationParameters().setMinNumPlaces(minPlacesSlider.getValue());
        parameters.getPBLPMDiscoveryParameters().getLpmCombinationParameters().setMinNumTransitions(minTransitionsSlider.getValue());
        parameters.getPBLPMDiscoveryParameters().getLpmCombinationParameters().setMaxNumTransitions(maxTransitionsSlider.getValue());
        parameters.getPBLPMDiscoveryParameters().getLpmCombinationParameters().setLpmProximity(proximitySlider.getValue());
        parameters.getPBLPMDiscoveryParameters().getLpmCombinationParameters().setConcurrencyCardinality(concurrencyCardinality.getValue());
        return parameters;
    }

    @Override
    public boolean canApply(OCLPMDiscoveryParameters parameters, JComponent jComponent) {
        return jComponent instanceof LPMDiscoveryWizardStep;
    }

    @Override
    public JComponent getComponent(OCLPMDiscoveryParameters parameters) {
    	PlaceBasedLPMDiscoveryParameters model = parameters.getPBLPMDiscoveryParameters();
        this.activitiesComponent.addSelectedItems(model.getPlaceChooserParameters().getChosenActivities());
//        this.filteringComponent.addSelectedItems(model.getLpmFilterParameters().getLPMFilterIds());
//        this.lpmCount.setText(String.valueOf(model.getLpmCount()));
        this.placeLimit.setText(String.valueOf(model.getPlaceChooserParameters().getPlaceLimit()));
        this.timeLimit.setText(String.valueOf(model.getTimeLimit() / 60000));
        this.maxPlacesSlider.setValue(model.getLpmCombinationParameters().getMaxNumPlaces());
        this.minPlacesSlider.setValue(model.getLpmCombinationParameters().getMinNumPlaces());
        this.minTransitionsSlider.setValue(model.getLpmCombinationParameters().getMinNumTransitions());
        this.maxTransitionsSlider.setValue(model.getLpmCombinationParameters().getMaxNumTransitions());
        this.proximitySlider.setValue(model.getLpmCombinationParameters().getLpmProximity());
        this.concurrencyCardinality.setValue(model.getLpmCombinationParameters().getConcurrencyCardinality());
        return this;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
