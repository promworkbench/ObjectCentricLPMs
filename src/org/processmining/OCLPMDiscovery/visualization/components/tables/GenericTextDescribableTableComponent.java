package org.processmining.OCLPMDiscovery.visualization.components.tables;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.placebasedlpmdiscovery.model.TextDescribable;

public class GenericTextDescribableTableComponent<T extends TextDescribable> extends JTable {

    private Map<Integer, T> indexMap;

    public GenericTextDescribableTableComponent(Map<Integer, T> indexMap) {
        this.indexMap = indexMap;
    }

    public Map<Integer, T> getIndexMap() {
        return indexMap;
    }
    
    public GenericTextDescribableTableComponent(Map<Integer, T> indexMap, OCLPMColors theme) {
        this.indexMap = indexMap;
        this.setBackground(theme.BACKGROUND);
        this.setForeground(theme.TEXT);
        this.setGridColor(theme.ELEMENTS);
        this.setSelectionBackground(theme.FOCUS);
        this.setSelectionForeground(theme.TEXT_INVERS);
        this.getTableHeader().setBackground(theme.ELEMENTS);
        this.getTableHeader().setForeground(theme.TEXT);
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
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
    
    public void setIndexMap (Map<Integer, T> indexMap) {
    	this.indexMap = indexMap;
    }
}
