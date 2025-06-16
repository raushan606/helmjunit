/*
 * Copyright 2025 Raushan Kumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
