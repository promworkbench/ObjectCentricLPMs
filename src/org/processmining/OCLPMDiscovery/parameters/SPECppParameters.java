package org.processmining.OCLPMDiscovery.parameters;

import java.time.Duration;
import java.util.Objects;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.OCLPMDiscovery.specpp.CodeDefinedConfigurationSample;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.config.AlgorithmParameterConfig;
import org.processmining.specpp.config.ComponentConfig;
import org.processmining.specpp.config.ConfigFactory;
import org.processmining.specpp.config.DataExtractionParameters;
import org.processmining.specpp.config.PreProcessingParameters;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.config.parameters.DefaultParameters;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.preprocessing.orderings.Lexicographic;

public class SPECppParameters {
	
	private SPECppConfigBundle cfg;
	private ExecutionParameters executionParameters;
	
	// Parameters that are useful to change:
	// time limits
//	private Duration discoveryTimeLimit = Duration.ofMinutes(2);
	private Duration discoveryTimeLimit = Duration.ofSeconds(30);
//	private Duration totalTimeLimit = Duration.ofMinutes(3);
	private Duration totalTimeLimit = Duration.ofSeconds(60);
	//	tau
	private double tau = 1;
	//	permit negative markings during token replay (more freedom = more possibilities but less pruning = more time)
	private boolean permitNegativeMarkingsDuringReplay = false;
	
	// defaults
	public SPECppParameters() {		
		this.cfg = ConfigFactory.create(
				new PreProcessingParameters(new XEventNameClassifier(), true), 
				new DataExtractionParameters(Lexicographic.class), 
				CodeDefinedConfigurationSample.createComponentConfiguration(), 
				new DefaultParameters(), 
				CodeDefinedConfigurationSample.createSpecificParameters());

		// set parameter defaults defined in this class
		this.registerParameters();
		
		// disable post processing: LP-Based Implicit Place Removal
		//	commented it out in CodeDefinedConfigurationSample.java
	}
	
