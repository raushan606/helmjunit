package com.raushan.helmjunit.dsl;

/**
 * Builder interface for Helm test configurations.
 * This interface provides methods to set various parameters for Helm tests,
 * such as chart name, release name, namespace, values file, and key-value pairs.
 * It also includes a method to run the test with a specified consumer.
 */
public interface HelmTestBuilder {

    HelmTestBuilder chart(String chart);

    HelmTestBuilder releaseName(String releaseName);

    HelmTestBuilder namespace(String namespace);

    HelmTestBuilder valuesFile(String valuesFile);

    HelmTestBuilder valuesFromClasspath(boolean valuesFromClasspath);

    HelmTestBuilder set(String keyValue);

    void run(HelmTestConsumer consumer) throws Exception;
}
