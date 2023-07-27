package org.processmining.OCLPMDiscovery.visualization.components.tables;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serializable;
import java.util.Map;
import java.util.function.BiFunction;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.OCLPMDiscovery.gui.OCLPMColors;
import org.processmining.OCLPMDiscovery.gui.OCLPMComboBox;
import org.processmining.OCLPMDiscovery.gui.OCLPMPanel;
import org.processmining.OCLPMDiscovery.gui.OCLPMScrollPane;
import org.processmining.OCLPMDiscovery.gui.OCLPMToggleButton;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.ObjectCentricLocalProcessModel;
import org.processmining.OCLPMDiscovery.parameters.PlaceCompletion;
import org.processmining.OCLPMDiscovery.utils.PlaceCompletionUtils;
import org.processmining.OCLPMDiscovery.visualization.components.ComponentId;
import org.processmining.OCLPMDiscovery.visualization.components.ICommunicativePanel;
import org.processmining.OCLPMDiscovery.visualization.components.tables.factories.AbstractPluginVisualizerTableFactory;
import org.processmining.OCLPMDiscovery.visualization.components.tables.factories.OCLPMResultPluginVisualizerTableFactory;
import org.processmining.placebasedlpmdiscovery.model.TextDescribable;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableCollection;
import org.processmining.placebasedlpmdiscovery.plugins.visualization.utils.RegexConverter;

public class TableComposition<T extends TextDescribable & Serializable> extends JComponent implements ICommunicativePanel {

    private final ComponentId componentId;
    private final OCLPMResult result;
    private final AbstractPluginVisualizerTableFactory<T> tableFactory;
    private final TableListener<T> controller;
    private OCLPMColors theme = new OCLPMColors();

    public TableComposition(OCLPMResult result,
                            AbstractPluginVisualizerTableFactory<T> tableFactory,
                            TableListener<T> controller) {
        this.componentId = new ComponentId(ComponentId.Type.TableComponent);
        this.result = result;
        this.tableFactory = tableFactory;
        this.controller = controller;

        init();
    }
    
    public TableComposition(OCLPMResult result,
            AbstractPluginVisualizerTableFactory<T> tableFactory,
            TableListener<T> controller,
            OCLPMColors theme) {
		this.componentId = new ComponentId(ComponentId.Type.TableComponent);
		this.result = result;
		this.tableFactory = tableFactory;
		this.controller = controller;
		this.theme = theme;
		
		init();
	}

