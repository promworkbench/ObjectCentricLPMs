package org.processmining.OCLPMDiscovery.plugins.imports;

import java.io.InputStream;

import org.processmining.OCLPMDiscovery.model.LPMResultsTagged;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.placebasedlpmdiscovery.model.exporting.importers.JsonImporter;

@Plugin(name = "Import Set of tagged LPMResults from a json file", parameterLabels = {"Filename"}, returnLabels = {"Tagged LPMResults"}, returnTypes = {LPMResultsTagged.class})
@UIImportPlugin(description = "Import Set of tagged LPMResults from a json file", extensions = {"json"})
public class LPMResultsTaggedJsonImporter extends AbstractImportPlugin {

    protected LPMResultsTagged importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
            throws Exception {

        try {
            context.getFutureResult(0).setLabel("Tagged LPMResults imported from " + filename);
        } catch (final Throwable ignored) {

        }
        JsonImporter<LPMResultsTagged> importer = new JsonImporter<>();
        LPMResultsTagged res = importer.read(LPMResultsTagged.class, input);
        return res;
    }
}