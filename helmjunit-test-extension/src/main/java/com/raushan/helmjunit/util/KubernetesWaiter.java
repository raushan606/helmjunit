/*
 * Copyright 2025 Raushan Kumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.raushan.helmjunit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KubernetesWaiter is a utility class that provides methods to wait for Kubernetes resources
 * to become ready or to confirm their deletion.
 * It uses `kubectl` commands to check the status of pods and namespaces.
 */
public class KubernetesWaiter {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesWaiter.class);

    /**
     * Waits for all pods in the specified namespace to be ready.
     * It checks the status of the pods and waits until all are running and have no restarts.
     *
     * @param namespace the Kubernetes namespace to check
     * @throws Exception if the pods do not become ready within the timeout
     */
    public void waitForPodsReady(String namespace) throws Exception {
        logger.info("Waiting for pods in namespace '{}' to be Ready...", namespace);
        int maxWaitSeconds = 60;
        int waited = 0;

        while (waited < maxWaitSeconds) {
            ProcessBuilder pb = new ProcessBuilder("kubectl", "get", "pods", "-n", namespace, "--no-headers");
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
                            !line.contains("0/") &&
                            !line.contains("CrashLoopBackOff") &&
                            !line.contains("Error")
            );

            if (allReady) {
                logger.info("✅ All pods in namespace [{}] are Ready.", namespace);
                return;
            }

            Thread.sleep(2000);
            waited += 2;
        }

        throw new RuntimeException("⏱️ Timeout waiting for pods to be ready in namespace: " + namespace);
    }

    /**
     * Confirms that all resources in the specified namespace have been deleted.
     * It checks for any remaining pods and throws an exception if any are found.
     *
     * @param namespace the Kubernetes namespace to check
     * @throws Exception if any pods still exist in the namespace after uninstallation
     */
    public void confirmResourcesDeleted(String namespace) throws Exception {
        logger.info("Checking if all pods are deleted in namespace '{}'", namespace);
        ProcessBuilder builder = new ProcessBuilder("kubectl", "get", "pods", "-n", namespace, "--no-headers");

        int maxWaitSeconds = 60;
        int waited = 0;
        while (waited < maxWaitSeconds) {
            Process process = builder.start();
            process.waitFor();

            String output = new String(process.getInputStream().readAllBytes()).trim();
            if (output.isEmpty()) return;

            Thread.sleep(2000);
            waited += 2;
        }

        throw new RuntimeException("⏱ Timeout: some pods still exist in namespace: " + namespace);
    }

    /**
     * Waits for a Kubernetes namespace to be fully deleted.
     * It checks the status of the namespace and waits until it is no longer present.
     *
     * @param namespace the name of the namespace to wait for deletion
     * @throws Exception if the namespace does not get deleted within the timeout
     */
    public void waitForNamespaceDeleted(String namespace) throws Exception {
        logger.info("Waiting for namespace '{}' to be deleted...", namespace);
        ProcessBuilder builder = new ProcessBuilder("kubectl", "get", "namespace", namespace);

        int maxWaitSeconds = 60;
        int waited = 0;
        while (waited < maxWaitSeconds) {
            Process process = builder.start();
            int exit = process.waitFor();
            if (exit != 0) return; // Deleted

            Thread.sleep(2000);
            waited += 2;
        }

        logger.warn("⏱ Namespace deletion timeout: {}", namespace);
        throw new RuntimeException("Namespace deletion timeout: " + namespace);
    }
}