    private void init() {
        this.setLayout(new BorderLayout());

        // create the table
        GenericTextDescribableTableComponent<T> table = this.tableFactory.getPluginVisualizerTable((SerializableCollection<T>) this.result, controller, this.theme);
        OCLPMScrollPane scrollPane = new OCLPMScrollPane(table, theme); // add the table in a scroll pane

        // create the filter form
        OCLPMPanel filterForm = new OCLPMPanel(this.theme);
        filterForm.setLayout(new FlowLayout());
        filterForm.add(new JLabel("Filter:"));
        filterForm.add(createRowFilter(table));
        filterForm.setVisible(false);

        // make it expandable
        OCLPMToggleButton expandBtn = new OCLPMToggleButton(this.theme); // create an expand/shrink button
        expandBtn.setText("Expand"); // in the beginning set the text to Expand
        expandBtn.setSelected(false); // and selected to false
        expandBtn.setCornerRadius(0);
        // when the button state is selected the table is shown entirely and the text is Shrink,
        // and when is not selected only the first column is shown and the text is Expand
        expandBtn.addActionListener(actionEvent -> {
            if (expandBtn.isSelected()) { // if the new state is selected
                expandBtn.setText("Shrink"); // set the text to Shrink
                ((VisibilityControllableTableColumnModel) table.getColumnModel()).setAllColumnsVisible(); // show all columns
                controller.componentExpansion(componentId, true);
                filterForm.setVisible(true);
            } else { // otherwise
                expandBtn.setText("Expand"); // set the text to Expand
                ((VisibilityControllableTableColumnModel) table.getColumnModel()).keepOnlyFirstColumn(); // keep only the first column
                controller.componentExpansion(componentId, false);
                filterForm.setVisible(false);
            }
        });
        
        // place completion label
        JLabel placeCompletionLabel = new JLabel("Place Completion:");
        placeCompletionLabel.setMinimumSize(new Dimension(1,30));
        
        // place completion box
        OCLPMComboBox placeCompletionBox = new OCLPMComboBox(PlaceCompletion.values(), this.theme);
        placeCompletionBox.addActionListener(actionEvent -> {
        	OCLPMResult newResult = PlaceCompletionUtils.completePlacesCopy(this.result, (PlaceCompletion) placeCompletionBox.getSelectedItem());
        	
        	// show all columns (otherwise it doesn't work)
            ((VisibilityControllableTableColumnModel) table.getColumnModel()).setAllColumnsVisible(); // show all columns
            
        	// insert new data into table and refresh
        	OCLPMResultPluginVisualizerTableFactory oclpmFactory = (OCLPMResultPluginVisualizerTableFactory) this.tableFactory;
        	Map<Integer, ObjectCentricLocalProcessModel> indexObjectMap = oclpmFactory.getIndexObjectMap(newResult);
        	table.setIndexMap((Map<Integer, T>) indexObjectMap);
        	BiFunction<Integer, ObjectCentricLocalProcessModel, Object[]> objectToColumnsMapper = oclpmFactory.getObjectToColumnsMapper();
        	Object[][] tableData;
        	if (indexObjectMap.size() <= 0)
                tableData = new Object[0][0];
        	else {
        		tableData = indexObjectMap.entrySet().stream().map(
        				entry -> {
	                        int ind = entry.getKey();
	                        ObjectCentricLocalProcessModel obj = entry.getValue();
	                        return objectToColumnsMapper.apply(ind, obj);
        				}
        			).toArray(Object[][]::new);
        	}
        	String[] columnNames = this.tableFactory.getColumnNames();
        	((DefaultTableModel) table.getModel()).setDataVector(tableData, columnNames);
        	
        	((DefaultTableModel) table.getModel()).fireTableDataChanged();
        	table.changeSelection(0, 0, false, false);
        	
        	// if table isn't expanded only show first column
        	if (!expandBtn.isSelected()) {
        		((VisibilityControllableTableColumnModel) table.getColumnModel()).keepOnlyFirstColumn(); // keep only the first column
        	}
        });
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,0,0,0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Component spans the whole row
        
        gbc.gridx = 0;
        gbc.weightx = 1.0; // Allow horizontal resizing
        gbc.anchor = GridBagConstraints.WEST; // Left-align the components
        gbc.weighty = 0.0;
        
        gbc.gridy = 0;
        this.add(filterForm, gbc); // add the filter field in the table container
        
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(scrollPane, gbc); // add the scroll pane in the table container
        
        gbc.weighty = 0.0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(expandBtn, gbc); // add the expand/shrink button in the table container
        
        gbc.gridy = 3;
        this.add(placeCompletionLabel, gbc);
        
        gbc.gridy = 4;
        this.add(placeCompletionBox, gbc);
    }


    /**
     * Creating the text field for the table filter
     *
     * @param table: the table for which the filter is created
     * @return created text field that will be used to filer the values in the table
     */
    private JTextField createRowFilter(JTable table) {
        RowSorter<? extends TableModel> rs = table.getRowSorter();
        if (rs == null) {
            table.setAutoCreateRowSorter(true);
            rs = table.getRowSorter();
        }

        TableRowSorter<? extends TableModel> rowSorter =
                (rs instanceof TableRowSorter) ? (TableRowSorter<? extends TableModel>) rs : null;

        if (rowSorter == null) {
            throw new RuntimeException("Cannot find appropriate rowSorter: " + rs);
        }

        final JTextField tf = new JTextField(50);
        tf.setToolTipText("Available operators are: 'and', 'or' and '{}'. It doesn't support nested parenthesis.");
        
        tf.setBackground(this.theme.BACKGROUND);
        tf.setForeground(this.theme.TEXT);

        tf.addActionListener(actionEvent -> {
            String text = tf.getText();
            RegexConverter regexConverter = new RegexConverter();
            String regex = regexConverter.getRegex(text);
            System.out.println(regex);
            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter(regex, 1));
            }
        });

        return tf;
    }

    @Override
    public ComponentId getComponentId() {
        return this.componentId;
    }
}
