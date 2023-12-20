package org.processmining.OCLPMDiscovery.plugins.imports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

@Plugin(name = "Import OCLPMResult from a file", parameterLabels = {"Filename"}, returnLabels = {"OCLPM Result"}, returnTypes = {OCLPMResult.class})
@UIImportPlugin(description = "Import object-centric local process models from a file", extensions = {"promoclpm"})
public class OCLPMResultImportPlugin extends AbstractImportPlugin {

    protected OCLPMResult importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
            throws Exception {

        try {
            context.getFutureResult(0).setLabel("OCLPM result imported from " + filename);
        } catch (final Throwable ignored) {

        }

        ObjectInputStream ois = new ObjectInputStream(input);
        Object object = ois.readObject();
        ois.close();
        if (object instanceof OCLPMResult) {
            return (OCLPMResult) object;
        } else {
            System.err.println("File could not be parsed as valid OCLPMResult object");
        }
        return null;
    }
    
    public static OCLPMResult importFromPath (String path){
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
        if (object instanceof OCLPMResult) {
            return (OCLPMResult) object;
        } else {
            System.err.println("File could not be parsed as valid OCLPMResult object");
        }
        return null;
    }
}
