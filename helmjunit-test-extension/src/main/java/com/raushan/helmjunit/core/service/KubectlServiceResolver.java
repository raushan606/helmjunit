package com.raushan.helmjunit.core.service;


import com.raushan.helmjunit.core.ServiceResolver;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Uses kubectl commands to resolve service name and port for a Helm release.
 * This is useful when the Helm manifest parsing is not sufficient or fails.
 */
public class KubectlServiceResolver implements ServiceResolver {

    private static final Logger logger = Logger.getLogger(KubectlServiceResolver.class.getName());

    @Override
    public Optional<String> resolveServiceName(String releaseName, String namespace) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
                "kubectl", "get", "svc", "-n", namespace,
                "-l", "app.kubernetes.io/instance=" + releaseName,
                "-o", "jsonpath={.items[?(@.spec.clusterIP!=\"None\")].metadata.name}"
        );

        Process process = builder.start();
        InputStream input = process.getInputStream();
        String output = new String(input.readAllBytes()).trim();
        process.waitFor();

        if (output.isBlank()) return Optional.empty();

        List<String> names = List.of(output.split("\\s+"));
        return names.stream()
                .filter(name -> name.contains("master"))
                .findFirst()
                .or(() -> names.stream().findFirst());
    }

    @Override
    public int resolveServicePort(String serviceName, String namespace) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
                "kubectl", "get", "svc", serviceName, "-n", namespace,
                "-o", "jsonpath={.spec.ports[0].port}"
        );

        Process process = builder.start();
        InputStream input = process.getInputStream();
        String output = new String(input.readAllBytes()).trim();
        process.waitFor();

        if (output.isBlank()) {
            throw new RuntimeException("Unable to resolve port for service: " + serviceName);
        }

        return Integer.parseInt(output);
    }
}
