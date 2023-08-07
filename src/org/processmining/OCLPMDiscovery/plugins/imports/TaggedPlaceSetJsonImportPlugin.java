package org.processmining.OCLPMDiscovery.plugins.imports;

import java.io.InputStream;

import org.processmining.OCLPMDiscovery.model.TaggedPlaceSet;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.placebasedlpmdiscovery.model.exporting.importers.JsonImporter;

@Plugin(name = "Import Set of tagged places from a json file", parameterLabels = {"Filename"}, returnLabels = {"Set of places"}, returnTypes = {TaggedPlaceSet.class})
@UIImportPlugin(description = "Import set of tagged places from a json file", extensions = {"jsontp"})
public class TaggedPlaceSetJsonImportPlugin extends AbstractImportPlugin {

    protected TaggedPlaceSet importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
            throws Exception {

        try {
            context.getFutureResult(0).setLabel("Tagged place set imported from " + filename);
        } catch (final Throwable ignored) {

        }
        JsonImporter<TaggedPlaceSet> importer = new JsonImporter<>();
        TaggedPlaceSet tps = importer.read(TaggedPlaceSet.class, input);
//        // convert from TaggedPlaceSet to PlaceSet in order to be usable with LPM discovery
//        Set<Place> places = new HashSet<Place>(tps.getElements());
//        PlaceSet placeSet = new PlaceSet(places);
        return tps;
    }
}