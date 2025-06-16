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

    String getNamespace();

    String getReleaseName();

    String getServiceName();

    int getServicePort();
}
