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

/**
 * Interface representing the environment for Helm tests.
 * This interface provides methods to retrieve information about the Helm test environment,
 * such as the namespace, release name, service name, and service port.
 */
public interface HelmTestEnvironment {

    /**
     * Gets the Kubernetes namespace for the Helm test.
     *
     * @return the namespace as a String
     */
    String getNamespace();

    /**
     * Gets the release name for the Helm test.
     *
     * @return the release name as a String
     */
    String getReleaseName();

    /**
     * Gets the service name for the Helm test.
     *
     * @return the service name as a String
     */
    String getServiceName();

    /**
     * Gets the service port for the Helm test.
     *
     * @return the service port as an int
     */
    int getServicePort();
}
