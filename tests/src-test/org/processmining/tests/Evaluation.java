package org.processmining.tests;

import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.OCLPMDiscovery.model.OCLPMResult;
import org.processmining.OCLPMDiscovery.model.TaggedPlaceSet;
import org.processmining.OCLPMDiscovery.parameters.CaseNotionStrategy;
import org.processmining.OCLPMDiscovery.parameters.ExternalObjectFlow;
import org.processmining.OCLPMDiscovery.parameters.OCLPMDiscoveryParameters;
import org.processmining.OCLPMDiscovery.parameters.PlaceCompletion;
import org.processmining.OCLPMDiscovery.plugins.imports.GraphImportPlugin;
import org.processmining.OCLPMDiscovery.plugins.imports.OCLPMResultImportPlugin;
import org.processmining.OCLPMDiscovery.plugins.imports.TaggedPlaceSetJsonImportPlugin;
import org.processmining.OCLPMDiscovery.plugins.mining.OCLPMDiscoveryPlugin;
import org.processmining.OCLPMDiscovery.utils.PlaceCompletionUtils;
import org.processmining.ocel.importers.JSONImporter;
import org.processmining.ocel.ocelobjects.OcelEventLog;

public class Evaluation {

	private static String basePath = 
			"C:\\Users\\Marvin\\OneDrive - Students RWTH Aachen University\\Uni\\Master-Thesis\\Event-Logs\\";
	private static String placeSetName = 
			"placeSet_tau09.jsontp";
	private static String graphName = 
			"graph.jgrapht";
	
	public static void main(String[] args) {
		
		try {
			
//			caseNotionStrategyTest("Evaluation_OrderManagement", "OrderManagementLog.jsonocel", "caseNotionTest.csv");
			postProcessingTest("Evaluation_Github", "Github", "result_CC.promoclpm", "postProcessingTest.csv");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished evaluation.");
	}
		
	private static void caseNotionStrategyTest(String folder, String ocelName, String csvName) throws IOException {
		
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
		
		CaseNotionStrategy[] strats = {
				CaseNotionStrategy.DUMMY,
				CaseNotionStrategy.PE_CONNECTED,
				CaseNotionStrategy.PE_LEADING_O2,
				CaseNotionStrategy.PE_LEADING_RELAXED_O2
				};
		
		// testing all strats for all types separately
		for (CaseNotionStrategy strat : strats) {
			for (String ot : parameters.getObjectTypesAll()) {
				parameters.setCaseNotionStrategy(strat);
				String rowName = strat.getName();
				if (CaseNotionStrategy.typeSelectionNeeded.contains(strat)) {
					parameters.setLeadingType(ot);
					rowName += " ot";
				}
				oclpmResult = OCLPMDiscoveryPlugin.mineOCLPMs(parameters, ocel, placeSet, graph);
				writeExtraStats(writer, oclpmResult, rowName);
				if (!CaseNotionStrategy.typeSelectionNeeded.contains(strat)) {
					break;
				}
			}			
		}
		
//		parameters.setCaseNotionStrategy(CaseNotionStrategy.DUMMY);
//		oclpmResult = OCLPMDiscoveryPlugin.mineOCLPMs(parameters, ocel, placeSet, graph);
//		writeExtraStats(writer, oclpmResult, "Single Case");
//		
//		parameters.setCaseNotionStrategy(CaseNotionStrategy.PE_CONNECTED);
//		oclpmResult = OCLPMDiscoveryPlugin.mineOCLPMs(parameters, ocel, placeSet, graph);
//		writeExtraStats(writer, oclpmResult, "Connected Components");
//		
//		parameters.setCaseNotionStrategy(CaseNotionStrategy.PE_LEADING_O2);
//		parameters.setLeadingType("customers");
//		oclpmResult = OCLPMDiscoveryPlugin.mineOCLPMs(parameters, ocel, placeSet, graph);
//		writeExtraStats(writer, oclpmResult, "LT-O2 customers");
//		
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
	
	private static void postProcessingTest (String folder, String logName, String resultName, String csvName) throws IOException {
		String resultPath = basePath + folder + "\\" + resultName;

		// imports
		OCLPMResult oclpmResultOriginal = OCLPMResultImportPlugin.importFromPath(resultPath);

		// prepare csv
		FileWriter writer = new FileWriter(basePath + folder + "\\" + csvName);
		writer.append("Log, Models, Places, PC:FewVariable, PC:FewVariableBetterFlow, PC:All, EOF:All, EOF:StartEnd, EOF:AllVisible, EOF:StartEndVisible\n");
		
		int numRuns = 20;
		long[] times = new long[numRuns]; 
		
		writer.append(logName+", "+oclpmResultOriginal.getElements().size()+", "+oclpmResultOriginal.getPlaceSet().size()+", ");
		
		PlaceCompletion[] pcList = {PlaceCompletion.FEWVARIABLE, PlaceCompletion.FEWVARIABLE_BETTERFLOW, PlaceCompletion.ALL};
		for (PlaceCompletion pc : pcList) {
			for (int i = 0; i<numRuns; i++) {
				OCLPMResult oclpmResult = oclpmResultOriginal.copyForPlaceCompletion();
				long start = System.currentTimeMillis();
				PlaceCompletionUtils.completePlaces(oclpmResult, pc);
				times[i] = System.currentTimeMillis()-start; 
			}
			long sum = 0;
	        for (long num : times) {
	            sum += num;
	        }
	        double average = (double) sum / times.length;
			writer.append(String.valueOf(average)+", ");
		}
		
		ExternalObjectFlow[] eofList = {ExternalObjectFlow.ALL, ExternalObjectFlow.START_END, ExternalObjectFlow.ALL_VISIBLE, ExternalObjectFlow.START_END_VISIBLE};
		for (ExternalObjectFlow eof : eofList) {
			for (int i = 0; i<numRuns; i++) {
				OCLPMResult oclpmResult = oclpmResultOriginal.copyForPlaceCompletion();
				PlaceCompletionUtils.completePlaces(oclpmResult, PlaceCompletion.FEWVARIABLE); //* testing eof for place completion: fewvariable
				long start = System.currentTimeMillis();
				oclpmResult.showExternalObjectFlow(eof, PlaceCompletion.FEWVARIABLE);
				times[i] = System.currentTimeMillis()-start; 
			}
			long sum = 0;
	        for (long num : times) {
	            sum += num;
	        }
	        double average = (double) sum / times.length;
			writer.append(String.valueOf(average)+", ");
		}
		writer.append("\n");
		writer.close();
	}

}
