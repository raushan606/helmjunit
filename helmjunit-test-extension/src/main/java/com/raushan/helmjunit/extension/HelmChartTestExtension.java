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

package com.raushan.helmjunit.extension;

import com.raushan.helmjunit.annotation.HelmChartTest;
import com.raushan.helmjunit.core.HelmAnnotationParser;
import com.raushan.helmjunit.core.HelmReleaseInjector;
import com.raushan.helmjunit.helm.HelmClient;
import com.raushan.helmjunit.model.HelmChartDescriptor;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

/**
 * JUnit 5 extension for managing Helm chart lifecycle in tests.
 * This extension installs and uninstalls Helm charts before and after tests,
 * and injects release information into test instances.
 */
public class HelmChartTestExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    Logger logger = LoggerFactory.getLogger(HelmChartTestExtension.class.getName());

    private final HelmClient helmClient = new HelmClient();
    List<HelmChartDescriptor> charts;
    private final HelmReleaseInjector releaseInjector = new HelmReleaseInjector();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (isPerTestLifecycle(extensionContext)) {
            logger.info("ℹ️ Per-test lifecycle enabled. Helm chart will be installed before each test.");
        } else {
            logger.info("🍲 Preparing Helm chart test environment...");
            Class<?> testClass = extensionContext.getRequiredTestClass();
            charts = new HelmAnnotationParser().parseHelmAnnotations(testClass);
            for (HelmChartDescriptor chart : charts) {
                logger.info("🏗️ Installing Helm chart: {} with release name: {}", chart.chart(), chart.releaseName());
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
                logger.info("🚨 Uninstalling Helm chart: {} with release name: {}", chart.chart(), chart.releaseName());
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
                logger.info("📩 Installing Helm chart: {} with release name: {}", chart.chart(), chart.releaseName());
                helmClient.installChart(chart);
            }
        }
        Object testInstance = extensionContext.getRequiredTestInstance();
        if (nonNull(testInstance)) {
            for (HelmChartDescriptor chart : charts) {
                releaseInjector.injectInto(testInstance, chart);
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

    /**
     * Checks if the Helm chart test is configured to use per-test lifecycle.
     *
     * @param extensionContext the JUnit extension context
     * @return true if per-test lifecycle is enabled, false otherwise
     */
    private static boolean isPerTestLifecycle(ExtensionContext extensionContext) {
        return extensionContext.getRequiredTestClass().getAnnotation(HelmChartTest.class).perTestLifecycle();
    }
}
