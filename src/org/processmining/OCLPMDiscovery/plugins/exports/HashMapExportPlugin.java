package org.processmining.OCLPMDiscovery.plugins.exports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
        name = "Export a hash map into a file",
        returnLabels = {},
        returnTypes = {},
        parameterLabels = {"Hash Map", "Filename"})
@UIExportPlugin(
        description = "ProM hash map (promhm) file",
        extension = "promhm")
public class HashMapExportPlugin {

    @PluginVariant(variantLabel = "Export a hash map into a file", requiredParameterLabels = {0, 1})
    public static void export(PluginContext context, HashMap<String,String> hashMap, File file) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(hashMap);
            oos.close();
        } catch (FileNotFoundException e) {
            System.err.println("File could not be found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}