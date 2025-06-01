package com.raushan.helmjunit.extension;

import com.raushan.helmjunit.annotation.HelmChartTest;
import com.raushan.helmjunit.core.HelmAnnotationParser;
import com.raushan.helmjunit.helm.HelmClient;
import com.raushan.helmjunit.modal.HelmChartDescriptor;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.logging.Logger;

public class HelmChartTestExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    Logger logger = Logger.getLogger(HelmChartTestExtension.class.getName());

    private final HelmClient helmClient = new HelmClient();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (isPerTestLifecycle(extensionContext)) {
            logger.info("ℹ️ Per-test lifecycle enabled. Helm chart will be installed before each test.");
        } else {
            logger.info("🍲 Preparing Helm chart test environment...");
            Class<?> testClass = extensionContext.getRequiredTestClass();
            List<HelmChartDescriptor> charts = new HelmAnnotationParser().parseHelmAnnotations(testClass);
            for (HelmChartDescriptor chart : charts) {
                logger.info("🏗️ Installing Helm chart: " + chart.getChart() + " with release name: " + chart.getReleaseName());
                helmClient.installChart(chart);
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (isPerTestLifecycle(extensionContext)) {
            logger.info("ℹ️ Per-test lifecycle enabled. Helm chart will be uninstalled after each test.");
        } else {

            logger.info("🧹 Cleaning up Helm chart test environment...");
            Class<?> testClass = extensionContext.getRequiredTestClass();
            List<HelmChartDescriptor> charts = new HelmAnnotationParser().parseHelmAnnotations(testClass);
            for (HelmChartDescriptor chart : charts) {
                logger.info("🚨 Uninstalling Helm chart: " + chart.getChart() + " with release name: " + chart.getReleaseName());
                helmClient.uninstallChart(chart);
            }
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        if (isPerTestLifecycle(extensionContext)) {
            logger.info("🪃 Preparing for Helm chart test...");
            List<HelmChartDescriptor> charts = new HelmAnnotationParser().parseHelmAnnotations(extensionContext.getRequiredTestClass());
            for (HelmChartDescriptor chart : charts) {
                logger.info("📩 Installing Helm chart: " + chart.getChart() + " with release name: " + chart.getReleaseName());
                helmClient.installChart(chart);
            }
        } else {
            logger.info("ℹ️ Per-test lifecycle not enabled. Helm chart will be installed only once before all tests.");
        }

    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (isPerTestLifecycle(extensionContext)) {
            logger.info("🕯️ Cleaning up after Helm chart test...");
            List<HelmChartDescriptor> charts = new HelmAnnotationParser().parseHelmAnnotations(extensionContext.getRequiredTestClass());
            for (HelmChartDescriptor chart : charts) {
                logger.info("🚨 Uninstalling Helm chart: " + chart.getChart() + " with release name: " + chart.getReleaseName());
                helmClient.uninstallChart(chart);
            }
        } else {
            logger.info("ℹ️ Per-test lifecycle not enabled. No cleanup after each test.");
        }
    }

    private static boolean isPerTestLifecycle(ExtensionContext extensionContext) {
        return extensionContext.getRequiredTestClass().getAnnotation(HelmChartTest.class).perTestLifecycle();
    }
}
