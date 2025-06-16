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
import com.raushan.helmjunit.model.HelmChartDescriptor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * HelmAnnotationParser is responsible for parsing Helm annotations from a given test class.
 * It extracts Helm chart descriptors based on the HelmResource annotations present in the class fields.
 */
public class HelmAnnotationParser {

    Logger logger = Logger.getLogger(HelmAnnotationParser.class.getName());

    /**
     * Parses the Helm annotations from the specified test class.
     * It looks for fields annotated with @HelmResource and creates HelmChartDescriptor objects
     * based on the annotation values.
     *
     * @param testClass the class to parse for Helm annotations
     * @return a list of HelmChartDescriptor objects representing the parsed Helm resources
     */
    public List<HelmChartDescriptor> parseHelmAnnotations(Class<?> testClass) {
        List<HelmChartDescriptor> helmChartDescriptors = new ArrayList<>();
        logger.info("📜 Parsing Helm annotations in class: " + testClass.getSimpleName());
        for (Field field : testClass.getDeclaredFields()) {
            HelmResource helmResource = field.getAnnotation(HelmResource.class);
            if (helmResource != null) {
                HelmChartDescriptor descriptor = new HelmChartDescriptor(
                        helmResource.chart(),
                        helmResource.releaseName().isEmpty() ? generateDefaultReleaseName(field) : helmResource.releaseName(),
                        helmResource.namespace(),
                        List.of(helmResource.values()), helmResource.valuesFile(), helmResource.valuesFromClassPath());
                helmChartDescriptors.add(descriptor);
            }
        }
        return helmChartDescriptors;
    }

    /**
     * Generates a default release name based on the field's declaring class and field name.
     * This is used when the releaseName is not specified in the HelmResource annotation.
     *
     * @param field the field for which to generate the default release name
     * @return a default release name in the format "ClassName-fieldName"
     */
    private String generateDefaultReleaseName(Field field) {
        return field.getDeclaringClass().getSimpleName().toLowerCase() + "-" + field.getName().toLowerCase();
    }
}
