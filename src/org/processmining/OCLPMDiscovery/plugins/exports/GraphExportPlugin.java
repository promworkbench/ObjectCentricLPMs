package org.processmining.OCLPMDiscovery.plugins.exports;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
        name = "Export JGraphT graphs into a file",
        returnLabels = {},
        returnTypes = {},
        parameterLabels = {"Graph", "Filename"})
@UIExportPlugin(
        description = "JGraphT graph files",
        extension = "jgrapht")
public class GraphExportPlugin {

    @PluginVariant(variantLabel = "Export JGraphT graphs into a file", requiredParameterLabels = {0, 1})
    public void export(PluginContext context, Graph<String,DefaultEdge> graph, File file) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(graph);
            oos.close();
        } catch (FileNotFoundException e) {
            System.err.println("File could not be found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
