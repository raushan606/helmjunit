package com.raushan.helmjunit.model;

import java.util.List;

/**
 * Represents a Helm chart descriptor that contains information about a Helm chart,
 * including its name, release name, namespace, and values.
 * <p>
 * This class is used to encapsulate the details of a Helm chart for testing purposes.
 */
public record HelmChartDescriptor(String chart, String releaseName, String namespace, List<String> values,
                                  String valuesFile, boolean valuesFromClasspath) {

    /**
     * Constructs a HelmChartDescriptor with the specified chart name, release name, namespace, and values.
     * The valuesFile is set to null and valuesFromClasspath is set to false by default.
     *
     * @param chart       the name of the Helm chart
     * @param releaseName the name of the Helm release
     * @param namespace   the namespace in which the Helm chart will be deployed
     * @param values      the values to be passed to the Helm chart
     */
    public HelmChartDescriptor(String chart, String releaseName, String namespace, List<String> values) {
        this(chart, releaseName, namespace, values, null, false);
    }
}
