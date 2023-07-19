package org.processmining.OCLPMDiscovery.plugins.imports;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.processmining.OCLPMDiscovery.model.TaggedPlaceSet;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.placebasedlpmdiscovery.model.Place;
import org.processmining.placebasedlpmdiscovery.model.exporting.importers.JsonImporter;
import org.processmining.placebasedlpmdiscovery.model.serializable.PlaceSet;

@Plugin(name = "Import Set of tagged places from a json file", parameterLabels = {"Filename"}, returnLabels = {"Set of places"}, returnTypes = {PlaceSet.class})
@UIImportPlugin(description = "Import set of tagged places from a json file", extensions = {"json"})
public class TaggedPlaceSetJsonImportPlugin extends AbstractImportPlugin {

    protected PlaceSet importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
            throws Exception {

        try {
            context.getFutureResult(0).setLabel("Set of places imported from " + filename);
        } catch (final Throwable ignored) {

        }
        JsonImporter<TaggedPlaceSet> importer = new JsonImporter<>();
        TaggedPlaceSet tps = importer.read(TaggedPlaceSet.class, input);
        // convert from TaggedPlaceSet to PlaceSet in order to be usable with LPM discovery
        Set<Place> places = new HashSet<Place>(tps.getElements());
        PlaceSet placeSet = new PlaceSet(places);
        return placeSet;
    }
}