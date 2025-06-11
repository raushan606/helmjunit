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
     * @param namespace the Kubernetes namespace
     * @return optional service name
     * @throws Exception on failure
     */
    Optional<String> resolveServiceName(String releaseName, String namespace) throws Exception;

    /**
     * Retrieves the primary service port for a given service.
     *
     * @param serviceName the Kubernetes service name
     * @param namespace the namespace
     * @return the port
     * @throws Exception on failure
     */
    int resolveServicePort(String serviceName, String namespace) throws Exception;

}

