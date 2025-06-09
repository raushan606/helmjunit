package com.raushan.helmjunit.example;

import com.raushan.helmjunit.annotation.HelmChartTest;
import com.raushan.helmjunit.annotation.HelmResource;
import com.raushan.helmjunit.example.helper.HttpClientHelper;
import com.raushan.helmjunit.modal.HelmRelease;
import com.raushan.helmjunit.util.PortForwardManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@HelmChartTest
public class EchoServiceIT {

    @HelmResource(chart = "charts/echo-service", releaseName = "echo", namespace = "echo-ns")
    HelmRelease echo;

    @Test
    void shouldRespondWithHello() throws Exception {
        try (PortForwardManager pf = new PortForwardManager(
                "svc/" + echo.serviceName(),
                echo.servicePort(),
                echo.namespace()
        )) {
            String url = pf.getLocalUrl(echo.servicePort()); // e.g., http://localhost:12345
            String body = HttpClientHelper.get(url);
            assertEquals("hello", body.trim());
        }
    }
}