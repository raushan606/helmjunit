package com.raushan.helmjunit.dsl;

import org.junit.jupiter.api.Test;

public class HelmDslIntegrationTest {

    @Test
    void shouldInstallAndExposeServiceWithDsl() throws Exception {
        HelmTestRunner.deploy()
                .chart("helmjunit-test-extension")
                .releaseName("helmjunit-test-extension")
                .namespace("helmjunit-test-extension")
                .valuesFile("values.yaml")
                .set("service.type=NodePort")
                .run(builder -> {
                    System.out.println("Namespace: " + builder.getNamespace());
                    System.out.println("Release Name: " + builder.getReleaseName());
                    System.out.println("Service Name: " + builder.getServiceName());
                    System.out.println("Service Port: " + builder.getServicePort());
                });
    }
}
