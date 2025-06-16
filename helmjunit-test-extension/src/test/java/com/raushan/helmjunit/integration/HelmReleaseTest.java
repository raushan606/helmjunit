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

package com.raushan.helmjunit.integration;

import com.raushan.helmjunit.annotation.HelmChartTest;
import com.raushan.helmjunit.annotation.HelmResource;
import com.raushan.helmjunit.model.HelmRelease;
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
        assertEquals("redis-master", redis.serviceName());
        assertInstanceOf(Integer.class, redis.servicePort());
    }
}
