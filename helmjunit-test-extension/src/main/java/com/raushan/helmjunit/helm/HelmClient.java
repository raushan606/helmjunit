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

package com.raushan.helmjunit.helm;

import com.raushan.helmjunit.model.HelmChartDescriptor;

import com.raushan.helmjunit.util.HelmCommandBuilder;
import com.raushan.helmjunit.util.KubernetesWaiter;
import com.raushan.helmjunit.util.ProcessExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * HelmClient is a utility class that provides methods to interact with Helm,
 * specifically for installing and uninstalling Helm charts.
 * It handles retries and waits for resources to become ready after installation.
 */
public class HelmClient {

    private static final Logger logger = LoggerFactory.getLogger(HelmClient.class.getName());
    private final KubernetesWaiter waiter = new KubernetesWaiter();

    /**
     * Installs a Helm chart based on the provided HelmChartDescriptor.
     * It retries the installation up to 3 times in case of failure.
     *
     * @param descriptor the descriptor containing chart details
     * @throws Exception if the installation fails after retries
     */
    public void installChart(HelmChartDescriptor descriptor) throws Exception {
        int maxRetries = 3;
        int attempt = 0;

        while (true) {
            try {
                List<String> command = HelmCommandBuilder.buildInstallCommand(descriptor);
                ProcessExecutor.run(command, "Helm install: " + descriptor.releaseName());

                waiter.waitForPodsReady(descriptor.namespace());
                return;
            } catch (Exception e) {
                attempt++;
                logger.error("❌ Helm install failed for release '{}'. Attempt {}/{}", descriptor.releaseName(), attempt, maxRetries, e);
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Helm install failed after " + maxRetries + " attempts", e);
                }
                Thread.sleep(2000);
            }
        }
    }

    /**
     * Uninstalls a Helm chart based on the provided HelmChartDescriptor.
     * It retries the uninstallation up to 3 times in case of failure.
     *
     * @param descriptor the descriptor containing chart details
     * @throws Exception if the uninstallation fails after retries
     */
    public void uninstallChart(HelmChartDescriptor descriptor) throws Exception {
        int maxRetries = 3;
        int attempt = 0;

        while (true) {
            try {
                List<String> command = HelmCommandBuilder.buildUninstallCommand(descriptor);
                ProcessExecutor.run(command, "Helm uninstall: " + descriptor.releaseName());

                waiter.confirmResourcesDeleted(descriptor.namespace());
                deleteNamespace(descriptor.namespace());
                return;
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new RuntimeException("❌ Helm uninstall failed after " + maxRetries + " attempts", e);
                }
                logger.error("❌ Helm uninstall failed for release '{}'. Attempt {}/{}", descriptor.releaseName(), attempt, maxRetries, e);
                Thread.sleep(2000);
            }
        }
    }

    /**
     * Deletes a Kubernetes namespace.
     * It waits for the namespace to be fully deleted after the command is executed.
     *
     * @param namespace the name of the namespace to delete
     * @throws Exception if the deletion fails or times out
     */
    private void deleteNamespace(String namespace) throws Exception {
        List<String> command = List.of("kubectl", "delete", "namespace", namespace);
        ProcessExecutor.run(command, "kubectl delete namespace: " + namespace);
        waiter.waitForNamespaceDeleted(namespace);
    }
}
