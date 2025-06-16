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

package com.raushan.helmjunit.core.service;

import com.raushan.helmjunit.core.ServiceResolver;

import java.util.List;
import java.util.Optional;

/**
 * A composite service resolver that chains multiple resolvers together.
 * It will try each resolver in order until one successfully resolves the service name or port.
 */
public class ChainedServiceResolver implements ServiceResolver {

    private final List<ServiceResolver> resolvers;

    public ChainedServiceResolver(ServiceResolver... resolvers) {
        this.resolvers = List.of(resolvers);
    }

    @Override
    public Optional<String> resolveServiceName(String releaseName, String namespace) throws Exception {
        for (ServiceResolver resolver : resolvers) {
            Optional<String> name = resolver.resolveServiceName(releaseName, namespace);
            if (name.isPresent()) return name;
        }
        return Optional.empty();
    }

    @Override
    public int resolveServicePort(String serviceName, String namespace) throws Exception {
        for (ServiceResolver resolver : resolvers) {
            try {
                return resolver.resolveServicePort(serviceName, namespace);
            } catch (Exception ignored) {
                // try next
            }
        }
        throw new RuntimeException("Unable to resolve port for service: " + serviceName);
    }
}