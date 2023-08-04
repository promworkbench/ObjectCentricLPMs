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
import org.processmining.OCLPMDiscovery.visualization.components.tables.CustomObjectTableModel;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.LPMEvaluationResult;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.LPMEvaluationResultId;
import org.processmining.placebasedlpmdiscovery.lpmevaluation.results.aggregateoperations.EvaluationResultAggregateOperation;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableCollection;
import org.processmining.placebasedlpmdiscovery.utils.LocalProcessModelUtils;

public class OCLPMResultPluginVisualizerTableFactory extends AbstractPluginVisualizerTableFactory<ObjectCentricLocalProcessModel> {

    private static double getResultOrDefault(ObjectCentricLocalProcessModel lpm, LPMEvaluationResultId resultId) {
        LPMEvaluationResult result = lpm.getAdditionalInfo().getEvalResults().get(resultId);
        if (result != null)
            return result.getResult();
        return -1;
    }

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
        DecimalFormat df = new DecimalFormat("#.###");
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
//                "Transition Overlapping Score",
                "Transition Coverage Score",
                "Fitting Window Score",
                "Passage Coverage Score",
                "Passage Repetition Score",
                "Trace Support",
                "Aggregate Result"
        };
    }
    
    public BiFunction<Integer, ObjectCentricLocalProcessModel, Object[]> getObjectToColumnsMapper(){
    	DecimalFormat df = new DecimalFormat("#.###");
    	return 
    		(ind, oclpm) -> new Object[]{
                ind + 1,
                oclpm.getShortString(),
//                df.format(getResultOrDefault(lpm, LPMEvaluationResultId.TransitionOverlappingEvaluationResult)),
                df.format(getResultOrDefault(oclpm, LPMEvaluationResultId.TransitionCoverageEvaluationResult)),
                df.format(getResultOrDefault(oclpm, LPMEvaluationResultId.FittingWindowsEvaluationResult)),
                df.format(getResultOrDefault(oclpm, LPMEvaluationResultId.PassageCoverageEvaluationResult)),
                df.format(getResultOrDefault(oclpm, LPMEvaluationResultId.PassageRepetitionEvaluationResult)),
                df.format(getResultOrDefault(oclpm, LPMEvaluationResultId.TraceSupportEvaluationResult)),
//                df.format(lpm.getAdditionalInfo().getEvaluationResult().getResult(new EvaluationResultAggregateOperation()))
                df.format(LocalProcessModelUtils.getGroupedEvaluationResult(oclpm.getLpm()).getResult(new EvaluationResultAggregateOperation()))
    		};
    }
}
