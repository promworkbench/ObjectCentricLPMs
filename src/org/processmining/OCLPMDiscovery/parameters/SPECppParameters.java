package org.processmining.OCLPMDiscovery.parameters;

import org.deckfour.xes.model.XLog;
import org.processmining.OCLPMDiscovery.specpp.CodeDefinedConfigurationSample;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.preprocessing.InputDataBundle;

public class SPECppParameters {
	
	private SPECppConfigBundle cfg;
	private InputDataBundle data;
	
	public SPECppParameters(XLog log) {
		this.cfg = CodeDefinedConfigurationSample.createConfiguration();
		this.setData(InputDataBundle.process(log, this.cfg.getInputProcessingConfig()));
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SPECppParameters that = (SPECppParameters) o;
        return true; // TODO
    }

    @Override
    public int hashCode() {
        return 1; //TODO
    }
    
	@Override
    public String toString() {
        return "Hi"; //TODO
    }

	public SPECppConfigBundle getCfg() {
		return cfg;
	}
	
	public void setCfg(SPECppConfigBundle cfg) {
		this.cfg = cfg;
	}

	public InputDataBundle getData() {
		return data;
	}

	public void setData(InputDataBundle data) {
		this.data = data;
	}
}
