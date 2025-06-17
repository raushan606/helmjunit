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

import com.raushan.helmjunit.model.HelmRelease;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder interface for Helm test configurations.
 * This interface provides methods to set various parameters for Helm tests,
 * such as chart name, release name, namespace, values file, and key-value pairs.
 * It also includes a method to run the test with a specified consumer.
 */
public interface HelmTestBuilder {

    /**
     * Sets the Helm chart to be used for the test.
     *
     * @param chart the name of the Helm chart
     * @return the current HelmTestBuilder instance
     */
    HelmTestBuilder chart(String chart);

    /**
     * Sets the release name for the Helm test.
     *
     * @param releaseName the name of the Helm release
     * @return the current HelmTestBuilder instance
     */
    HelmTestBuilder releaseName(String releaseName);

    /**
     * Sets the namespace for the Helm test.
     *
     * @param namespace the Kubernetes namespace
     * @return the current HelmTestBuilder instance
     */
    HelmTestBuilder namespace(String namespace);

    /**
     * Sets the values file for the Helm test.
     * This file contains configuration values for the Helm chart.
     *
     * @param valuesFile the path to the values file
     * @return the current HelmTestBuilder instance
     */
    HelmTestBuilder valuesFile(String valuesFile);

    /**
     * Sets whether to load values from the classpath.
     * This is useful for loading default values defined in the classpath.
     *
     * @param valuesFromClasspath true to load values from classpath, false otherwise
     * @return the current HelmTestBuilder instance
     */
    HelmTestBuilder valuesFromClasspath(boolean valuesFromClasspath);

    /**
     * Sets a key-value pair for the Helm test.
     * This can be used to override specific values in the Helm chart.
     *
     * @param keyValue the key-value pair in the format "key=value"
     * @return the current HelmTestBuilder instance
     */
    HelmTestBuilder set(String keyValue);

    /**
     * Runs the Helm test with the specified consumer.
     * The consumer will receive the HelmRelease instance for further operations.
     *
     * @param consumer the consumer to execute with the HelmRelease
     * @throws Exception if an error occurs during the test execution
     */
    void run(HelmTestConsumer consumer) throws Exception;

    /**
     * Adds a chart configuration using a consumer.
     * This allows for more complex configurations to be applied to the Helm test.
     *
     * @param chartConfig the consumer that configures the HelmTestBuilder
     * @return the current HelmTestBuilder instance
     */
    HelmTestBuilder add(Consumer<HelmTestBuilder> chartConfig);

    /**
     * Runs the Helm test with a multi-release configuration.
     * This allows for testing multiple Helm releases in a single test run.
     *
     * @param testLogic the consumer that contains the logic to execute with the Helm releases
     * @throws Exception if an error occurs during the multi-release test execution
     */
    void runMulti(Consumer<Map<String, HelmRelease>> testLogic) throws Exception;
}
