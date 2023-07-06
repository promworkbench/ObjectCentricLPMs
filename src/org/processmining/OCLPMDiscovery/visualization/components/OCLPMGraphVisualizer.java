package org.processmining.OCLPMDiscovery.visualization.components;

import java.util.Collection;
import java.util.Map;

import javax.swing.SwingConstants;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.gui.OCLPMGraphPanel;
import org.processmining.OCLPMDiscovery.gui.graphVisualizer.ExportInteractionPanel;
import org.processmining.OCLPMDiscovery.gui.graphVisualizer.PIPInteractionPanel;
import org.processmining.OCLPMDiscovery.gui.graphVisualizer.ZoomInteractionPanel;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionID;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.scalableview.ScalableViewPanel;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

/*
 * Altered version of: org.processmining.models.jgraph.ProMJGraphVisualizer;
 */
public class OCLPMGraphVisualizer {

	protected OCLPMGraphVisualizer() {
	};

	private static OCLPMGraphVisualizer instance = null;

	public static OCLPMGraphVisualizer instance() {
		if (instance == null) {
			instance = new OCLPMGraphVisualizer();
		}
		return instance;
	}

	protected GraphLayoutConnection findConnection(PluginContext context, DirectedGraph<?, ?> graph) {
		return findConnection(context.getConnectionManager(), graph);
	}

	protected GraphLayoutConnection findConnection(ConnectionManager manager, DirectedGraph<?, ?> graph) {
		Collection<ConnectionID> cids = manager.getConnectionIDs();
		for (ConnectionID id : cids) {
			Connection c;
			try {
				c = manager.getConnection(id);
			} catch (ConnectionCannotBeObtained e) {
				continue;
			}
			if (c != null && !c.isRemoved() && c instanceof GraphLayoutConnection
					&& c.getObjectWithRole(GraphLayoutConnection.GRAPH) == graph) {
				return (GraphLayoutConnection) c;
			}
		}
		return null;
	}

	public OCLPMGraphPanel visualizeGraphWithoutRememberingLayout(DirectedGraph<?, ?> graph) {
		return visualizeGraph(new GraphLayoutConnection(graph), null, graph, new ViewSpecificAttributeMap());
	}

	public OCLPMGraphPanel visualizeGraphWithoutRememberingLayout(DirectedGraph<?, ?> graph,
			ViewSpecificAttributeMap map) {
		return visualizeGraph(new GraphLayoutConnection(graph), null, graph, map);
	}

	public OCLPMGraphPanel visualizeGraph(PluginContext context, DirectedGraph<?, ?> graph) {
		return visualizeGraph(findConnection(context, graph), context, graph, new ViewSpecificAttributeMap());
	}

	public OCLPMGraphPanel visualizeGraph(PluginContext context, DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map, OCLPMColors theme) {
		return visualizeGraph(findConnection(context, graph), context, graph, map, theme);
	}
	
	public OCLPMGraphPanel visualizeGraph(PluginContext context, DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map) {
		return visualizeGraph(findConnection(context, graph), context, graph, map);
	}

	private OCLPMGraphPanel visualizeGraph(GraphLayoutConnection layoutConnection, PluginContext context,
			DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map) {
		return visualizeGraph(layoutConnection, context, graph, map, OCLPMColors.getLightMode());
	}
	
	private OCLPMGraphPanel visualizeGraph(GraphLayoutConnection layoutConnection, PluginContext context,
			DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map, OCLPMColors theme) {
		boolean newConnection = false;
		if (layoutConnection == null) {
			layoutConnection = createLayoutConnection(graph);
			newConnection = true;
		}

		if (!layoutConnection.isLayedOut()) {
			// shown for the first time.
			layoutConnection.expandAll();
		}
		//		graph.signalViews();

		ProMGraphModel model = new ProMGraphModel(graph);
		ProMJGraph jgraph;
		/*
		 * Make sure that only a single ProMJGraph is created at every time.
		 * The underlying JGrpah code cannot handle creating multiple creations at the same time.
		 */
		synchronized (instance) {
			jgraph = new ProMJGraph(model, map, layoutConnection);
		}

		JGraphLayout layout = getLayout(map.get(graph, AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));

		if (!layoutConnection.isLayedOut()) {

			JGraphFacade facade = new JGraphFacade(jgraph);

			facade.setOrdered(false);
			facade.setEdgePromotion(true);
			facade.setIgnoresCellsInGroups(false);
			facade.setIgnoresHiddenCells(false);
			facade.setIgnoresUnconnectedCells(false);
			facade.setDirected(true);
			facade.resetControlPoints();
			if (layout instanceof JGraphHierarchicalLayout) {
				facade.run((JGraphHierarchicalLayout) layout, true);
			} else {
				facade.run(layout, true);
			}

			Map<?, ?> nested = facade.createNestedMap(true, true);

			jgraph.getGraphLayoutCache().edit(nested);
//			jgraph.repositionToOrigin();
			layoutConnection.setLayedOut(true);

		}

		jgraph.setUpdateLayout(layout);

		OCLPMGraphPanel panel = new OCLPMGraphPanel(jgraph, theme);

		panel.addViewInteractionPanel(new PIPInteractionPanel(panel), SwingConstants.NORTH);
		panel.addViewInteractionPanel(new ZoomInteractionPanel(panel, ScalableViewPanel.MAX_ZOOM), SwingConstants.WEST);
		panel.addViewInteractionPanel(new ExportInteractionPanel(panel), SwingConstants.SOUTH);

		layoutConnection.updated();

		if (newConnection) {
			context.getConnectionManager().addConnection(layoutConnection);
		}

		return panel;

	}

	private GraphLayoutConnection createLayoutConnection(DirectedGraph<?, ?> graph) {
		GraphLayoutConnection c = new GraphLayoutConnection(graph);
		return c;
	}

	protected JGraphLayout getLayout(int orientation) {
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		layout.setDeterministic(true);
		layout.setCompactLayout(true);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(15);
		layout.setFixRoots(false);
		
	
		layout.setOrientation(orientation);

		return layout;
	}

}

