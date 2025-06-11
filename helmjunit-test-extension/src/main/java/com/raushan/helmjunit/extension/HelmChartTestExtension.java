package com.raushan.helmjunit.extension;

import com.raushan.helmjunit.annotation.HelmChartTest;
import com.raushan.helmjunit.core.HelmAnnotationParser;
import com.raushan.helmjunit.helm.HelmClient;
import com.raushan.helmjunit.model.HelmChartDescriptor;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

public class HelmChartTestExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    Logger logger = Logger.getLogger(HelmChartTestExtension.class.getName());

    private final HelmClient helmClient = new HelmClient();
    List<HelmChartDescriptor> charts;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (isPerTestLifecycle(extensionContext)) {
            logger.info("ℹ️ Per-test lifecycle enabled. Helm chart will be installed before each test.");
        } else {
            logger.info("🍲 Preparing Helm chart test environment...");
            Class<?> testClass = extensionContext.getRequiredTestClass();
            charts = new HelmAnnotationParser().parseHelmAnnotations(testClass);
            for (HelmChartDescriptor chart : charts) {
                logger.info("🏗️ Installing Helm chart: " + chart.chart() + " with release name: " + chart.releaseName());
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
            charts = new HelmAnnotationParser().parseHelmAnnotations(testClass);
            for (HelmChartDescriptor chart : charts) {
                logger.info("🚨 Uninstalling Helm chart: " + chart.chart() + " with release name: " + chart.releaseName());
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
                logger.info("📩 Installing Helm chart: " + chart.chart() + " with release name: " + chart.releaseName());
                helmClient.installChart(chart);
            }
        }
        Object testInstance = extensionContext.getRequiredTestInstance();
        if (nonNull(testInstance)) {
            for (HelmChartDescriptor chart : charts) {
                helmClient.injectHelmServiceHandle(testInstance, chart);
            }
        }

    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (isPerTestLifecycle(extensionContext)) {
            logger.info("🕯️ Cleaning up after Helm chart test...");
            List<HelmChartDescriptor> charts = new HelmAnnotationParser().parseHelmAnnotations(extensionContext.getRequiredTestClass());
            for (HelmChartDescriptor chart : charts) {
                logger.info("🚨 Uninstalling Helm chart: " + chart.chart() + " with release name: " + chart.releaseName());
                helmClient.uninstallChart(chart);
            }
        }
    }

    private static boolean isPerTestLifecycle(ExtensionContext extensionContext) {
        return extensionContext.getRequiredTestClass().getAnnotation(HelmChartTest.class).perTestLifecycle();
    }
}
