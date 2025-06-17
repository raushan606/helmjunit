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

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to ensure that the Minikube environment is ready for Helm tests.
 * It checks if Docker is running, starts Minikube if it's not running,
 * and ensures that the Minikube ingress addon is enabled.
 */
public class MinikubeSupport {

    private static final Logger log = LoggerFactory.getLogger(MinikubeSupport.class);

    /**
     * Ensures that Docker is running, Minikube is started, and the ingress addon is enabled.
     */
    public static void ensureEnvironmentReady() {
        if (!isDockerRunning()) {
            throw new RuntimeException("❌ Docker is not running. Please start Docker.");
        }
        ensureMinikubeRunning();
        ensureIngressEnabled();
    }

    /**
     * Checks if Docker is running by executing the "docker info" command.
     *
     * @return true if Docker is running, false otherwise
     */
    private static boolean isDockerRunning() {
        try {
            Process process = new ProcessBuilder("docker", "info").start();
            boolean completed = process.waitFor(10, TimeUnit.SECONDS);
            if (!completed || process.exitValue() != 0) {
                log.warn("Docker is not running or not accessible.");
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("Failed to check Docker status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Ensures that Minikube is running by checking its status and starting it if necessary.
     * If Minikube is not running, it attempts to start it and waits for up to 90 seconds.
     */
    public static void ensureMinikubeRunning() {
        if (isRunning()) {
            log.info("✅ Minikube is already running.");
            return;
        }

        log.info("🚀 Starting Minikube...");
        try {
            Process process = new ProcessBuilder("minikube", "start")
                    .inheritIO()
                    .start();

            boolean completed = process.waitFor(90, TimeUnit.SECONDS);

            if (!completed || process.exitValue() != 0) {
                throw new RuntimeException("❌ Minikube start failed with exit code " + process.exitValue());
            }
            log.info("✅ Minikube started successfully.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to start Minikube", e);
        }
    }

    /**
     * Checks if Minikube is currently running by executing the "minikube status" command.
     *
     * @return true if Minikube is running, false otherwise
     */
    private static boolean isRunning() {
        try {
            Process process = new ProcessBuilder("minikube", "status", "-o", "json").start();
            InputStream input = process.getInputStream();
            String output = new String(input.readAllBytes());
            process.waitFor();
            return output.contains("\"Host\": \"Running\"") || output.contains("Running");
        } catch (Exception e) {
            log.warn("Failed to check minikube status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Ensures that the Minikube ingress addon is enabled.
     * If it is not enabled, it attempts to enable it and waits for up to 60 seconds.
     */
    private static void ensureIngressEnabled() {
        if (isIngressEnabled()) {
            log.info("✅ Minikube ingress addon is already enabled.");
            return;
        }
        log.info("🔧 Enabling Minikube ingress addon...");
        try {
            Process process = new ProcessBuilder("minikube", "addons", "enable", "ingress")
                    .inheritIO()
                    .start();
            boolean completed = process.waitFor(60, TimeUnit.SECONDS);
            if (!completed || process.exitValue() != 0) {
                throw new RuntimeException("❌ Failed to enable Minikube ingress addon.");
            }
            log.info("✅ Minikube ingress addon enabled.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to enable Minikube ingress addon", e);
        }
    }

    /**
     * Checks if the Minikube ingress addon is enabled by executing the "minikube addons list" command.
     *
     * @return true if the ingress addon is enabled, false otherwise
     */
    private static boolean isIngressEnabled() {
        try {
            Process process = new ProcessBuilder("minikube", "addons", "list", "-o", "json").start();
            InputStream input = process.getInputStream();
            String output = new String(input.readAllBytes());
            process.waitFor();
            return output.contains("\"ingress\":") && output.contains("\"Status\": \"enabled\"");
        } catch (Exception e) {
            log.warn("Failed to check ingress addon status: {}", e.getMessage());
            return false;
        }
    }
}