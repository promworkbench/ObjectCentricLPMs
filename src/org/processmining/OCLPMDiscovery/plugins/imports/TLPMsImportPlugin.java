package org.processmining.OCLPMDiscovery.plugins.imports;

import java.io.InputStream;
import java.io.ObjectInputStream;

import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

@Plugin(
		name = "Import tagged local process model results from a file", 
		parameterLabels = {"Filename"}, returnLabels = {"OCLPM Result"}, 
		returnTypes = {LPMResultsTagged.class})
@UIImportPlugin(
		description = "Import tagged local process model results from a file", 
		extensions = {"promtlpms"})
public class TLPMsImportPlugin extends AbstractImportPlugin {

    protected LPMResultsTagged importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
            throws Exception {

        try {
            context.getFutureResult(0).setLabel("TLPMs imported from " + filename);
        } catch (final Throwable ignored) {

        }

        ObjectInputStream ois = new ObjectInputStream(input);
        Object object = ois.readObject();
        ois.close();
        if (object instanceof LPMResultsTagged) {
            return (LPMResultsTagged) object;
        } else {
            System.err.println("File could not be parsed as valid LPMResultsTagged object");
        }
        return null;
    }
}
