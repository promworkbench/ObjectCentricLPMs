package org.processmining.OCLPMDiscovery.visualization.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.gui.OCLPMSplitPane;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.model.TaggedPlace;
import org.processmining.OCLPMDiscovery.plugins.visualization.OCLPMVisualizer;
import org.processmining.OCLPMDiscovery.plugins.visualization.TaggedPlaceVisualizer;
import org.processmining.OCLPMDiscovery.visualization.components.tables.TableComposition;
import org.processmining.OCLPMDiscovery.visualization.components.tables.TableListener;
import org.processmining.OCLPMDiscovery.visualization.components.tables.factories.AbstractPluginVisualizerTableFactory;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.placebasedlpmdiscovery.model.TextDescribable;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableCollection;
import org.processmining.plugins.utils.ProvidedObjectHelper;

public class SimpleCollectionOfElementsComponent<T extends TextDescribable & Serializable>
        extends OCLPMSplitPane implements TableListener<T>, ComponentListener {

    private final UIPluginContext context;
    private final OCLPMResult result;
    private final AbstractPluginVisualizerTableFactory<T> tableFactory;
    private OCLPMColors theme = new OCLPMColors();

    private JComponent visualizerComponent;

    public SimpleCollectionOfElementsComponent(UIPluginContext context,
    											OCLPMResult result,
                                               AbstractPluginVisualizerTableFactory<T> tableFactory) {
        this.context = context;
        this.result = result;
        this.tableFactory = tableFactory;
        init();
    }
    
    public SimpleCollectionOfElementsComponent(UIPluginContext context,
												OCLPMResult result,
												AbstractPluginVisualizerTableFactory<T> tableFactory,
												OCLPMColors theme) {
    	super(JSplitPane.HORIZONTAL_SPLIT, theme, 0);
		this.context = context;
		this.result = result;
		this.tableFactory = tableFactory;
		this.theme = theme;
		init();
	}

    private void init() {

        // create the table and LPM visualization containers
        visualizerComponent = createVisualizerComponent();
        JComponent tableContainer = new TableComposition<>(this.result, this.result.copyForPlaceCompletion(), this.tableFactory, this, theme);

        // set the preferred dimension of the two containers
        int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        tableContainer.setMinimumSize(new Dimension (0,0));
        tableContainer.setPreferredSize(new Dimension(15 * windowWidth / 100, windowHeight));
        visualizerComponent.setPreferredSize(new Dimension(85 * windowWidth / 100, windowHeight)); // doesn't really work well
        this.setResizeWeight(0.10); // weighs which side should be larger

        // add the table and LPM visualization containers
        this.setLeftComponent(tableContainer);
        this.setRightComponent(visualizerComponent);
    }

    private JComponent createVisualizerComponent() {
        JComponent lpmVisualizerComponent = new JPanel();
        lpmVisualizerComponent.setLayout(new BorderLayout());
        return lpmVisualizerComponent;
    }

    @Override
    public void newSelection(T selectedObject) {
        // if in the visualizer component there is an LPM drawn
        if (visualizerComponent.getComponents().length >= 1)
            visualizerComponent.remove(0); // remove it

        if (selectedObject instanceof ObjectCentricLocalProcessModel) {
            // create the visualizer
            OCLPMVisualizer visualizer = new OCLPMVisualizer();
            // add visualization for the newly selected LPM
            ObjectCentricLocalProcessModel oclpm = (ObjectCentricLocalProcessModel) selectedObject;
            visualizerComponent.add(
                    visualizer.visualize(context, oclpm, this.result, theme),
                    BorderLayout.CENTER);
        }

        if (selectedObject instanceof TaggedPlace) {
            // create the visualizer
            TaggedPlaceVisualizer visualizer = new TaggedPlaceVisualizer();
            // add visualization for the newly selected Place
            TaggedPlace place = (TaggedPlace) selectedObject;
            visualizerComponent.add(
                    visualizer.visualize(context, place, this.result),
                    BorderLayout.CENTER);
        }

        visualizerComponent.revalidate(); // revalidate the component
    }

    @Override
    public void export(SerializableCollection<T> collection) {
        if (collection instanceof LPMResult) {
            LPMResult lpmResult = (LPMResult) collection;
            context.getProvidedObjectManager()
                    .createProvidedObject("Collection exported from LPM Discovery plugin", lpmResult, LPMResult.class, context);
            ProvidedObjectHelper.setFavorite(context, lpmResult);
        }

        if (collection instanceof PlaceSet) {
            PlaceSet places = (PlaceSet) collection;
            context.getProvidedObjectManager()
                    .createProvidedObject("Collection exported from LPM Discovery plugin", places, PlaceSet.class, context);
            ProvidedObjectHelper.setFavorite(context, places);
        }
    }

    @Override
    public void componentExpansion(ComponentId componentId, boolean expanded) {
        // change visibility of lpm container
//        this.visualizerComponent.setVisible(!expanded);
        
        // reset split position
        if (!expanded) {
        	this.setDividerLocation(0.10);
        }
        if (expanded) {
        	this.setDividerLocation(1.0);
        }
    }
}
