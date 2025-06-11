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