package org.processmining.OCLPMDiscovery.visualization.components.eventattributesummaries;

import org.processmining.placebasedlpmdiscovery.utilityandcontext.eventattributesummary.*;

public class EventAttributeSummaryComponentFactory {
    public static EventAttributeSummaryComponent<?> getComponentForEventAttributeSummary(AttributeSummary<?, ?> eventAttributeSummary) {
        if (eventAttributeSummary instanceof ContinuousAttributeSummary) {
            ContinuousAttributeSummary summary = (ContinuousAttributeSummary) eventAttributeSummary;
            return new RangeEventAttributeSummaryComponent(summary);
        } else if (eventAttributeSummary instanceof DiscreteAttributeSummary) {
            DiscreteAttributeSummary summary = (DiscreteAttributeSummary) eventAttributeSummary;
            return new RangeEventAttributeSummaryComponent(summary);
        } else if (eventAttributeSummary instanceof TimestampAttributeSummary) {
            TimestampAttributeSummary summary = (TimestampAttributeSummary) eventAttributeSummary;
            return new RangeEventAttributeSummaryComponent(summary);
        } else if (eventAttributeSummary instanceof DistinctValuesAttributeSummary<?,?>) {
            DistinctValuesAttributeSummary<?,?> summary = (DistinctValuesAttributeSummary<?,?>) eventAttributeSummary;
            return new DistinctValuesEventAttributeSummaryComponent(summary);
        } else {
            throw new IllegalArgumentException("Component for the type: " + eventAttributeSummary.getClass() + " is not supported");
        }
    }
}
