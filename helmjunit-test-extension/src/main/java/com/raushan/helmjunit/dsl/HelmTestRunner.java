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

package com.raushan.helmjunit.dsl;

import com.raushan.helmjunit.core.HelmReleaseInjector;
import com.raushan.helmjunit.helm.HelmClient;
import com.raushan.helmjunit.model.HelmChartDescriptor;
import com.raushan.helmjunit.model.HelmRelease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

/**
 * HelmTestRunner is a DSL for deploying and testing Helm charts in a Kubernetes environment.
 * It provides methods to configure the chart, release name, namespace, values file, and other parameters.
 * The run method accepts a HelmTestConsumer to execute tests against the deployed chart.
 */
public class HelmTestRunner {

    /**
     * Creates a new HelmTestBuilder instance for deploying a Helm chart.
     *
     * @return a HelmTestBuilder instance
     */
    public static HelmTestBuilder deploy() {
        return new HelmTestBuilderImpl();
    }


    private static class HelmTestBuilderImpl implements HelmTestBuilder {

        private final List<HelmChartDescriptor> descriptors = new ArrayList<>();

        private String chart;
        private String releaseName;
        private String namespace;
        private String valuesFile;
        private boolean valuesFromClasspath;
        private final List<String> values = new ArrayList<>();
        private boolean isMultiChartMode = false;

        /**
         * Sets the Helm chart to be deployed.
         * The chart name cannot be null or empty.
         *
         * @param chart the name of the Helm chart
         * @return this HelmTestBuilder instance
         */
        @Override
        public HelmTestBuilder chart(String chart) {
            this.chart = chart;
            if (isNull(chart) || chart.isBlank()) {
                throw new IllegalArgumentException("Chart name cannot be null or empty");
            }
            return this;
        }

        /**
         * Sets the release name for the Helm chart.
         * The release name cannot be null or empty.
         *
         * @param releaseName the name of the Helm release
         * @return this HelmTestBuilder instance
         */
        @Override
        public HelmTestBuilder releaseName(String releaseName) {
            this.releaseName = releaseName;
            if (isNull(releaseName) || releaseName.isBlank()) {
                throw new IllegalArgumentException("Release name cannot be null or empty");
            }
            return this;
        }

        /**
         * Sets the namespace for the Helm release.
         * If no namespace is provided, it defaults to "default".
         *
         * @param namespace the namespace for the Helm release
         * @return this HelmTestBuilder instance
         */
        @Override
        public HelmTestBuilder namespace(String namespace) {
            this.namespace = namespace;
            if (isNull(namespace) || namespace.isBlank()) {
                this.namespace = "default"; // Default namespace if not provided
            }
            return this;
        }

        /**
         * Sets the path to a values file to be used with the Helm chart.
         * This file can contain custom values to override defaults in the chart.
         *
         * @param path the path to the values file
         * @return this HelmTestBuilder instance
         */
        @Override
        public HelmTestBuilder valuesFile(String path) {
            this.valuesFile = path;
            return this;
        }

        /**
         * Configures whether to load values from the classpath resources.
         * If true, the values file will be resolved via ClassLoader.
         *
         * @param flag true to load values from classpath, false otherwise
         * @return this HelmTestBuilder instance
         */
        @Override
        public HelmTestBuilder valuesFromClasspath(boolean flag) {
            this.valuesFromClasspath = flag;
            return this;
        }

        /**
         * Adds a key-value pair to the list of values to be passed to the Helm chart.
         * This can be used to override specific values in the chart.
         *
         * @param keyValue a string in the format "key=value"
         * @return this HelmTestBuilder instance
         */
        @Override
        public HelmTestBuilder set(String keyValue) {
            this.values.add(keyValue);
            return this;
        }

        @Override
        public HelmTestBuilder add(Consumer<HelmTestBuilder> chartConfig) {
            HelmTestBuilderImpl nested = new HelmTestBuilderImpl();
            chartConfig.accept(nested);
            descriptors.add(nested.toDescriptor());
            this.isMultiChartMode = true;
            return this;
        }

        private HelmChartDescriptor toDescriptor() {
            return new HelmChartDescriptor(
                    chart, releaseName, namespace,
                    new ArrayList<>(values), valuesFile, valuesFromClasspath
            );
        }

        /**
         * Runs the Helm test with the configured parameters.
         * It deploys the Helm chart, executes the provided consumer, and then cleans up by uninstalling the chart.
         *
         * @param consumer a HelmTestConsumer that defines the test logic to be executed
         * @throws Exception if an error occurs during deployment or test execution
         */
        @Override
        public void run(HelmTestConsumer consumer) throws Exception {
            if (isMultiChartMode) {
                throw new IllegalStateException("Use runMulti(Map<String, HelmRelease> -> ...) for multi-chart tests");
            }

            HelmChartDescriptor descriptor = toDescriptor();
            HelmClient client = new HelmClient();
            client.installChart(descriptor);

            HelmRelease release = new HelmReleaseInjector().createHelmRelease(releaseName, namespace);
            HelmTestEnvironment env = new HelmTestEnvironment() {
                @Override
                public String getNamespace() {
                    return release.namespace();
                }

                @Override
                public String getReleaseName() {
                    return release.releaseName();
                }

                @Override
                public String getServiceName() {
                    return release.serviceName();
                }

                @Override
                public int getServicePort() {
                    return release.servicePort();
                }
            };

            try {
                consumer.accept(env);
            } finally {
                client.uninstallChart(descriptor);
            }
        }

        /**
         * Runs the Helm test in multi-chart mode.
         * It deploys multiple Helm charts, executes the provided consumer with a map of releases,
         * and then cleans up by uninstalling all charts.
         *
         * @param consumer a Consumer that processes a map of HelmRelease objects
         * @throws Exception if an error occurs during deployment or test execution
         */
        @Override
        public void runMulti(Consumer<Map<String, HelmRelease>> consumer) throws Exception {
            if (!isMultiChartMode) {
                descriptors.add(toDescriptor());
            }

            HelmClient client = new HelmClient();
            HelmReleaseInjector injector = new HelmReleaseInjector();
            Map<String, HelmRelease> releases = new HashMap<>();

            try {
                for (HelmChartDescriptor desc : descriptors) {
                    client.installChart(desc);
                    releases.put(desc.releaseName(), injector.createHelmRelease(desc.releaseName(), desc.namespace()));
                    Thread.sleep(2000); // Wait for the chart to be fully deployed
                }
                consumer.accept(releases);
            } finally {
                for (HelmChartDescriptor desc : descriptors) {
                    client.uninstallChart(desc);
                }
            }
        }
    }
}
