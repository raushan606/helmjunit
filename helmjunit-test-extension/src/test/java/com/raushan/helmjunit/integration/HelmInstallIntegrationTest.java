package com.raushan.helmjunit.integration;

import com.raushan.helmjunit.annotation.HelmChartTest;
import com.raushan.helmjunit.annotation.HelmResource;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@HelmChartTest
public class HelmInstallIntegrationTest {

    @HelmResource(
            chart = "bitnami/nginx",
            releaseName = "nginx-test",
            namespace = "nginx-integration-test",
            values = {"replicaCount=2", "service.type=ClusterIP"}
    )
    private Object nginx;

    @Test
    void verifyNginxDeployment() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "kubectl", "get", "pods", "-n", "nginx-integration-test",
                "-l", "app.kubernetes.io/instance=nginx-test"
        );
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        String output = new BufferedReader(
                new InputStreamReader(process.getInputStream()))
                .lines()
                .reduce("", (a, b) -> a + "\n" + b);
        assertEquals(0, exitCode, "Failed to get pods for nginx deployment");
        assertTrue(output.contains("Running"), "Nginx pod is not running:\n" + output);
        verifyNginxIsReachableViaPortForward();
    }

    private void verifyNginxIsReachableViaPortForward() throws Exception {
        Process portForward = new ProcessBuilder(
                "kubectl", "port-forward", "svc/nginx-test", "8088:80", "-n", "nginx-integration-test"
        ).inheritIO().start();

        // Wait for port-forward to be established
        sleep(4000); // wait
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8088/"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response: " + response.statusCode() + "\n" + response.body());
            assertEquals(200, response.statusCode());

        } finally {
            // Stop port-forward
            portForward.destroy();
        }
    }
}
