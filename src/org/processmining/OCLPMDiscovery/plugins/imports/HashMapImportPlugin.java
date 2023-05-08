package org.processmining.OCLPMDiscovery.plugins.imports;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

@Plugin(name = "Import hash map from a file", parameterLabels = {"Filename"}, returnLabels = {"Hash map"}, returnTypes = {HashMap.class})
@UIImportPlugin(description = "Import hash map from a file", extensions = {"promhm"})
public class HashMapImportPlugin extends AbstractImportPlugin {

    protected HashMap<String,String> importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
            throws Exception {

        try {
            context.getFutureResult(0).setLabel("Hash map imported from " + filename);
        } catch (final Throwable ignored) {

        }

        ObjectInputStream ois = new ObjectInputStream(input);
        Object object = ois.readObject();
        ois.close();
        if (object instanceof HashMap) {
            return (HashMap<String,String>) object;
        } else {
            System.err.println("File could not be parsed as valid HashMap object");
        }
        return null;
    }
}

