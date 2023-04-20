package org.processmining.OCLPMDiscovery;

import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.framework.plugin.PluginContext;

public class Main {
	private static PluginContext Context;
	
	public static void setUp(PluginContext context) {
        Main.Context = context;
    }
	
	public static PluginContext getContext() {
        return Context;
    }
	
	public static Object[] run() {
        System.out.println("Hello from main run!");
        OCLPMResult result = new OCLPMResult();

        return new Object[] {result};
    }
}
