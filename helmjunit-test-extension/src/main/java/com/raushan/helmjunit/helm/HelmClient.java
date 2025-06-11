package com.raushan.helmjunit.helm;

import com.raushan.helmjunit.model.HelmChartDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HelmClient is a utility class that provides methods to interact with Helm,
 * specifically for installing and uninstalling Helm charts.
 * It handles retries and waits for resources to become ready after installation.
 */
public class HelmClient {

    private static final Logger logger = LoggerFactory.getLogger(HelmClient.class.getName());

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

        while (!success) {
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command().add("helm");
                builder.command().add("install");
                builder.command().add(chartDescriptor.releaseName());
                builder.command().add(chartDescriptor.chart());
                builder.command().add("--namespace");
                builder.command().add(chartDescriptor.namespace());
                builder.command().add("--create-namespace");
                builder.command().add("--wait");
                builder.command().add("--timeout");
                builder.command().add("120s");

                for (String value : chartDescriptor.values()) {
                    builder.command().add("--set");
                    builder.command().add(value);
                }
                logger.info("Executing Helm install command: {}", String.join(" ", builder.command()));
                runAndLogProcess(builder, "Helm install: " + chartDescriptor.releaseName());

                waitForResourcesReady(chartDescriptor.namespace());
                waitForPodsReady(chartDescriptor.namespace());
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
        logger.info("Checking pod readiness in namespace: {}", namespace);
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

            if (allReady) {
                logger.info("😊 All pods in namespace '{}' are Running.", namespace);
                return;
            }

            Thread.sleep(2000);
            waited += 2;
        }

        throw new RuntimeException("❌ Timed out waiting for pods to become ready in namespace: " + namespace);
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

        while (!success) {
            try {
                ProcessBuilder builder = new ProcessBuilder(
                        "helm", "uninstall",
                        chartDescriptor.releaseName(),
                        "--namespace", chartDescriptor.namespace()
                );
                runAndLogProcess(builder, "Helm uninstall: " + chartDescriptor.releaseName());

                confirmResourcesDeleted(chartDescriptor.namespace());
                success = true;
                deleteNamespace(chartDescriptor.namespace());
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new RuntimeException("𐄂 Helm uninstall failed after " + maxRetries + " attempts", e);
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
            logger.info("Checking if all pods are deleted in namespace: {}", namespace);
            if (output.isEmpty()) return;

            Thread.sleep(2000);
            waited += 2;
        }

        throw new RuntimeException("⏱ Timeout: some pods still exist in namespace: " + namespace);
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
        runAndLogProcess(builder, "kubectl delete namespace: " + namespace);
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
        logger.info("Waiting for namespace '{}' to be deleted...", namespace);
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

        logger.warn("Namespace deletion timeout: {}", namespace);
        throw new RuntimeException("Namespace deletion timeout: " + namespace);
    }

    /**
     * Waits for all pods in the specified namespace to be ready.
     * It checks the status of the pods and waits until all are running and have no restarts.
     *
     * @param namespace the Kubernetes namespace to check
     * @throws Exception if the pods do not become ready within the timeout
     */
    private void waitForPodsReady(String namespace) throws Exception {
        logger.info("Waiting for pods in namespace '{}' to be Ready...", namespace);
        int maxWaitSeconds = 60;
        int waited = 0;

        while (waited < maxWaitSeconds) {
            ProcessBuilder pb = new ProcessBuilder(
                    "kubectl", "get", "pods", "-n", namespace, "--no-headers"
            );

            Process process = pb.start();
            process.waitFor();

            String output = new String(process.getInputStream().readAllBytes());
            if (output.trim().isEmpty()) {
                Thread.sleep(2000);
                waited += 2;
                continue;
            }

            boolean allReady = output.lines().allMatch(line ->
                    line.contains("Running") &&
                            !line.contains("0/") &&  // "0/1" means not ready
                            !line.contains("CrashLoopBackOff") &&
                            !line.contains("Error")
            );

            if (allReady) {
                logger.info("All pods in namespace [{}] are Ready.", namespace);
                return;
            }

            Thread.sleep(2000);
            waited += 2;
        }

        logger.warn("Timeout waiting for pods to become ready in namespace: {}", namespace);
        throw new RuntimeException("⏱️ Timeout waiting for pods to be ready in namespace: " + namespace);
    }

    private void runAndLogProcess(ProcessBuilder builder, String contextDescription) throws Exception {
        Process process = builder.start();

        String output = new String(process.getInputStream().readAllBytes());
        String error = new String(process.getErrorStream().readAllBytes());

        int exitCode = process.waitFor();

        if (!output.isBlank()) {
            logger.info("[{}] STDOUT:\n{}", contextDescription, output);
        }
        if (!error.isBlank()) {
            logger.error("[{}] STDERR:\n{}", contextDescription, error);
        }

        if (exitCode != 0) {
            throw new RuntimeException("[" + contextDescription + "] failed with exit code " + exitCode);
        }
    }
}
