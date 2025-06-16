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

package com.raushan.helmjunit.dsl;

import com.raushan.helmjunit.model.HelmRelease;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelmDslIntegrationTest {

    @Test
    void shouldInstallSingleChartWithDsl() throws Exception {
        HelmTestRunner.deploy()
                .chart("bitnami/redis")
                .releaseName("redis-dsl")
                .namespace("dsl-single")
                .set("auth.enabled=false")
                .run(env -> {
                    assertEquals("redis-dsl", env.getReleaseName());
                    assertEquals("dsl-single", env.getNamespace());
                    assertNotNull(env.getServiceName());
                    assertTrue(env.getServicePort() > 0);
                    System.out.printf("Redis => %s:%d%n", env.getServiceName(), env.getServicePort());
                });
    }

    @Test
    void shouldInstallMultipleChartsWithDsl() throws Exception {
        HelmTestRunner.deploy()
                .add(c -> c.chart("bitnami/redis")
                        .releaseName("redis-dsl")
                        .namespace("dsl-multi")
                        .set("auth.enabled=false"))
                .add(c -> c.chart("bitnami/postgresql")
                        .releaseName("postgres-dsl")
                        .namespace("dsl-multi")
                        .set("auth.postgresqlPassword=secret"))
                .runMulti((Map<String, HelmRelease> releases) -> {
                    assertEquals(2, releases.size());

                    HelmRelease redis = releases.get("redis-dsl");
                    HelmRelease postgres = releases.get("postgres-dsl");

                    assertNotNull(redis);
                    assertNotNull(postgres);

                    System.out.printf("Redis => %s:%d%n", redis.releaseName(), redis.servicePort());
                    System.out.printf("Postgres => %s:%d%n", postgres.serviceName(), postgres.servicePort());

                    assertTrue(redis.servicePort() > 0);
                    assertTrue(postgres.servicePort() > 0);
                });
    }
}
