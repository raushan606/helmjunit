package com.raushan.helmjunit.model;

public record HelmRelease(String releaseName, String namespace, String serviceName, int servicePort) {
}
