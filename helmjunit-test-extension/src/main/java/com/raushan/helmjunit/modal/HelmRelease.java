package com.raushan.helmjunit.modal;

public class HelmRelease {

    private final String releaseName;
    private final String namespace;
    private final String serviceName;
    private final int servicePort;

    public HelmRelease(String releaseName, String namespace, String serviceName, int servicePort) {
        this.releaseName = releaseName;
        this.namespace = namespace;
        this.serviceName = serviceName;
        this.servicePort = servicePort;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getServicePort() {
        return servicePort;
    }

    public String getServiceQualifiedName() {
        return "svc/" + serviceName;
    }
}
