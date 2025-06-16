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

package com.raushan.helmjunit.core;

import java.util.Optional;

/**
 * Strategy interface to resolve service name and port for a Helm release.
 */
public interface ServiceResolver {

    /**
     * Finds the preferred service name for a given Helm release in a namespace.
     *
     * @param releaseName the Helm release name
     * @param namespace   the Kubernetes namespace
     * @return optional service name
     * @throws Exception on failure
     */
    Optional<String> resolveServiceName(String releaseName, String namespace) throws Exception;

    /**
     * Retrieves the primary service port for a given service.
     *
     * @param serviceName the Kubernetes service name
     * @param namespace   the namespace
     * @return the port
     * @throws Exception on failure
     */
    int resolveServicePort(String serviceName, String namespace) throws Exception;

}

