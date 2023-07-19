package org.processmining.OCLPMDiscovery.plugins.exports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.processmining.OCLPMDiscovery.model.TaggedPlaceSet;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.placebasedlpmdiscovery.model.exporting.exporters.JsonExporter;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;

@Plugin(
        name = "Export set of tagged places into a file",
        returnLabels = {},
        returnTypes = {},
        parameterLabels = {"Set of tagged places", "Filename"})
@UIExportPlugin(
        description = "ProM set of tagged places (json) file",
        extension = "json")
public class TaggedPlaceSetJsonExportPlugin {

    @PluginVariant(variantLabel = "Export set of tagged places into a file", requiredParameterLabels = {0, 1})
    public static void export(PluginContext context, PlaceSet placeSet, File file) throws IOException {
        // convert PlaceSet to TaggedPlaceSet
    	TaggedPlaceSet tps = new TaggedPlaceSet(placeSet);
    	
    	JsonExporter<TaggedPlaceSet> exporter = new JsonExporter<>();
        tps.export(exporter, Files.newOutputStream(file.toPath()));
    }
}
