package org.processmining.OCLPMDiscovery.model;

import java.io.OutputStream;
import java.util.HashMap;

import org.processmining.placebasedlpmdiscovery.model.exporting.Exportable;
import org.processmining.placebasedlpmdiscovery.model.exporting.exporters.Exporter;
import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;
import org.processmining.placebasedlpmdiscovery.model.serializable.SerializableSet;

/**
 * Model to combine LPMs with the case notion / type which has been used to discover them.
 * @author Marvin
 *
 */
public class LPMResultsTagged extends SerializableSet<TaggedLPMResult> implements Exportable<LPMResultsTagged>{
	private HashMap<String,String> extraStats = new HashMap<>();
	
	public LPMResultsTagged () {
		super();
	}
	
	public LPMResultsTagged(LPMResult lpmResult, String type) {
		super();
		this.add(new TaggedLPMResult(lpmResult,type));
	}
	
	/**
	 * Counts the total number of local process models across all stored results.
	 * @return
	 */
	public int size() {
		int count = 0;
		for (TaggedLPMResult cur : this.getElements()) {
			count += cur.size();
		}
		return count;
	}
	
	@Override
    public void export(Exporter<LPMResultsTagged> exporter, OutputStream os) {
        exporter.export(this, os);
    }

	public void add(LPMResult lpmResult, String currentType) {
		this.add(new TaggedLPMResult(lpmResult,currentType));		
	}

	public HashMap<String,String> getExtraStats() {
		return extraStats;
	}

	public void setExtraStats(HashMap<String,String> extraStats) {
		this.extraStats = extraStats;
	}
}
