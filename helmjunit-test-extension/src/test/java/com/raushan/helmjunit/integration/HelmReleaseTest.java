package com.raushan.helmjunit.integration;

import com.raushan.helmjunit.annotation.HelmChartTest;
import com.raushan.helmjunit.annotation.HelmResource;
import com.raushan.helmjunit.modal.HelmRelease;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@HelmChartTest
public class HelmReleaseTest {

    @HelmResource(
            chart = "bitnami/nginx",
            releaseName = "nginx-port",
            namespace = "nginx-port-ns"
    )
    private HelmRelease nginx;

    @Test
    void testHandleInjectedCorrectly() {
        assertNotNull(nginx);
        assertEquals("nginx-port", nginx.releaseName());
        assertEquals("nginx-port-ns", nginx.namespace());
        assertEquals("nginx-port", nginx.serviceName());
        assertInstanceOf(Integer.class, nginx.servicePort());
    }

    @HelmResource(chart = "bitnami/redis", releaseName = "redis", namespace = "redis-ns")
    HelmRelease redis;

    @Test
    void shouldConnectToRedis() {
        assertNotNull(redis);
        assertEquals("redis", redis.releaseName());
        assertEquals("redis-ns", redis.namespace());
        assertEquals("redis", redis.serviceName());
        assertInstanceOf(Integer.class, redis.servicePort());
    }
}
