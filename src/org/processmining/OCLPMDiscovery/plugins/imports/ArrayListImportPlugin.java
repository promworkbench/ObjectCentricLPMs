package org.processmining.OCLPMDiscovery.plugins.imports;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

@Plugin(
		name = "Import ArrayList from a file", 
		parameterLabels = {"Filename"}, returnLabels = {"ArrayList"}, 
		returnTypes = {ArrayList.class})
@UIImportPlugin(
		description = "Import ArrayList from a file", 
		extensions = {"list"})
public class ArrayListImportPlugin extends AbstractImportPlugin {

    protected ArrayList<String> importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
            throws Exception {

        try {
            context.getFutureResult(0).setLabel("ArrayList imported from " + filename);
        } catch (final Throwable ignored) {

        }
        
        ArrayList<String> list = new ArrayList();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));) {
            String line;
            while ((line = br.readLine()) != null) {
            	if (line.equals("") || line.equals("\n")) { // ignore empty lines
            		continue;
            	}
            	list.add(line);
            }
        }

        return list;
    }
}
