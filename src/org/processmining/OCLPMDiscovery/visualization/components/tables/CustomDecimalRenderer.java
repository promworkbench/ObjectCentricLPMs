package org.processmining.OCLPMDiscovery.visualization.components.tables;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomDecimalRenderer extends DefaultTableCellRenderer {
	private DecimalFormat decimalFormat;

    public CustomDecimalRenderer() {
        super();
        // Create a DecimalFormat with a specific decimal separator (dot)
        Locale locale = new Locale("en", "US"); // Use the appropriate locale
        decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(symbols);

        setHorizontalAlignment(SwingConstants.RIGHT); // Align cell content to the right
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof Double) {
            setText(decimalFormat.format(value));
        } else {
            super.setValue(value);
        }
    }
}