package com.raushan.helmjunit.example.dsl;

import com.raushan.helmjunit.dsl.HelmTestRunner;
import com.raushan.helmjunit.model.HelmRelease;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DslExamplesIT {

    @Test
    void redisAloneWithDsl() throws Exception {
        HelmTestRunner.deploy()
                .chart("bitnami/redis")
                .releaseName("redis-dsl")
                .namespace("showcase-ns")
                .set("architecture=standalone")
                .set("auth.enabled=false")
                .run(env -> {
                    assertNotNull(env.getServiceName());
                    assertTrue(env.getServicePort() > 0);
                    System.out.printf("✔ Redis => %s:%d%n", env.getServiceName(), env.getServicePort());
                });
    }

    @Test
    void redisAndPostgresTogetherWithDsl() throws Exception {
        HelmTestRunner.deploy()
                .add(c -> c.chart("bitnami/redis")
                        .releaseName("redis")
                        .namespace("showcase-multi")
                        .set("architecture=standalone")
                        .set("auth.enabled=false"))
                .add(c -> c.chart("bitnami/postgresql")
                        .releaseName("postgres")
                        .namespace("showcase-multi")
                        .set("auth.postgresPassword=secret"))
                .run((Map<String, HelmRelease> releases) -> {
                    assertEquals(2, releases.size());

                    HelmRelease redis = releases.get("redis");
                    HelmRelease postgres = releases.get("postgres");

                    System.out.printf("✔ Redis => %s:%d%n", redis.getServiceName(), redis.getServicePort());
                    System.out.printf("✔ Postgres => %s:%d%n", postgres.getServiceName(), postgres.getServicePort());
                });
    }
}
