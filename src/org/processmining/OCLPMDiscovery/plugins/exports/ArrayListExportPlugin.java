package org.processmining.OCLPMDiscovery.plugins.exports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
        name = "Export ArrayLists into a file",
        returnLabels = {},
        returnTypes = {},
        parameterLabels = {"ArrayList", "Filename"})
@UIExportPlugin(
        description = "ArrayList files (one element per line)",
        extension = "list")
public class ArrayListExportPlugin {

    @PluginVariant(variantLabel = "Export ArrayLists into a file", requiredParameterLabels = {0, 1})
    public void export(PluginContext context, ArrayList<String> list, File file) {
        try {
            FileWriter writer = new FileWriter(file);
            for (String s : list) {
            	writer.append(s+"\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("File could not be found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
