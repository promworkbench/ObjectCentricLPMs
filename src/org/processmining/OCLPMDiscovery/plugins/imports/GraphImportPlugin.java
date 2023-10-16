package org.processmining.OCLPMDiscovery.plugins.imports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

@Plugin(
		name = "Import JGraphT graphs from a file", 
		parameterLabels = {"Filename"}, returnLabels = {"JGraphT"}, 
		returnTypes = {Graph.class})
@UIImportPlugin(
		description = "Import JGraphT graphs from a file", 
		extensions = {"jgrapht"})
public class GraphImportPlugin extends AbstractImportPlugin {

    protected Graph<String,DefaultEdge> importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
            throws Exception {

        try {
            context.getFutureResult(0).setLabel("Graph imported from " + filename);
        } catch (final Throwable ignored) {

        }

        ObjectInputStream ois = new ObjectInputStream(input);
        Object object = ois.readObject();
        ois.close();
        if (object instanceof Graph) {
            return (Graph<String,DefaultEdge>) object;
        } else {
            System.err.println("File could not be parsed as valid JGraphT object");
        }
        return null;
    }
    
    public static Graph<String,DefaultEdge> importFromPath (String path){
    	File file = new File(path);
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Object object = null;
		try {
			object = ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (object instanceof Graph) {
            return (Graph<String,DefaultEdge>) object;
        } else {
            System.err.println("File could not be parsed as valid JGraphT object");
        }
        return null;
    }
}
