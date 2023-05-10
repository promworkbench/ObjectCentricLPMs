package org.processmining.OCLPMDiscovery.model;

import java.util.HashMap;

import org.processmining.placebasedlpmdiscovery.model.serializable.LPMResult;

/**
 * Model to combine LPMs with the case notion / type which has been used to discover them.
 * @author Marvin
 *
 */
public class LPMResultsTagged {
	
	private HashMap<LPMResult,String> typeMap;
	
	public LPMResultsTagged() {
		this.setTypeMap(new HashMap<LPMResult,String>());
	}
	
	public LPMResultsTagged(LPMResult lpmResult, String type) {
		this();
		this.getTypeMap().put(lpmResult, type);
	}

	public HashMap<LPMResult,String> getTypeMap() {
		return typeMap;
	}

	public void setTypeMap(HashMap<LPMResult,String> typeMap) {
		this.typeMap = typeMap;
	}
	
	public void put(LPMResult key, String value) {
		this.getTypeMap().put(key, value);
	}
	
	/**
	 * Get the type used as case notion for the discovery of that LPMResult
	 * @param key
	 * @return
	 */
	public String getTypeOf (LPMResult key) {
		return this.getTypeMap().get(key);
	}
	
	/**
	 * Counts the total number of local process models across all stored results.
	 * @return
	 */
	public int totalLPMs() {
		int count = 0;
		for (LPMResult cur : this.getTypeMap().keySet()) {
			count += cur.size();
		}
		return count;
	}
}
