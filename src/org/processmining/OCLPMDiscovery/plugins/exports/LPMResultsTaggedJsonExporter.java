package org.processmining.OCLPMDiscovery.plugins.exports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.placebasedlpmdiscovery.model.exporting.exporters.JsonExporter;

@Plugin(
        name = "Export set of tagged LPMResults into a file",
        returnLabels = {},
        returnTypes = {},
        parameterLabels = {"Set of tagged LPMReults", "Filename"})
@UIExportPlugin(
        description = "ProM set of tagged LPMResults (json) file",
        extension = "json")
public class LPMResultsTaggedJsonExporter {

    @PluginVariant(variantLabel = "Export set of tagged LPMResults into a file", requiredParameterLabels = {0, 1})
    public static void export(PluginContext context, LPMResultsTagged results, File file) throws IOException {
        
    	JsonExporter<LPMResultsTagged> exporter = new JsonExporter<>();
    	results.export(exporter, Files.newOutputStream(file.toPath()));
    }
}
