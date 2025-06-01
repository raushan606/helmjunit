package com.raushan.helmjunit.helm;

import com.raushan.helmjunit.modal.HelmChartDescriptor;

import java.util.logging.Logger;

/**
 * HelmClient is a utility class that provides methods to interact with Helm,
 * specifically for installing and uninstalling Helm charts.
 * It handles retries and waits for resources to become ready after installation.
 */
public class HelmClient {

    Logger logger = Logger.getLogger(HelmClient.class.getName());

    /**
     * Installs a Helm chart based on the provided HelmChartDescriptor.
     * It retries the installation up to 3 times in case of failure.
     *
     * @param chartDescriptor the descriptor containing chart details
     * @throws Exception if the installation fails after retries
     */
    public void installChart(HelmChartDescriptor chartDescriptor) throws Exception {
        int maxRetries = 3;
        int attempt = 0;
        boolean success = false;

        while (attempt < maxRetries && !success) {
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command().add("helm");
                builder.command().add("install");
                builder.command().add(chartDescriptor.getReleaseName());
                builder.command().add(chartDescriptor.getChart());
                builder.command().add("--namespace");
                builder.command().add(chartDescriptor.getNamespace());
                builder.command().add("--create-namespace");
                builder.command().add("--wait");
                builder.command().add("--timeout");
                builder.command().add("120s");

                for (String value : chartDescriptor.getValues()) {
                    builder.command().add("--set");
                    builder.command().add(value);
                }
                logger.info("Executing Helm install command: " + String.join(" ", builder.command()));
                builder.inheritIO();
                Process process = builder.start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Helm install failed with exit code: " + exitCode);
                }

                waitForResourcesReady(chartDescriptor.getNamespace());
                success = true;

            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Helm install failed after " + maxRetries + " attempts", e);
                }
                Thread.sleep(2000); // wait before retry
            }
        }
    }

    /**
     * Waits for all resources in the specified namespace to become ready.
     * It checks the status of pods and waits until all are running and have no restarts.
     *
     * @param namespace the Kubernetes namespace to check
     * @throws Exception if the pods do not become ready within the timeout
     */
    private void waitForResourcesReady(String namespace) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
                "kubectl", "get", "pods", "-n", namespace, "--no-headers"
        );

        int maxWaitSeconds = 60;
        int waited = 0;
        while (waited < maxWaitSeconds) {
            Process process = builder.start();
            process.waitFor();

            String output = new String(process.getInputStream().readAllBytes());
            boolean allReady = output.lines().allMatch(line ->
                    line.contains("Running") && !line.contains("0/")
            );

            logger.info("Checking if all pods are ready in namespace: " + namespace);

            if (allReady) return;

            Thread.sleep(2000);
            waited += 2;
        }

        throw new RuntimeException("Timed out waiting for pods to become ready in namespace: " + namespace);
    }

    /**
     * Uninstalls a Helm chart based on the provided HelmChartDescriptor.
     * It retries the uninstallation up to 3 times in case of failure.
     *
     * @param chartDescriptor the descriptor containing chart details
     * @throws Exception if the uninstallation fails after retries
     */
    public void uninstallChart(HelmChartDescriptor chartDescriptor) throws Exception {
        int maxRetries = 3;
        int attempt = 0;
        boolean success = false;

        while (attempt < maxRetries && !success) {
            try {
                ProcessBuilder builder = new ProcessBuilder(
                        "helm", "uninstall",
                        chartDescriptor.getReleaseName(),
                        "--namespace", chartDescriptor.getNamespace()
                );
                builder.inheritIO();
                Process process = builder.start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Helm uninstall failed with exit code: " + exitCode);
                }

                confirmResourcesDeleted(chartDescriptor.getNamespace());
                success = true;
                deleteNamespace(chartDescriptor.getNamespace());
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Helm uninstall failed after " + maxRetries + " attempts", e);
                }
                Thread.sleep(2000); // backoff before retry
            }
        }
    }

    /**
     * Confirms that all resources in the specified namespace have been deleted.
     * It checks for any remaining pods and throws an exception if any are found.
     *
     * @param namespace the Kubernetes namespace to check
     * @throws Exception if any pods still exist in the namespace after uninstallation
     */
    private void confirmResourcesDeleted(String namespace) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
                "kubectl", "get", "pods", "-n", namespace, "--no-headers"
        );

        int maxWaitSeconds = 60;
        int waited = 0;
        while (waited < maxWaitSeconds) {
            Process process = builder.start();
            process.waitFor();

            String output = new String(process.getInputStream().readAllBytes()).trim();
            logger.info("Checking if all pods are deleted in namespace: " + namespace);
            if (output.isEmpty()) return;

            Thread.sleep(2000);
            waited += 2;
        }

        throw new RuntimeException("Timeout: some pods still exist in namespace: " + namespace);
    }

    /**
     * Deletes a Kubernetes namespace.
     * It waits for the namespace to be fully deleted after the command is executed.
     *
     * @param namespace the name of the namespace to delete
     * @throws Exception if the deletion fails or times out
     */
    private void deleteNamespace(String namespace) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("kubectl", "delete", "namespace", namespace);
        builder.inheritIO();
        Process process = builder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to delete namespace: " + namespace);
        }

        waitForNamespaceDeleted(namespace);
    }

    /**
     * Waits for a Kubernetes namespace to be fully deleted.
     * It checks the status of the namespace and waits until it is no longer present.
     *
     * @param namespace the name of the namespace to wait for deletion
     * @throws Exception if the namespace does not get deleted within the timeout
     */
    private void waitForNamespaceDeleted(String namespace) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("kubectl", "get", "namespace", namespace);

        int maxWaitSeconds = 60;
        int waited = 0;
        while (waited < maxWaitSeconds) {
            Process process = builder.start();
            int exit = process.waitFor();
            if (exit != 0) return; // kubectl get ns failed => it's deleted

            Thread.sleep(2000);
            waited += 2;
        }

        throw new RuntimeException("Namespace deletion timeout: " + namespace);
    }
}
