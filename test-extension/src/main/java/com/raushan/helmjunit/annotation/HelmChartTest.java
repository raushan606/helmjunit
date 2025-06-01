package com.raushan.helmjunit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.raushan.helmjunit.extension.HelmChartTestExtension;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Annotation to mark a class as a Helm chart test.
 * <p>
 * This annotation can be used to indicate that the class is intended
 * to test Helm charts. It can be used in conjunction with the JUnit 5
 * test framework.
 * <p>
 * Example usage:
 * <pre>
 * &#64;HelmChartTest
 * public class MyHelmChartTest {
 *     // Test methods go here
 * }
 * </pre>
 * <p>
 * This annotation does not have any attributes and is used solely for
 * identification purposes.
 *
 * @see HelmResource
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(HelmChartTestExtension.class)
public @interface HelmChartTest {
}
