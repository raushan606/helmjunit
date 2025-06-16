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

    HelmTestBuilder chart(String chart);

    HelmTestBuilder releaseName(String releaseName);

    HelmTestBuilder namespace(String namespace);

    HelmTestBuilder valuesFile(String valuesFile);

    HelmTestBuilder valuesFromClasspath(boolean valuesFromClasspath);

    HelmTestBuilder set(String keyValue);

    void run(HelmTestConsumer consumer) throws Exception;

    HelmTestBuilder add(Consumer<HelmTestBuilder> chartConfig);

    void runMulti(Consumer<Map<String, HelmRelease>> testLogic) throws Exception;
}
