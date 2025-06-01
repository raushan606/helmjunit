package com.raushan.helmjunit.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.logging.Logger;

public class HelmChartTestExtension implements BeforeAllCallback, AfterAllCallback {

    Logger logger = Logger.getLogger(HelmChartTestExtension.class.getName());

    /**
     * @param extensionContext
     * @throws Exception
     */
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        logger.info("Preparing Helm chart test environment...");
    }

    /**
     * @param extensionContext
     * @throws Exception
     */
    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        logger.info("Cleaning up Helm chart test environment...");
    }
}
