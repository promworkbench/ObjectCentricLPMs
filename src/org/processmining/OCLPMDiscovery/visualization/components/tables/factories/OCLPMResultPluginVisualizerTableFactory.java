package org.processmining.OCLPMDiscovery.visualization.components.tables.factories;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.parameters.OCLPMEvaluationMetrics;
import org.processmining.OCLPMDiscovery.visualization.components.tables.CustomObjectTableModel;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableCollection;

public class OCLPMResultPluginVisualizerTableFactory extends AbstractPluginVisualizerTableFactory<ObjectCentricLocalProcessModel> {

//    private static double getResultOrDefault(ObjectCentricLocalProcessModel lpm, LPMEvaluationResultId resultId) {
//        LPMEvaluationResult result = lpm.getAdditionalInfo().getEvalResults().get(resultId);
//        if (result != null)
//            return result.getResult();
//        return -1;
//    }

    @Override
	public Map<Integer, ObjectCentricLocalProcessModel> getIndexObjectMap(SerializableCollection<ObjectCentricLocalProcessModel> elements) {
        Iterator<ObjectCentricLocalProcessModel> lpmIterator = elements.getElements().iterator();
        return IntStream
                .range(0, elements.size())
                .boxed()
                .collect(Collectors.toMap(i -> i, i -> lpmIterator.next()));
    }

    @Override
    protected CustomObjectTableModel<ObjectCentricLocalProcessModel> createTableModel(Map<Integer, ObjectCentricLocalProcessModel> indexObjectMap) {
        return new CustomObjectTableModel<>(
                indexObjectMap,
                this.getColumnNames(),
                this.getObjectToColumnsMapper());
    }

    @Override
    protected JPopupMenu getPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem exportItem = new JMenuItem("Export");
        exportItem.addActionListener(e -> {
            OCLPMResult res = new OCLPMResult();
            for (Integer ind : table.getSelectedRows()) {
                res.add(table.getIndexMap().get(table.convertRowIndexToModel(ind)));
            }
            this.listener.export(res);
        });
        popupMenu.add(exportItem);
        return popupMenu;
    }
    
    public String[] getColumnNames() {
    	return
    	new String[]{
                "LPM Index",
                "LPM Short Name",
                OCLPMEvaluationMetrics.COMBINED_SCORE.getName(), // keep at index 2 for default sorting
                OCLPMEvaluationMetrics.FITTING_WINDOWS.getName(),
                OCLPMEvaluationMetrics.PASSAGE_COVERAGE.getName(),
                OCLPMEvaluationMetrics.PASSAGE_REPETITION.getName(),
                OCLPMEvaluationMetrics.TRANSITION_COVERAGE.getName(),
                OCLPMEvaluationMetrics.TRACE_SUPPORT.getName(),
                OCLPMEvaluationMetrics.TYPE_USAGE.getName(),
                OCLPMEvaluationMetrics.NUM_TYPES.getName(),
                OCLPMEvaluationMetrics.NUM_PLACES.getName(),
                OCLPMEvaluationMetrics.NUM_TRANSITIONS.getName(),
                OCLPMEvaluationMetrics.NUM_VARIABLEARCS.getName(),
        };
    }
    
    public BiFunction<Integer, ObjectCentricLocalProcessModel, Object[]> getObjectToColumnsMapper(){
    	DecimalFormat df = new DecimalFormat("#.###");
    	return 
    		(ind, oclpm) -> new Object[]{
                ind + 1,
                oclpm.getShortString(),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.COMBINED_SCORE)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.FITTING_WINDOWS)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.PASSAGE_COVERAGE)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.PASSAGE_REPETITION)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.TRANSITION_COVERAGE)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.TRACE_SUPPORT)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.TYPE_USAGE)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.NUM_TYPES)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.NUM_PLACES)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.NUM_TRANSITIONS)),
                df.format(oclpm.getEvaluation(OCLPMEvaluationMetrics.NUM_VARIABLEARCS)),
    		};
    }
}
