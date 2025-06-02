package com.raushan.helmjunit.modal;

public record HelmRelease(String releaseName, String namespace, String serviceName, int servicePort) {

    public String getServiceQualifiedName() {
        return "svc/" + serviceName;
    }
}
