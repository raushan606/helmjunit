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

package com.raushan.helmjunit.core;

import com.raushan.helmjunit.annotation.HelmResource;
import com.raushan.helmjunit.core.service.ChainedServiceResolver;
import com.raushan.helmjunit.core.service.HelmManifestServiceResolver;
import com.raushan.helmjunit.core.service.KubectlServiceResolver;
import com.raushan.helmjunit.model.HelmChartDescriptor;
import com.raushan.helmjunit.model.HelmRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Optional;

public class HelmReleaseInjector {
    private static final Logger logger = LoggerFactory.getLogger(HelmReleaseInjector.class);

    private final ServiceResolver resolver = new ChainedServiceResolver(
            new HelmManifestServiceResolver(),
            new KubectlServiceResolver()
    );

    public HelmRelease createHelmRelease(String releaseName, String namespace) {
        try {
            logger.info("Resolving HelmRelease for '{}' in namespace '{}'", releaseName, namespace);

            Optional<String> serviceNameOpt = resolver.resolveServiceName(releaseName, namespace);
            String serviceName = serviceNameOpt.orElse(releaseName);
            logger.info("Resolved service name: {}", serviceName);

            int port = resolver.resolveServicePort(serviceName, namespace);
            logger.info("Resolved service port: {}", port);

            HelmRelease release = new HelmRelease(releaseName, namespace, serviceName, port);
            logger.debug("Created HelmRelease: {}", release);
            return release;
        } catch (Exception e) {
            logger.error("Failed to resolve HelmRelease for '{}' in namespace '{}'", releaseName, namespace, e);
            throw new RuntimeException("Failed to inject HelmRelease for: " + releaseName, e);
        }
    }

    public void injectInto(Object testInstance, HelmChartDescriptor chartDescriptor) {
        try {
            Class<?> testClass = testInstance.getClass();
            for (Field field : testClass.getDeclaredFields()) {
                if (field.getType().equals(HelmRelease.class)) {
                    HelmResource annotation = field.getAnnotation(HelmResource.class);
                    if (annotation != null && annotation.releaseName().equals(chartDescriptor.releaseName())) {
                        try {
                            field.setAccessible(true);
                            HelmRelease handle = createHelmRelease(chartDescriptor.releaseName(), chartDescriptor.namespace());
                            field.set(testInstance, handle);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Failed to inject HelmRelease", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject HelmRelease", e);
        }
    }
}
