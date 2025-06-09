package com.raushan.helmjunit.example;

import com.raushan.helmjunit.annotation.HelmChartTest;
import com.raushan.helmjunit.annotation.HelmResource;
import com.raushan.helmjunit.modal.HelmRelease;
import com.raushan.helmjunit.util.PortForwardManager;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@HelmChartTest
public class ConnectionIT {

    @HelmResource(chart = "bitnami/redis", releaseName = "redis", namespace = "connection-ns")
    HelmRelease redis;

    @HelmResource(chart = "bitnami/postgresql", releaseName = "postgres", namespace = "connection-ns")
    HelmRelease pg;

    @Test
    void shouldConnectToRedis() throws Exception {
        try (PortForwardManager pf = new PortForwardManager(
                "svc/" + redis.serviceName(),
                redis.servicePort(),
                redis.namespace()
        )) {
            String host = "localhost";
            int port = redis.servicePort();

            try (Jedis jedis = new Jedis(host, port)) {
                jedis.set("foo", "bar");
                String value = jedis.get("foo");
                assertEquals("bar", value);
            }
        }
    }

    @Test
    void shouldConnectToPostgres() throws Exception {
        try (PortForwardManager pf = new PortForwardManager(
                "svc/" + pg.serviceName(),
                pg.servicePort(),
                pg.namespace()
        )) {
            String jdbcUrl = "jdbc:postgresql://localhost:" + pg.servicePort() + "/postgres";
            try (Connection conn = DriverManager.getConnection(jdbcUrl, "postgres", "postgres")) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("CREATE TABLE IF NOT EXISTS test (id SERIAL PRIMARY KEY, name TEXT);");
                    stmt.execute("INSERT INTO test(name) VALUES ('HelmJUnit');");
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test;");
                    assertTrue(rs.next());
                    assertTrue(rs.getInt(1) >= 1);
                }
            }
        }
    }
}