package org.processmining.OCLPMDiscovery.visualization.components;

import flanagan.analysis.Stat;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.placebasedlpmdiscovery.Main;
import org.processmining.placebasedlpmdiscovery.analysis.statistics.LogStatistics;
import org.processmining.placebasedlpmdiscovery.analysis.statistics.Statistics;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.SimpleEvaluationResult;
import org.processmining.placebasedlpmdiscovery.model.LocalProcessModel;
import org.processmining.placebasedlpmdiscovery.plugins.visualization.components.tables.CustomObjectTableModel;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SettableComponentFactory {

    private final int windowWidth;
    private final int windowHeight;

    private LocalProcessModel lpm;
    private Statistics statistics;

    public SettableComponentFactory() {
        this.windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    }

    public JPanel getComponent(ComponentId.Type type) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        if (type.equals(ComponentId.Type.LogStatistics)) {
            panel = getLogInfo();
        } else if (type.equals(ComponentId.Type.BasicLPMEvalMetrics)) {
            panel = getSimpleEvalMetrics();
        }
        return panel;
    }

    private JPanel getSimpleEvalMetrics() {
        List<SimpleEvaluationResult> results = this.lpm.getAdditionalInfo().getEvaluationResult().getResults();
        List<Object[]> tableModel = results.stream().map(r -> new Object[]{r.getId(), r.getResult()}).collect(Collectors.toList());
        ProMTable table = new ProMTable(new DefaultTableModel(
                tableModel.toArray(new Object[tableModel.size()][2]),
                new Object[]{"Metric", "Value"})
        );
        table.setPreferredSize(new Dimension(20 * windowWidth / 100, 40 * windowHeight / 100));
        table.setMaximumSize(new Dimension(20 * windowWidth / 100, 40 * windowHeight / 100));
        return table;
    }

    private JPanel getLogInfo() {
        LogStatistics logStat = this.statistics.getLogStatistics();
        Object[][] data = new Object[][] {
                new Object[] {"Count Trace Variants", logStat.getTraceVariantsCount()},
                new Object[] {"Trace Variants Total Events", logStat.getTraceVariantsTotalEvents()},
                new Object[] {"Activities Count", logStat.getActivitiesCount()}
        };
        ProMTable table = new ProMTable(new DefaultTableModel(data, new Object[] {"Stat Description", "Value"}));
        table.setPreferredSize(new Dimension(20 * windowWidth / 100, 40 * windowHeight / 100));
        table.setMaximumSize(new Dimension(20 * windowWidth / 100, 40 * windowHeight / 100));
        return table;
    }

    public void setSelectedLpm(LocalProcessModel lpm) {
        this.lpm = lpm;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
