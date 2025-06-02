package com.raushan.helmjunit.modal;

import java.util.List;

/**
 * Represents a Helm chart descriptor that contains information about a Helm chart,
 * including its name, release name, namespace, and values.
 * <p>
 * This class is used to encapsulate the details of a Helm chart for testing purposes.
 */
public record HelmChartDescriptor(String chart, String releaseName, String namespace, List<String> values) {
}
