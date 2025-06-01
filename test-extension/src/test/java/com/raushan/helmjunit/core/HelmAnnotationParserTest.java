package com.raushan.helmjunit.core;

import com.raushan.helmjunit.annotation.HelmResource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelmAnnotationParserTest {

    static class NoAnnotationClass {
        private Object someField;
    }

    static class InvalidFieldTypeClass {
        @HelmResource(chart = "bitnami/mysql")
        private String invalid;
    }

    static class EmptyValuesClass {
        @HelmResource(chart = "bitnami/postgresql", values = {})
        private Object pg;
    }

    static class DuplicateReleaseClass {
        @HelmResource(chart = "bitnami/nginx", releaseName = "same")
        private Object a;

        @HelmResource(chart = "bitnami/nginx", releaseName = "same")
        private Object b;
    }


    static class SingleChartTest {
        @HelmResource(chart = "bitnami/keycloak", values = {"replicaCount=1"})
        private Object keycloak;
    }

    static class MultipleChartsTest {
        @HelmResource(chart = "bitnami/nginx", values = {"replicaCount=1"}, releaseName = "nginx-release", namespace = "nginx-namespace")
        private Object nginx;

        @HelmResource(chart = "bitnami/postgresql", values = {"replicaCount=1", "version=14"}, releaseName = "postgres-release", namespace = "postgres-namespace")
        private Object postgresql;
    }

    @Test
    void testNoAnnotatedFields() {
        HelmAnnotationParser parser = new HelmAnnotationParser();
        List<HelmChartDescriptor> charts = parser.parseHelmAnnotations(NoAnnotationClass.class);
        assertTrue(charts.isEmpty());
    }

    @Test
    void testInvalidFieldTypeDoesNotCrash() {
        HelmAnnotationParser parser = new HelmAnnotationParser();
        List<HelmChartDescriptor> charts = parser.parseHelmAnnotations(InvalidFieldTypeClass.class);
        assertEquals(1, charts.size());
    }

    @Test
    void testEmptyValuesHandledGracefully() {
        HelmAnnotationParser parser = new HelmAnnotationParser();
        List<HelmChartDescriptor> charts = parser.parseHelmAnnotations(EmptyValuesClass.class);
        assertEquals(1, charts.size());
        assertTrue(charts.get(0).getValues().isEmpty());
    }

    @Test
    void testDuplicateReleaseNames() {
        HelmAnnotationParser parser = new HelmAnnotationParser();
        List<HelmChartDescriptor> charts = parser.parseHelmAnnotations(DuplicateReleaseClass.class);
        assertEquals(2, charts.size());
        assertEquals("same", charts.get(0).getReleaseName());
        assertEquals("same", charts.get(1).getReleaseName());
        //TODO: add conflict detection logic later
    }

    @Test
    void testSingleChartAnnotationParser() {
        HelmAnnotationParser helmAnnotationParser = new HelmAnnotationParser();
        List<HelmChartDescriptor> helmChartDescriptors = helmAnnotationParser.parseHelmAnnotations(SingleChartTest.class);
        assertEquals(1, helmChartDescriptors.size(), "Should parse one Helm chart descriptor");
        HelmChartDescriptor descriptor = helmChartDescriptors.get(0);
        assertEquals("bitnami/keycloak", descriptor.getChart(), "Chart name should match");
        assertEquals("singlecharttest-keycloak", descriptor.getReleaseName(), "Release name should be generated correctly");
        assertEquals("default", descriptor.getNamespace(), "Namespace should default to 'default'");
        assertEquals(List.of("replicaCount=1"), descriptor.getValues(), "Values should match the annotation");
    }

    @Test
    void testMultipleChartsAnnotationParser() {
        HelmAnnotationParser helmAnnotationParser = new HelmAnnotationParser();
        List<HelmChartDescriptor> helmChartDescriptors = helmAnnotationParser.parseHelmAnnotations(MultipleChartsTest.class);
        assertEquals(2, helmChartDescriptors.size(), "Should parse two Helm chart descriptors");

        HelmChartDescriptor nginxDescriptor = helmChartDescriptors.get(0);
        assertEquals("bitnami/nginx", nginxDescriptor.getChart(), "Nginx chart name should match");
        assertEquals("nginx-release", nginxDescriptor.getReleaseName(), "Nginx release name should match");
        assertEquals("nginx-namespace", nginxDescriptor.getNamespace(), "Nginx namespace should match");
        assertEquals(List.of("replicaCount=1"), nginxDescriptor.getValues(), "Nginx values should match the annotation");

        HelmChartDescriptor postgresDescriptor = helmChartDescriptors.get(1);
        assertEquals("bitnami/postgresql", postgresDescriptor.getChart(), "PostgreSQL chart name should match");
        assertEquals("postgres-release", postgresDescriptor.getReleaseName(), "PostgreSQL release name should match");
        assertEquals("postgres-namespace", postgresDescriptor.getNamespace(), "PostgreSQL namespace should match");
        assertEquals(List.of("replicaCount=1", "version=14"), postgresDescriptor.getValues(), "PostgreSQL values should match the annotation");
    }
}