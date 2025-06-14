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
