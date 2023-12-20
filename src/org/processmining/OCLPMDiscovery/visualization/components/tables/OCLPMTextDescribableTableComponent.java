package org.processmining.OCLPMDiscovery.visualization.components.tables;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.table.JTableHeader;

import org.processmining.OCLPMDiscovery.gui.OCLPMTable;
import org.processmining.placebasedlpmdiscovery.model.TextDescribable;

public class OCLPMTextDescribableTableComponent<T extends TextDescribable> extends OCLPMTable {

    private final Map<Integer, T> indexMap;

    public OCLPMTextDescribableTableComponent(Map<Integer, T> indexMap) {
        this.indexMap = indexMap;
    }

    public Map<Integer, T> getIndexMap() {
        return indexMap;
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(this.getTable().getColumnModel()) {
            @Override
            public String getToolTipText(MouseEvent event) {
                Point p = event.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                return columnModel.getColumn(index).getHeaderValue() + "    (Click in order to sort)";
            }
        };
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int column = this.columnAtPoint(event.getPoint());

        if (column != 1)
            return null;

        int row = this.rowAtPoint(event.getPoint());
        int ind = this.getRowSorter().convertRowIndexToModel(row);
        return this.indexMap.containsKey(ind) ?
                this.indexMap.get(ind).getShortString() : null;
    }

	public void setModel(CustomObjectTableModel<T> tableModel) {
		this.getTable().setModel(tableModel);
	}

	public void setColumnModel(VisibilityControllableTableColumnModel visibilityControllableTableColumnModel) {
		this.getTable().setColumnModel(visibilityControllableTableColumnModel);
	}

	public void createDefaultColumnsFromModel() {
		this.getTable().createDefaultColumnsFromModel();
	}

	public void setAutoCreateColumnsFromModel(boolean b) {
		this.getTable().setAutoCreateColumnsFromModel(b);
	}

	public void setFillsViewportHeight(boolean b) {
		this.getTable().setFillsViewportHeight(b);
	}
}
