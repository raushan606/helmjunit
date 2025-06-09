package com.raushan.helmjunit.util;


import java.util.logging.Logger;

public class ClusterProvisioner {

    private static final Logger logger = Logger.getLogger(ClusterProvisioner.class.getName());

    public static void startMiniKube() {
        if (isMinikubeRunning()) {
            logger.info("Minikube is already running. 💨");
            return;
        }

        logger.info("Starting Minikube... 🏁");
        try {
            pullMinikubeImage();

            ProcessBuilder processBuilder = new ProcessBuilder("minikube", "start");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Minikube started successfully. ⏳");
                return;
            } else {
                logger.severe("❌ Failed to start Minikube. Exit code: " + exitCode);
            }
        } catch (Exception e) {
            logger.severe("❌ Error starting Minikube: " + e.getMessage());
        }
    }

    private static boolean isMinikubeRunning() {
        try {
            ProcessBuilder pb = new ProcessBuilder("kubectl", "cluster-info");
            Process process = pb.start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            logger.severe("❌ Error checking Minikube status: " + e.getMessage());
            return false;
        }
    }

    private static void pullMinikubeImage() {
        try {
            // Assumes docker driver is used
            Process check = new ProcessBuilder("docker", "images", "-q", "gcr.io/k8s-minikube/kicbase").start();
            String output = new String(check.getInputStream().readAllBytes());

            if (output.trim().isEmpty()) {
                System.out.println("📦 Pulling Minikube base image...");
                new ProcessBuilder("docker", "pull", "gcr.io/k8s-minikube/kicbase").inheritIO().start().waitFor();
            } else {
                System.out.println("📦 Minikube image already present.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not check or pull Minikube image: " + e.getMessage());
        }
    }

    public static void stopMiniKube() {
        if (!isMinikubeRunning()) {
            logger.info("Minikube is not running. No need to stop. ??");
            return;
        }

        logger.info("Stopping Minikube... ??");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("minikube", "stop");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Minikube stopped successfully. ✅");
            } else {
                logger.severe("❌ Failed to stop Minikube. Exit code: " + exitCode);
            }
        } catch (Exception e) {
            logger.severe("❌ Error stopping Minikube: " + e.getMessage());
        }
    }
}
