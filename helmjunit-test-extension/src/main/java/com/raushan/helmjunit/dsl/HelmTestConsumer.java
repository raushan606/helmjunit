package com.raushan.helmjunit.dsl;

/**
 * Interface for a consumer that accepts a HelmTestEnvironment.
 * This functional interface is used to define a method that takes a HelmTestEnvironment
 * and performs some operations on it, typically in the context of a Helm test.
 * @see HelmTestEnvironment
 */
@FunctionalInterface
public interface HelmTestConsumer {
    void accept(HelmTestEnvironment builder) throws Exception;
}
