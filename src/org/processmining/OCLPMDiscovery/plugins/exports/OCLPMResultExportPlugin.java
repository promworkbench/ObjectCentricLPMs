package org.processmining.OCLPMDiscovery.plugins.exports;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
        name = "Export object-centric local process models into a file",
        returnLabels = {},
        returnTypes = {},
        parameterLabels = {"OCLPMResult", "Filename"})
@UIExportPlugin(
        description = "ProM object-centric local process models (promoclpm) files",
        extension = "promoclpm")
public class OCLPMResultExportPlugin {

    @PluginVariant(variantLabel = "Export object-centric local process models into a file", requiredParameterLabels = {0, 1})
    public void export(PluginContext context, OCLPMResult oclpmResult, File file) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(oclpmResult);
            oos.close();
        } catch (FileNotFoundException e) {
            System.err.println("File could not be found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
