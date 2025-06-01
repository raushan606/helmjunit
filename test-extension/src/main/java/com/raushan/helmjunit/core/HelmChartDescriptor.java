package com.raushan.helmjunit.core;

import java.util.List;

/**
 * Represents a Helm chart descriptor that contains information about a Helm chart,
 * including its name, release name, namespace, and values.
 * <p>
 * This class is used to encapsulate the details of a Helm chart for testing purposes.
 */
public class HelmChartDescriptor {
    private final String chart;
    private final String releaseName;
    private final String namespace;
    private final List<String> values;

    public HelmChartDescriptor(String chart, String releaseName, String namespace, List<String> values) {
        this.chart = chart;
        this.releaseName = releaseName;
        this.namespace = namespace;
        this.values = values;
    }

    public String getChart() {
        return chart;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<String> getValues() {
        return values;
    }
}
