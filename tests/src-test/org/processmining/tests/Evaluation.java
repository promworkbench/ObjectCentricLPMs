package org.processmining.tests;

import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.TaggedPlaceSet;
import org.processmining.OCLPMDiscovery.parameters.CaseNotionStrategy;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.plugins.imports.GraphImportPlugin;
import org.processmining.OCLPMDiscovery.plugins.imports.TaggedPlaceSetJsonImportPlugin;
import org.processmining.OCLPMDiscovery.plugins.mining.OCLPMDiscoveryPlugin;
import org.processmining.ocel.importers.JSONImporter;
import org.processmining.ocel.ocelobjects.OcelEventLog;

public class Evaluation {

	private static String basePath = 
			"C:\\Users\\Marvin\\OneDrive - Students RWTH Aachen University\\Uni\\Master-Thesis\\Event-Logs\\"; 
	private static String csvName = 
			"evaluation.csv";
	private static String placeSetName = 
			"placeSet_tau09.jsontp";
	private static String graphName = 
			"graph.jgrapht";
	
	public static void main(String[] args) {
		
		try {
			
			orderManagementTests();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished evaluation.");
	}
		
	private static void orderManagementTests() throws IOException {
		
		String folder = "Evaluation_OrderManagement";
		String ocelName = "OrderManagementLog.jsonocel";
		
		String ocelPath = basePath + folder + "\\" + ocelName;
		String placeSetPath = basePath + folder + "\\" + placeSetName;
		String graphPath = basePath + folder + "\\" + graphName;

		// imports
		OcelEventLog ocel = new JSONImporter(ocelPath).doImport();
		TaggedPlaceSet placeSet = TaggedPlaceSetJsonImportPlugin.importFromPath(placeSetPath);
		Graph<String,DefaultEdge> graph = GraphImportPlugin.importFromPath(graphPath);

		// prepare csv
		FileWriter writer = new FileWriter(basePath + folder + "\\" + csvName);
		writer.append("Case Notion Strategy, Runtime [minutes], Models, Cases, Replication Factor, Avg, Min, 1st Quartile, Median, 3rd Quartile, Max\n");
		
		OCLPMDiscoveryParameters parameters = new OCLPMDiscoveryParameters(ocel);
		parameters.setComputeExtraStats(true);
		OCLPMResult oclpmResult;		
		
		parameters.setCaseNotionStrategy(CaseNotionStrategy.PE_CONNECTED);
		oclpmResult = OCLPMDiscoveryPlugin.mineOCLPMs(parameters, ocel, placeSet, graph);
		writeExtraStats(writer, oclpmResult, "Connected Components");
		
//		parameters.setCaseNotionStrategy(CaseNotionStrategy.PE_LEADING_O2);
//		parameters.setLeadingType("customers");
//		oclpmResult = OCLPMDiscoveryPlugin.mineOCLPMs(parameters, ocel, placeSet, graph);
//		writeExtraStats(writer, oclpmResult, "LT-O2 customers");
		
		writer.close();
	}
	
	private static void writeExtraStats(FileWriter writer, OCLPMResult oclpmResult, String rowName) throws IOException {
		writer.append(rowName+", "); // Case Notion Strategy
		writer.append(oclpmResult.getExecutionTimeMinutes()+", "); // Runtime [minutes]
		writer.append(oclpmResult.getElements().size()+", "); // Models
		writer.append(oclpmResult.getExtraStats().get("Cases")+", "); // Cases
		writer.append(oclpmResult.getExtraStats().get("Replication Factor")+", "); // Replication Factor
		writer.append(oclpmResult.getExtraStats().get("Avg")+", "); // Avg
		writer.append(oclpmResult.getExtraStats().get("Min")+", "); // Min
		writer.append(oclpmResult.getExtraStats().get("1st Quartile")+", "); // 1st Quartile
		writer.append(oclpmResult.getExtraStats().get("Median")+", "); // Median
		writer.append(oclpmResult.getExtraStats().get("3rd Quartile")+", "); // 3rd Quartile
		writer.append(oclpmResult.getExtraStats().get("Max")+"\n"); // Max
	}

}
