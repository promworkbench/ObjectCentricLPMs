package org.processmining.OCLPMDiscovery.plugins.exports;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
        name = "Export tagged local process model results into a file",
        returnLabels = {},
        returnTypes = {},
        parameterLabels = {"TLPMs", "Filename"})
@UIExportPlugin(
        description = "ProM tagged local process models (promtlpms) files",
        extension = "promtlpms")
public class TLPMsExportPlugin {

    @PluginVariant(variantLabel = "Export tagged local process model results into a file", requiredParameterLabels = {0, 1})
    public void export(PluginContext context, LPMResultsTagged tlpms, File file) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(tlpms);
            oos.close();
        } catch (FileNotFoundException e) {
            System.err.println("File could not be found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
