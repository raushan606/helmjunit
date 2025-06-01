package com.raushan.helmjunit.extension;

import com.raushan.helmjunit.core.HelmAnnotationParser;
import com.raushan.helmjunit.helm.HelmClient;
import com.raushan.helmjunit.modal.HelmChartDescriptor;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.logging.Logger;

public class HelmChartTestExtension implements BeforeAllCallback, AfterAllCallback {

    Logger logger = Logger.getLogger(HelmChartTestExtension.class.getName());

    private final HelmClient helmClient = new HelmClient();

    /**
     * @param extensionContext
     * @throws Exception
     */
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        logger.info("Preparing Helm chart test environment...");
        Class<?> testClass = extensionContext.getRequiredTestClass();
        List<HelmChartDescriptor> charts = new HelmAnnotationParser().parseHelmAnnotations(testClass);
        for (HelmChartDescriptor chart : charts) {
            logger.info("Installing Helm chart: " + chart.getChart() + " with release name: " + chart.getReleaseName());
            helmClient.installChart(chart);
        }
    }

    /**
     * @param extensionContext
     * @throws Exception
     */
    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        logger.info("Cleaning up Helm chart test environment...");
        Class<?> testClass = extensionContext.getRequiredTestClass();
        List<HelmChartDescriptor> charts = new HelmAnnotationParser().parseHelmAnnotations(testClass);
        for (HelmChartDescriptor chart : charts) {
            logger.info("Uninstalling Helm chart: " + chart.getChart() + " with release name: " + chart.getReleaseName());
            helmClient.uninstallChart(chart);
        }
    }
}