	// provide everything on your own
	public SPECppParameters( 
			PreProcessingParameters preProcessingParameters, 
			DataExtractionParameters dataExtractionParameters, 
			ComponentConfig componentConfig, 
			ProvidesParameters... providesParameters
			) {
		this.cfg = ConfigFactory.create(
				preProcessingParameters, 
				dataExtractionParameters, 
				componentConfig, 
				providesParameters);
	}
	
	

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SPECppParameters that = (SPECppParameters) o;
        return
        		this.getDiscoveryTimeLimit().equals(that.getDiscoveryTimeLimit())
        		&& this.getTotalTimeLimit().equals(that.getTotalTimeLimit())
        		&& this.getTau() == that.getTau()
        		&& this.isPermitNegativeMarkingsDuringReplay() == that.isPermitNegativeMarkingsDuringReplay()
        		;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        		this.getDiscoveryTimeLimit(),
        		this.getTotalTimeLimit(),
        		this.getTau(),
        		this.isPermitNegativeMarkingsDuringReplay()
        		);
    }
    
	@Override
    public String toString() {
        return "Discovery Time Limit: "+this.getDiscoveryTimeLimitAsMinutes()+" minutes\n"
        		+ "Total Time Limit: "+this.getTotalTimeLimitAsMinutes()+" minutes\n"
        		+ "Tau: "+this.getTau()+"\n"
        		+ "Permit Negative Markings During Replay: "+Boolean.toString(this.isPermitNegativeMarkingsDuringReplay())+"\n"
        		;
    }
	
	// call this after changing any parameters!
	public void registerParameters() {
		// copy variables into method, otherwise they are out of scope for the ParameterProvider
		double tau = this.tau;
		boolean permitNegativeMarkingsDuringReplay = this.permitNegativeMarkingsDuringReplay;
		
		ExecutionParameters exp = new ExecutionParameters(
				new ExecutionParameters.ExecutionTimeLimits(this.discoveryTimeLimit, null, this.totalTimeLimit), 
				ExecutionParameters.ParallelizationTarget.Moderate, 
				ExecutionParameters.PerformanceFocus.Balanced);
		this.setExecutionParameters(exp);
		
//        PlaceGeneratorParameters pgp = new PlaceGeneratorParameters(pc.depth < 0 ? Integer.MAX_VALUE : pc.depth, true, pc.respectWiring, false, false);
		
		AlgorithmParameterConfig currentAPC = this.cfg.getAlgorithmParameterConfig();
		
		ParameterProvider pp = new ParameterProvider() {
            @Override
            public void init() { //TODO does all this even work?
            	// combine default parameters with new parameters
                currentAPC.registerAlgorithmParameters(globalComponentSystem());
                
                System.out.println("SPECpp registering total time limit: "+exp.getTimeLimits().getTotalTimeLimit().getSeconds()+" seconds.");
                System.out.println("SPECpp registering discovery time limit: "+exp.getTimeLimits().getDiscoveryTimeLimit().getSeconds()+" seconds.");
            	globalComponentSystem()
                .provide(ParameterRequirements.EXECUTION_PARAMETERS.fulfilWithStatic(exp))
                .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(TauFitnessThresholds.tau(tau)))
                .provide(ParameterRequirements.REPLAY_COMPUTATION.fulfilWithStatic(ReplayComputationParameters.permitNegative(permitNegativeMarkingsDuringReplay)))
//				.provide(ParameterRequirements.EXTERNAL_INITIALIZATION.fulfilWithStatic(new ExternalInitializationParameters(pc.initiallyWireSelfLoops)))
//				.provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWithStatic(pc.supervisionSetting != ProMConfig.SupervisionSetting.Nothing ? SupervisionParameters.instrumentAll(false, logToFile) : SupervisionParameters.instrumentNone(false, logToFile)))
//				.provide(ParameterRequirements.IMPLICITNESS_TESTING.fulfilWithStatic(new ImplicitnessTestingParameters(pc.ciprVariant.bridge(), pc.implicitnessReplaySubLogRestriction)))
//				.provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(pgp))
//				.provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWithStatic(OutputPathParameters.getDefault()));
//                if (pc.compositionStrategy == ProMConfig.CompositionStrategy.TauDelta) {
//                    globalComponentSystem().provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(new DeltaParameters(pc.delta, pc.steepness)))
//                                           .provide(ParameterRequirements.DELTA_COMPOSER_PARAMETERS.fulfilWithStatic(DeltaComposerParameters.getDefault()));
//                }
//                if (pc.enforceHeuristicThreshold)
//                    globalComponentSystem().provide(ParameterRequirements.TREE_HEURISTIC_THRESHOLD.fulfilWithStatic(new TreeHeuristicThreshold(pc.heuristicThreshold, pc.heuristicThresholdRelation)))
				;
            }
        };

        this.cfg = ConfigFactory.create(this.cfg.getInputProcessingConfig(), cfg.getComponentConfig(), ConfigFactory.create(pp));
	}

	public SPECppConfigBundle getCfg() {
		return cfg;
	}
	
	public void setCfg(SPECppConfigBundle cfg) {
		this.cfg = cfg;
	}
	
	public void setTau(double tau) {
		this.tau = tau;
	}

	public Duration getDiscoveryTimeLimit() {
		return discoveryTimeLimit;
	}

	public void setDiscoveryTimeLimit(Duration discoveryTimeLimit) {
		this.discoveryTimeLimit = discoveryTimeLimit;
	}

	public Duration getTotalTimeLimit() {
		return totalTimeLimit;
	}

	public void setTotalTimeLimit(Duration totalTimeLimit) {
		this.totalTimeLimit = totalTimeLimit;
	}

	public boolean isPermitNegativeMarkingsDuringReplay() {
		return permitNegativeMarkingsDuringReplay;
	}

	public void setPermitNegativeMarkingsDuringReplay(boolean permitNegativeMarkingsDuringReplay) {
		this.permitNegativeMarkingsDuringReplay = permitNegativeMarkingsDuringReplay;
	}

	public double getTau() {
		return tau;
	}
	
	public double getTotalTimeLimitAsMinutes() {
		return this.totalTimeLimit.getSeconds()/60.0;
	}
	
	public double getDiscoveryTimeLimitAsMinutes() {
		return this.discoveryTimeLimit.getSeconds()/60.0;
	}

	public ExecutionParameters getExecutionParameters() {
		return executionParameters;
	}

	public void setExecutionParameters(ExecutionParameters executionParameters) {
		this.executionParameters = executionParameters;
	}
}
